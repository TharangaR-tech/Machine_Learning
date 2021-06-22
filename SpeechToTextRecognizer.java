package com.company;

import com.google.cloud.speech.v1.*;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.protobuf.ByteString;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SpeechToTextRecognizer {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage:");
            System.out.printf(
                    "\tjava %s \"<command>\" \"<path-to-sound-file>\"\n"
                            + "Commands:\n"
                            + "\tsyncrecognize\n"
                            + "Path:\n\tA file path (ex: ./resources/audio.raw) or a URI "
                            + "for a Cloud Storage resource (gs://...)\n",
                    SpeechToTextRecognizer.class.getCanonicalName());
            return;
        }
        String command = args[0];
        String path = args.length > 1 ? args[1] : "";

        // Use command and GCS path pattern to invoke transcription.
        if (command.equals("syncrecognize")) {
            syncRecognizeFile(path);
        }

    }

    /**
     * Speech recognition on audio file (.wav) and prints the transcription.
     *
     * @param fileName the path to a PCM audio file to transcribe.
     */
    public static void syncRecognizeFile(String fileName) throws Exception {
        System.out.println(fileName);
        try (SpeechClient speech = SpeechClient.create()) {
            Path path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(path);
            ByteString audioBytes = ByteString.copyFrom(data);

            // Configure request with local raw PCM audio
            RecognitionConfig config =
                    RecognitionConfig.newBuilder()
                            .setEncoding(AudioEncoding.LINEAR16)
                            .setLanguageCode("en-US")
                            .setSampleRateHertz(44100)
                            .setAudioChannelCount(2)
                            .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

            // Use blocking call to get audio transcript
            RecognizeResponse response = speech.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            for (SpeechRecognitionResult result : results) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s%n", alternative.getTranscript());
            }
        }
    }
}
