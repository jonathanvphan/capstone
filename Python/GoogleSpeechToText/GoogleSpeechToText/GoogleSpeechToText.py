import sounddevice as sd
from scipy.io.wavfile import write
from google.cloud import speech
import io

def record_audio(file_name):
    fs = 44100  # Sample rate
    seconds = 3  # Duration of recording

    myrecording = sd.rec(int(seconds * fs), samplerate=fs, channels=1)
    sd.wait()  # Wait until recording is finished
    write(file_name, fs, myrecording)  # Save as WAV file 

def transcribe_file(speech_file):
    """Transcribe the given audio file."""
    client = speech.SpeechClient.from_service_account_json('credentials.json')

    with io.open(speech_file, "rb") as audio_file:
        content = audio_file.read()

    audio = speech.RecognitionAudio(content=content)
    config = speech.RecognitionConfig(
        encoding=speech.RecognitionConfig.AudioEncoding.LINEAR16,
        language_code="en-US"
#        sample_rate_hertz = 44100,
#        audio_channel_count = 2
    )

    response = client.recognize(config=config, audio=audio)

    # Each result is for a consecutive portion of the audio. Iterate through
    # them to get the transcripts for the entire audio file.
    for result in response.results:
        # The first alternative is the most likely one for this portion.
        print(u"Transcript: {}".format(result.alternatives[0].transcript))

#record_audio('test1.wav')
transcribe_file(r"C:\Users\Admin\Documents\GitHub\capstone\Python\GoogleSpeechToText\GoogleSpeechToText\testaudioforgoogle2.wav")