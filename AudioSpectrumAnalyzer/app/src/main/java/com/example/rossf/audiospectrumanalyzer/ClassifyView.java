package com.example.rossf.audiospectrumanalyzer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassifyView extends AppCompatActivity {

    private TextView textViewFileToSend;
    private EditText editTextRenameFile;
    private Spinner spinnerSelectableLayers;

    private String originalID;
    private List<String> spinnerTextItems = new ArrayList<String>();
    private String selectedLayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify_view);

        textViewFileToSend = (TextView) findViewById(R.id.textViewFileToSend);
        editTextRenameFile = (EditText) findViewById(R.id.editTextRenameFile);
        spinnerSelectableLayers = (Spinner) findViewById(R.id.spinnerSelectableLayers);

        Intent intentReceived = getIntent();

        originalID = intentReceived.getStringExtra(getString(R.string.classify_view_recording_key));

        textViewFileToSend.setText(originalID);
        editTextRenameFile.setHint(originalID);


        spinnerTextItems.add("WindTurbine");
        spinnerTextItems.add("Wind");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerTextItems);
        spinnerSelectableLayers.setAdapter(adapter);
        spinnerSelectableLayers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLayer = spinnerTextItems.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
            new TCPSendAudioData().execute(rawWavData, (textViewFileToSend.getText().toString().getBytes()), ("Training|" + selectedLayer).getBytes());

    }

    public void SendForClassification(View view) {

        byte[] rawWavData = new byte[0];

        try {
            WavReader wav = new WavReader(MainActivity.RecordingsDirectory + originalID + ".wav", true);
            rawWavData = wav.getByteArrayRaw();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!(rawWavData != null && rawWavData.length > 0))
        {
            return; //an error has occured when getting the local wav data
        }
        new TCPSendAudioData().execute(rawWavData, (textViewFileToSend.getText().toString().getBytes()), ("Classification|" + selectedLayer).getBytes());
        new TCPReceiveClassification().execute();

    }



    public void AdjustNameForSending(View view) {

        textViewFileToSend.setText(editTextRenameFile.getText());


    }
}
