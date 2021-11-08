package com.example.listening_eye

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.speech.v1.*
import com.google.protobuf.ByteString
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class Voice2TextKotlin : Fragment(){

    companion object {
        fun newInstance() = Voice2TextKotlin()
    }

    private val speechClient: SpeechClient by lazy {
        activity?.applicationContext?.resources?.openRawResource(R.raw.credential).use {
            SpeechClient.create(
                    SpeechSettings.newBuilder()
                            .setCredentialsProvider { GoogleCredentials.fromStream(it) }
                            .build())
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        return inflater.inflate(R.layout.activity_voice2_text_kotlin, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val filePath = requireActivity().getExternalFilesDir("Download").toString() + "/testaudio.m4a"
        GlobalScope.launch {
            analyze(ByteString.copyFrom(File(filePath).readBytes()))
        }
    }

    private fun analyze(fileByteString: ByteString) {
        val req = RecognizeRequest.newBuilder()
                .setConfig(
                        RecognitionConfig.newBuilder()
                                //.setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                                .setLanguageCode("en-US")
                                .setSampleRateHertz(16000)
                                .build()
                )
                .setAudio(
                        RecognitionAudio.newBuilder()
                                .setContent(fileByteString)
                                .build()
                )
                .build()

        val response = speechClient.recognize(req)

        Log.d("TUT", "Response, count ${response.resultsCount}")
        val results = response.resultsList
        for (result in results) {
            val alternative = result.alternativesList[0]
            val text = alternative.transcript
            Log.d("TUT", "Transcription: $text")

        }
    }
}