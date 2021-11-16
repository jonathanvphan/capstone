from __future__ import division
from google.cloud import speech
from six.moves import queue
import re
import sys
import pyaudio
import io
import serial
import time

# Audio recording parameters
STREAMING_LIMIT = 240000  # 4 minutes
SAMPLE_RATE = 16000
CHUNK_SIZE = int(SAMPLE_RATE / 10)  # 100ms

def get_current_time():
    """Return Current Time in MS."""

    return int(round(time.time() * 1000))


class ResumableMicrophoneStream:
    """Opens a recording stream as a generator yielding the audio chunks."""

    def __init__(self, rate, chunk_size):
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
                    if "Logitech" in self._audio_interface.get_device_info_by_host_api_device_index(0, i).get('name'):
                        mic_index = i
                    #print ("Input Device id ", i, " - ", self._audio_interface.get_device_info_by_host_api_device_index(0, i).get('name'))

        if mic_index == 0:
            print("Using built-in mic")
        else:
            print("Using Logitech mic")

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


def listen_print_loop(responses, stream, bluetooth):
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

            nameRecognition("Johnny", to_send, bluetooth)

            check = 0
            check += changeFont(to_send, bluetooth)

            if check == 0:
                sendToBluetooth(to_send, bluetooth)

            # Exit recognition if any of the transcribed phrases could be
            # one of our keywords.
            if re.search(r"\b(exit now please)\b", transcript, re.I):
                sys.stdout.write("Exiting...\n")
                stream.closed = True
                break

        else:
            sys.stdout.write(str(corrected_time) + ": " + transcript + "\r")

            stream.last_transcript_was_final = False

def sendToBluetooth(to_send, bluetooth):
    to_send = to_send.lstrip(" ")
    bytes_to_send = bytes(to_send, 'utf-8')
    if bluetooth.isOpen():
        bluetooth.write(bytes_to_send)
    #checkBluetoothConnection(bluetooth)

def checkBluetoothConnection(bluetooth):
    if bluetooth.isOpen():
        ACK = bluetooth.read()
        print("Response: " + str(ACK))

        if ACK == b'A':
            print("Sent successfully\n")

        elif ACK != b'A':
            print("Bluetooth disconnected, retrying")
            bluetooth.close()
            time.sleep(2)
            bluetooth = serial.Serial('COM6', 4800, parity=serial.PARITY_NONE, stopbits=serial.STOPBITS_ONE, timeout=3)

            bluetooth.write(b'TEST')

            ACK = bluetooth.read()
            print("Response: " + str(ACK))

            if ACK == b'A':
                print("Sent successfully\n")
            elif ACK != b'A':
                checkBluetoothConnection(bluetooth)

    else:
        print("Bluetooth disconnected, retrying")
        bluetooth.close()
        time.sleep(2)
        bluetooth = serial.Serial('COM6', 4800, parity=serial.PARITY_NONE, stopbits=serial.STOPBITS_ONE, timeout=3)

        bluetooth.write(b'TEST')
        ACK = bluetooth.read()
        print("Response: " + str(ACK))

        if ACK == b'A':
            print("Sent successfully\n")
        elif ACK != b'A':
            checkBluetoothConnection(bluetooth)

def nameRecognition(name, transcript, bluetooth):
    if name in transcript:
        print("****NAME RECOGNIZED****")
        sendToBluetooth("Your name has been mentioned", bluetooth)

def changeFont(transcript, bluetooth):
    if "size 1" in transcript:
        print("Setting font size to " + str(1))
        sendToBluetooth("FONT"+str(1), bluetooth)
    elif "size 2" in transcript:
        print("Setting font size to " + str(2))
        sendToBluetooth("FONT"+str(2), bluetooth)
    elif "size 3" in transcript:
        print("Setting font size to " + str(3))
        sendToBluetooth("FONT"+str(3), bluetooth)
    else:
        return 0
    return 1

def main():
    """start bidirectional streaming from microphone input to speech API"""

#    bluetooth = 1
    bluetooth = serial.Serial('COM6', 4800, parity=serial.PARITY_NONE, stopbits=serial.STOPBITS_ONE, timeout=3)

    if bluetooth.isOpen():
        print("Bluetooth port open")
    else:
        print("Bluetooth not connected")

    bluetooth.write(b"Bluetooth connected")

    checkBluetoothConnection(bluetooth)

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

    mic_manager = ResumableMicrophoneStream(SAMPLE_RATE, CHUNK_SIZE)
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
            listen_print_loop(responses, stream, bluetooth)

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

if __name__ == "__main__":
    main()