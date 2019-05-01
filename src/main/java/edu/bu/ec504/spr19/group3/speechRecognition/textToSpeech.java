package edu.bu.ec504.spr19.group3.speechRecognition;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import edu.bu.ec504.spr19.group3.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class textToSpeech {
    /**
     * Demonstrates using the Text-to-Speech API.
     *
     *
     * FileInputStream credentialsStream = new FileInputStream(fileName);
     * 		GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
     * 		FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);
     *
     * 		SpeechSettings speechSettings =
     * 				SpeechSettings.newBuilder()
     * 					.setCredentialsProvider(credentialsProvider)
     * 					.build();
     *
     * 		SpeechClient speech = SpeechClient.create(speechSettings);
     *
     */
    public static void run(String text, String fileLocation) throws Exception {


        FileInputStream credentialStream = new FileInputStream((new File(Main.ABSOLUTE_EXTERNAL_FILES_PATH, "textToSpeech.json")).getAbsolutePath());
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialStream);
        FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);

        TextToSpeechSettings speechSettings =
                TextToSpeechSettings.newBuilder()
                        .setCredentialsProvider(credentialsProvider)
                        .build();


        // Instantiates a client
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(speechSettings)) {
            // Set the text input to be synthesized
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(text) // Pass String here!!!
                    .build();

            // Build the voice request, select the language code ("en-US") and the ssml voice gender
            // ("neutral")
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("en-US")
                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                    .build();

            // Select the type of audio file you want returned
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            // Perform the text-to-speech request on the text input with the selected voice parameters and
            // audio file type
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice,
                    audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();

            // Write the response to the output file.
            try (OutputStream out = new FileOutputStream(fileLocation)) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio content written to file " + fileLocation);
            }
        }
    }
}
