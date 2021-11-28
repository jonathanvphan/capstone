from __future__ import division
from google.cloud import speech
from six.moves import queue
import re
import sys
import pyaudio
import io
import serial
import time

# App settings
USERNAME = "default name"
APP_MODE = True
TRAIN_ON = True
WALK_ON = True
FIRE_ON = True
WARNING_ON = True
ALERT_ON = True
DANGER_ON = True

# Audio recording parameters
STREAMING_LIMIT = 240000  # 4 minutes
SAMPLE_RATE = 16000
CHUNK_SIZE = int(SAMPLE_RATE / 10)  # 100ms

def get_current_time():
    """Return Current Time in MS."""

    return int(round(time.time() * 1000))


class ResumableMicrophoneStream:
    """Opens a recording stream as a generator yielding the audio chunks."""

    def __init__(self, rate, chunk_size, phone, bluetooth):
        self.phone = phone
        self.bluetooth = bluetooth
        self._rate = rate
        self.chunk_size = chunk_size
        self._num_channels = 1
        self._buff = queue.Queue()
        self.closed = True
        self.start_time = get_current_time()
        self.restart_counter = 0
        self.audio_input = []
        self.last_audio_input = []
        self.result_end_time = 0
        self.is_final_end_time = 0
        self.final_request_end_time = 0
        self.bridging_offset = 0
        self.last_transcript_was_final = False
        self.new_stream = True
        self._audio_interface = pyaudio.PyAudio()

        mic_index = 0

        info = self._audio_interface.get_host_api_info_by_index(0)
        numdevices = info.get('deviceCount')
        for i in range(0, numdevices):
                if (self._audio_interface.get_device_info_by_host_api_device_index(0, i).get('maxInputChannels')) > 0:
                    if "Array" in self._audio_interface.get_device_info_by_host_api_device_index(0, i).get('name'):
                        mic_index = i
                        break
                    elif "Logitech" in self._audio_interface.get_device_info_by_host_api_device_index(0, i).get('name'):
                        mic_index = i
                    #print ("Input Device id ", i, " - ", self._audio_interface.get_device_info_by_host_api_device_index(0, i).get('name'))

        if mic_index == 0:
            print("Using built-in mic")
        else:
            print("Using " + str(self._audio_interface.get_device_info_by_host_api_device_index(0, mic_index).get('name')))

        self._audio_stream = self._audio_interface.open(
            format=pyaudio.paInt16,
            channels=self._num_channels,
            rate=self._rate,
            input=True,
            input_device_index=mic_index,
            frames_per_buffer=self.chunk_size,
            # Run the audio stream asynchronously to fill the buffer object.
            # This is necessary so that the input device's buffer doesn't
            # overflow while the calling thread makes network requests, etc.
            stream_callback=self._fill_buffer,
        )

    def __enter__(self):

        self.closed = False
        return self

    def __exit__(self, type, value, traceback):

        self._audio_stream.stop_stream()
        self._audio_stream.close()
        self.closed = True
        # Signal the generator to terminate so that the client's
        # streaming_recognize method will not block the process termination.
        self._buff.put(None)
        self._audio_interface.terminate()

    def _fill_buffer(self, in_data, *args, **kwargs):
        """Continuously collect data from the audio stream, into the buffer."""

        self._buff.put(in_data)
        return None, pyaudio.paContinue

    def generator(self):
        """Stream Audio from microphone to API and to local buffer"""

        while not self.closed:
            checkPhoneCommands(self.phone, self.bluetooth)
            data = []

            if self.new_stream and self.last_audio_input:

                chunk_time = STREAMING_LIMIT / len(self.last_audio_input)

                if chunk_time != 0:

                    if self.bridging_offset < 0:
                        self.bridging_offset = 0

                    if self.bridging_offset > self.final_request_end_time:
                        self.bridging_offset = self.final_request_end_time

                    chunks_from_ms = round(
                        (self.final_request_end_time - self.bridging_offset)
                        / chunk_time
                    )

                    self.bridging_offset = round(
                        (len(self.last_audio_input) - chunks_from_ms) * chunk_time
                    )

                    for i in range(chunks_from_ms, len(self.last_audio_input)):
                        data.append(self.last_audio_input[i])

                self.new_stream = False

            # Use a blocking get() to ensure there's at least one chunk of
            # data, and stop iteration if the chunk is None, indicating the
            # end of the audio stream.
            chunk = self._buff.get()
            self.audio_input.append(chunk)

            if chunk is None:
                return
            data.append(chunk)
            # Now consume whatever other data's still buffered.
            while True:
                try:
                    chunk = self._buff.get(block=False)

                    if chunk is None:
                        return
                    data.append(chunk)
                    self.audio_input.append(chunk)

                except queue.Empty:
                    break

            yield b"".join(data)


def listen_print_loop(responses, stream, bluetooth, phone):
    """Iterates through server responses and prints them.

    The responses passed is a generator that will block until a response
    is provided by the server.

    Each response may contain multiple results, and each result may contain
    multiple alternatives; for details, see https://goo.gl/tjCPAU.  Here we
    print only the transcription for the top alternative of the top result.

    In this case, responses are provided for interim results as well. If the
    response is an interim one, print a line feed at the end of it, to allow
    the next result to overwrite it, until the response is a final one. For the
    final one, print a newline to preserve the finalized transcription.
    """
    for response in responses:
        if get_current_time() - stream.start_time > STREAMING_LIMIT:
            stream.start_time = get_current_time()
            break

        if not response.results:
            continue

        result = response.results[0]

        if not result.alternatives:
            continue

        transcript = result.alternatives[0].transcript

        result_seconds = 0
        result_micros = 0

        if result.result_end_time.seconds:
            result_seconds = result.result_end_time.seconds

        if result.result_end_time.microseconds:
            result_micros = result.result_end_time.microseconds

        stream.result_end_time = int((result_seconds * 1000) + (result_micros / 1000))

        corrected_time = (
            stream.result_end_time
            - stream.bridging_offset
            + (STREAMING_LIMIT * stream.restart_counter)
        )
        # Display interim results, but with a carriage return at the end of the
        # line, so subsequent lines will overwrite them.

        if result.is_final:
            sys.stdout.write(str(corrected_time) + ": " + transcript + "\n")

            stream.is_final_end_time = stream.result_end_time
            stream.last_transcript_was_final = True

            to_send = transcript
            
            if APP_MODE == False:
                alertRecognition(to_send, bluetooth)
                nameRecognition(USERNAME, to_send, bluetooth)

            check = 0
            check += changeFont(to_send, bluetooth)

            if check == 0 and APP_MODE == True:
                sendToBluetooth(to_send + "\n", bluetooth)

             #Exit recognition if any of the transcribed phrases could be
             #one of our keywords.
            if re.search(r"\b(exit now please)\b", transcript, re.I):
                sys.stdout.write("Exiting...\n")
                stream.closed = True
                break

        else:
            sys.stdout.write(str(corrected_time) + ": " + transcript + "\r")

            stream.last_transcript_was_final = False

def connectToBluetooth():
    bluetooth = serial.Serial('COM6', 4800, parity=serial.PARITY_NONE, stopbits=serial.STOPBITS_ONE, timeout=3)

    if bluetooth.isOpen():
        print("Bluetooth port open")
        print("Timeout: " + str(bluetooth.timeout))
    else:
        print("Bluetooth not connected")

    bluetooth.write(b"Bluetooth connected\n")

    checkBluetoothConnection(bluetooth)
    return(bluetooth)

def sendToBluetooth(to_send, bluetooth):
    to_send = to_send.lstrip(" ")
    bytes_to_send = bytes(to_send, 'utf-8')
    if bluetooth.isOpen():
        bluetooth.write(bytes_to_send)
    #checkBluetoothConnection(bluetooth)

def checkBluetoothConnection(bluetooth):
    counter = 5
    if bluetooth.isOpen():
        for retries in range(counter):

            ACK = bluetooth.read()
            print("Response: " + str(ACK))

            if ACK == b'A':
                print("Sent successfully\n")
                bluetooth.reset_input_buffer()
                break

            elif ACK != b'A':
                print("Retry: " + str(retries+1))

                if retries == 4:
                    print("Bluetooth disconnected, retrying")
                    bluetooth.close()
                    time.sleep(2)

                    bluetooth = serial.Serial('COM6', 4800, parity=serial.PARITY_NONE, stopbits=serial.STOPBITS_ONE, timeout=3)

                    bluetooth.write(b' ')
                    checkBluetoothConnection(bluetooth)
                
                if bluetooth.isOpen():
                    bluetooth.write(b' ')

    else:
        print("Bluetooth disconnected, retrying")
        bluetooth.close()
        time.sleep(2)

        bluetooth = serial.Serial('COM6', 4800, parity=serial.PARITY_NONE, stopbits=serial.STOPBITS_ONE, timeout=0.1)

        if bluetooth.isOpen():
            bluetooth.write(b'')
        checkBluetoothConnection(bluetooth)

def checkPhoneCommands(phone, bluetooth):
    if phone.in_waiting > 0:
        phone_command = phone.read(phone.in_waiting).decode("utf-8")
        print(phone_command)
        if '!FONT' in phone_command:
            changeFont(phone_command, bluetooth)
        if '!CHANGE' in phone_command:  
            changeName(phone_command, bluetooth)
        if '!ALERT' in phone_command:
            conversationAlertMode(False, bluetooth)
        if '!CONVO' in phone_command:
            conversationAlertMode(True, bluetooth)
        if '!TRAIN' in phone_command:
            if 'ON' in phone_command:
                TRAIN_ON = True
            if 'OFF' in phone_command:
                TRAIN_ON = False
        if '!WALK' in phone_command:
            if 'ON' in phone_command:
                WALK_ON = True
            if 'OFF' in phone_command:
                WALK_ON = False
        if '!FIRE' in phone_command:
            if 'ON' in phone_command:
                FIRE_ON = True
            if 'OFF' in phone_command:
                FIRE_ON = False
        if '!WARNING' in phone_command:
            if 'ON' in phone_command:
                WARNING_ON = True
            if 'OFF' in phone_command:
                WARNING_ON = False
        if '!ALERT' in phone_command:
            if 'ON' in phone_command:
                ALERT_ON = True
            if 'OFF' in phone_command:
                ALERT_ON = False
        if '!DANGER' in phone_command:
            if 'ON' in phone_command:
                DANGER_ON = True
            if 'OFF' in phone_command:
                DANGER_ON = False

def nameRecognition(name, transcript, bluetooth):
    if name in transcript:
        print("****NAME RECOGNIZED****")
        sendToBluetooth("Your name has been mentioned\n", bluetooth)

def alertRecognition(transcript, bluetooth):
    transcript = transcript.lower()
    if 'train' in transcript and TRAIN_ON == True:
        print("Train has been heard")
        sendToBluetooth("Train: " + transcript, bluetooth)
    if 'walk' in transcript and WALK_ON == True:
        print("Walk has been heard")
        sendToBluetooth("Walk has been heard\n", bluetooth)
    if 'fire' in transcript and FIRE_ON == True:
        print("Fire has been heard")
        sendToBluetooth("Fire has been heard\n", bluetooth)
    if 'warning' in transcript and WARNING_ON == True:
        print("Warning has been heard")
        sendToBluetooth("Warning has been heard\n", bluetooth)
    if 'alert' in transcript and ALERT_ON == True:
        print("Alert has been heard")
        sendToBluetooth("Alert has been heard\n", bluetooth)
    if 'danger' in transcript and DANGER_ON == True:
        print("Danger has been heard")
        sendToBluetooth("Danger has been heard\n", bluetooth)

def conversationAlertMode(mode, bluetooth):
    global APP_MODE
    APP_MODE = mode
    if mode == True:
        sendToBluetooth("Set to Conversation Mode\n", bluetooth)
        print("Set to Conversation Mode")
    elif mode == False:
        sendToBluetooth("Set to Alert Mode\n", bluetooth)
        print("Set to Alert Mode")

def changeName(name, bluetooth):
    global USERNAME
    name = name.replace('!CHANGE', '', 1)
    USERNAME = name
    print("Changing name to " + name)
    sendToBluetooth("Your name has been set to " + str(USERNAME) + "\n", bluetooth)

def changeFont(transcript, bluetooth):
    if "size 1" in transcript or '!FONT1' in transcript:
        print("Setting font size to " + str(1))
        sendToBluetooth("FONT"+str(1), bluetooth)
    elif "size 2" in transcript or '!FONT2' in transcript:
        print("Setting font size to " + str(2))
        sendToBluetooth("FONT"+str(2), bluetooth)
    elif "size 3" in transcript or '!FONT3' in transcript:
        print("Setting font size to " + str(3))
        sendToBluetooth("FONT"+str(3), bluetooth)
    else:
        return 0
    return 1

def connectToPhone():
    phone = serial.Serial('COM8', 38400, parity=serial.PARITY_NONE, stopbits=serial.STOPBITS_ONE, timeout=0)

    if phone.isOpen():
        print("Phone port open")
        print("Timeout: " + str(phone.timeout))
    else:
        print("Phone not connected")

    phone.write(b"Phone connected\n")
    print("Phone connected successfully\n")

    return(phone)

def main():
    """start bidirectional streaming from microphone input to speech API"""

    #bluetooth = 1
    #phone = 1
    bluetooth = connectToBluetooth()
    phone = connectToPhone()

    client = speech.SpeechClient.from_service_account_json('credentials.json')
    config = speech.RecognitionConfig(
        encoding=speech.RecognitionConfig.AudioEncoding.LINEAR16,
        sample_rate_hertz=SAMPLE_RATE,
        language_code="en-US",
        max_alternatives=1,
    )

    streaming_config = speech.StreamingRecognitionConfig(
        config=config, interim_results=True
    )

    mic_manager = ResumableMicrophoneStream(SAMPLE_RATE, CHUNK_SIZE, phone, bluetooth)
    print("Mic manager chunk size: " + str(mic_manager.chunk_size))
    sys.stdout.write('\nListening, say "Exit now please" to stop.\n\n')
    sys.stdout.write("End (ms)       Transcript Results/Status\n")
    sys.stdout.write("=====================================================\n")

    with mic_manager as stream:

        while not stream.closed:
            sys.stdout.write(
                "\n" + str(STREAMING_LIMIT * stream.restart_counter) + ": NEW REQUEST\n"
            )

            stream.audio_input = []
            audio_generator = stream.generator()

            requests = (
                speech.StreamingRecognizeRequest(audio_content=content)
                for content in audio_generator
            )
            responses = client.streaming_recognize(streaming_config, requests)

            # Now, put the transcription responses to use.
            listen_print_loop(responses, stream, bluetooth, phone)

            if stream.result_end_time > 0:
                stream.final_request_end_time = stream.is_final_end_time
            stream.result_end_time = 0
            stream.last_audio_input = []
            stream.last_audio_input = stream.audio_input
            stream.audio_input = []
            stream.restart_counter = stream.restart_counter + 1

            if not stream.last_transcript_was_final:
                sys.stdout.write("\n")
            stream.new_stream = True
   
    bluetooth.close()
    phone.close()

if __name__ == "__main__":
    main()