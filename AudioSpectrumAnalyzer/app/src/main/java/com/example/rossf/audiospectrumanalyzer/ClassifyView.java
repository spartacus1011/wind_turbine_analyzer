package com.example.rossf.audiospectrumanalyzer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class ClassifyView extends AppCompatActivity {

    private TextView textViewFileToSend;
    private EditText editTextRenameFile;

    private String originalID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify_view);

        textViewFileToSend = (TextView) findViewById(R.id.textViewFileToSend);
        editTextRenameFile = (EditText) findViewById(R.id.editTextRenameFile);

        Intent intentReceived = getIntent();

        originalID = intentReceived.getStringExtra(getString(R.string.classify_view_recording_key));

        textViewFileToSend.setText(originalID);
        editTextRenameFile.setHint(originalID);
    }

    public void SendForTraining(View view) {

        byte[] rawWavData = new byte[0];

        try {
            WavReader wav = new WavReader(MainActivity.RecordingsDirectory + originalID + ".wav", true);
            rawWavData = wav.getByteArrayRaw();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(rawWavData != null && rawWavData.length > 0)
            new TCPSendAudioData().execute(rawWavData, (textViewFileToSend.getText().toString().getBytes()), "Training".getBytes());

    }

    public void SendForClassification(View view) {

        byte[] rawWavData = new byte[0];

        try {
            WavReader wav = new WavReader(MainActivity.RecordingsDirectory + originalID + ".wav", true);
            rawWavData = wav.getByteArrayRaw();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(rawWavData != null && rawWavData.length > 0)
            new TCPSendAudioData().execute(rawWavData, (textViewFileToSend.getText().toString().getBytes()), "Classification".getBytes());

    }



    public void AdjustNameForSending(View view) {

        textViewFileToSend.setText(editTextRenameFile.getText());


    }
}
