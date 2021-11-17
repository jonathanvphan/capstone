package com.example.listening_eye;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeRequest;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;


import com.google.api.gax.longrunning.OperationFuture;
import com.google.api.gax.longrunning.OperationTimedPollAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.retrying.TimedRetryAlgorithm;
import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.speech.v1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.cloud.speech.v1.WordInfo;
import com.google.common.util.concurrent.SettableFuture;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.threeten.bp.Duration;


import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class voice2text extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice2text);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        String filenameTest="src/credential.json";
//        Path pathToFile = Paths.get(filenameTest);
//        System.out.println(pathToFile.toAbsolutePath());

        String credentialPath = "/Users/michaelyan/AndroidStudioProjects/Listening_Eye/app/src/main/res/raw/credential.json";

        Button testButton = findViewById(R.id.testButton);
//        Environment GOOGLE_APPLICATION_CREDENTIALS = "/Users/michaelyan/AndroidStudioProjects/Listening_Eye/app/src/main/res/raw/credential.json";
//        try {
//            authExplicit("/Users/michaelyan/AndroidStudioProjects/Listening_Eye/app/src/main/res/raw/credential.json");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    streamingRecognizeFile(credentialPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//        TextView conversionTextbox = findViewById(R.id.conversion_text);
//        conversionTextbox.setText();

    }


//    public static void sampleRecognize() {
//        // TODO(developer): Replace these variables before running the sample.
//        String localFilePath = "sdcard/download/testaudio.txt";
//        sampleRecognize(localFilePath);
//    }
//
//    /**
//     * Transcribe a short audio file using synchronous speech recognition
//     *
//     * @param localFilePath Path to local audio file, e.g. /path/audio.wav
//     */
//    public static void sampleRecognize(String localFilePath) {
//
//        try (SpeechClient speechClient = SpeechClient.create()) {
//
//            // The language of the supplied audio
//            String languageCode = "en-US";
//
//            // Sample rate in Hertz of the audio data sent
//            int sampleRateHertz = 16000;
//
//            // Encoding of audio data sent. This sample sets this explicitly.
//            // This field is optional for FLAC and WAV audio formats.
//            RecognitionConfig.AudioEncoding encoding = RecognitionConfig.AudioEncoding.LINEAR16;
//            RecognitionConfig config =
//                    RecognitionConfig.newBuilder()
//                            .setLanguageCode(languageCode)
//                            .setSampleRateHertz(sampleRateHertz)
//                            .setEncoding(encoding)
//                            .build();
//            Path path = Paths.get(localFilePath);
//            byte[] data = Files.readAllBytes(path);
//            ByteString content = ByteString.copyFrom(data);
//            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(content).build();
//            RecognizeRequest request =
//                    RecognizeRequest.newBuilder().setConfig(config).setAudio(audio).build();
//            RecognizeResponse response = speechClient.recognize(request);
//            for (SpeechRecognitionResult result : response.getResultsList()) {
//                // First alternative is the most probable result
//                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
//                System.out.printf("Transcript: %s\n", alternative.getTranscript());
//            }
//        } catch (Exception exception) {
//            System.err.println("Failed to create the client due to: " + exception);
//        }
//    }

    /**
     * Performs streaming speech recognition on raw PCM audio data.
     *
     * @param fileName the path to a PCM audio file to transcribe.
     */
    public static void streamingRecognizeFile(String fileName) throws Exception, IOException {
//        Path path = Paths.get(fileName);
//        System.out.println(Files.exists(path));
//        System.out.println(Files.notExists(path));
        File f = new File(fileName);
        System.out.println(f.getAbsoluteFile());
        System.out.println(fileName);
        Path path = Paths.get(f.getAbsolutePath());
        System.out.println(path.getFileName());
        System.out.println(path.toUri());
        byte[] data = Files.readAllBytes(path);
        System.out.println(data);



        FileInputStream credentialsStream = new FileInputStream(fileName);
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
        FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);

        SpeechSettings speechSettings =
                SpeechSettings.newBuilder()
                        .setCredentialsProvider(credentialsProvider)
                        .build();


        // Instantiates a client with GOOGLE_APPLICATION_CREDENTIALS
        try (SpeechClient speech = SpeechClient.create(speechSettings)) {
            System.out.println("printed succes");
            // Configure request with local raw PCM audio
            RecognitionConfig recConfig =
                    RecognitionConfig.newBuilder()
                            .setEncoding(AudioEncoding.LINEAR16)
                            .setLanguageCode("en-US")
                            .setSampleRateHertz(16000)
                            .setModel("default")
                            .build();
            StreamingRecognitionConfig config =
                    StreamingRecognitionConfig.newBuilder().setConfig(recConfig).build();

            class ResponseApiStreamingObserver<T> implements ApiStreamObserver<T> {
                private final SettableFuture<List<T>> future = SettableFuture.create();
                private final List<T> messages = new java.util.ArrayList<T>();

                @Override
                public void onNext(T message) {
                    messages.add(message);
                }

                @Override
                public void onError(Throwable t) {
                    future.setException(t);
                }

                @Override
                public void onCompleted() {
                    future.set(messages);
                }

                // Returns the SettableFuture object to get received messages / exceptions.
                public SettableFuture<List<T>> future() {
                    return future;
                }
            }

            ResponseApiStreamingObserver<StreamingRecognizeResponse> responseObserver =
                    new ResponseApiStreamingObserver<>();

            BidiStreamingCallable<StreamingRecognizeRequest, StreamingRecognizeResponse> callable =
                    speech.streamingRecognizeCallable();

            ApiStreamObserver<StreamingRecognizeRequest> requestObserver =
                    callable.bidiStreamingCall(responseObserver);

            // The first request must **only** contain the audio configuration:
            requestObserver.onNext(
                    StreamingRecognizeRequest.newBuilder().setStreamingConfig(config).build());

            // Subsequent requests must **only** contain the audio data.
            requestObserver.onNext(
                    StreamingRecognizeRequest.newBuilder()
                            .setAudioContent(ByteString.copyFrom(data))
                            .build());

            // Mark transmission as completed after sending the data.
            requestObserver.onCompleted();

            List<StreamingRecognizeResponse> responses = responseObserver.future().get();

            for (StreamingRecognizeResponse response : responses) {
                // For streaming recognize, the results list has one is_final result (if available) followed
                // by a number of in-progress results (if iterim_results is true) for subsequent utterances.
                // Just print the first result here.
                StreamingRecognitionResult result = response.getResultsList().get(0);
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcript : %s\n", alternative.getTranscript());
            }
        }
    }

    static void authExplicit(String jsonPath) throws IOException {
        // You can specify a credential file by providing a path to GoogleCredentials.
        // Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath))
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        System.out.println("Buckets:");
        Page<Bucket> buckets = storage.list();
        for (Bucket bucket : buckets.iterateAll()) {
            System.out.println(bucket.toString());
        }
    }

    static void authImplicit() {
        // If you don't specify credentials when constructing the client, the client library will
        // look for credentials via the environment variable GOOGLE_APPLICATION_CREDENTIALS.
        Storage storage = StorageOptions.getDefaultInstance().getService();

        System.out.println("Buckets:");
        Page<Bucket> buckets = storage.list();
        for (Bucket bucket : buckets.iterateAll()) {
            System.out.println(bucket.toString());
        }
    }
}