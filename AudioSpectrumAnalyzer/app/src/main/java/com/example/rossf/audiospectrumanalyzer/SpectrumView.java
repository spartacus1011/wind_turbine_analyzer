package com.example.rossf.audiospectrumanalyzer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;


import org.jtransforms.fft.DoubleFFT_1D;
import org.jtransforms.fft.RealFFTUtils_2D;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpectrumView extends AppCompatActivity {

    private Spinner spinnerSelectableRecording;
    private ImageView graph;
    private EditText editTextMaxFreq;
    private EditText editTextMaxAmp;
    private Button buttonLoad;
    private CheckBox checkBoxJoinGraphPoints;

    private double[] currentWaveDataDouble;

    private List<RecordedItemInfo> allItems;

    private String currentRecordingID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectrum_view);

        //Testing


        //End Testing
        spinnerSelectableRecording = (Spinner) findViewById(R.id.spinnerSelectableRecordings);
        graph = (ImageView) findViewById(R.id.imageViewSpectrum);
        editTextMaxAmp = (EditText) findViewById(R.id.editTextMaxAmp);
        editTextMaxFreq = (EditText) findViewById(R.id.editTextMaxFreq);
        buttonLoad = (Button) findViewById(R.id.ButtonLoad);
        checkBoxJoinGraphPoints = (CheckBox) findViewById(R.id.checkBoxJoinGraphPoints);

        allItems = MainActivity.dbHelper.GetAllRecords();

        if (allItems.isEmpty()) {
            List<String> nullRecordingsList = new ArrayList<String>();
            nullRecordingsList.add("There are no Recordings in the Database");
            buttonLoad.setEnabled(false);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nullRecordingsList);
            spinnerSelectableRecording.setAdapter(adapter);
            return; //we have a return, no need for else. If anything non db related needs to be done, [ut it before here
        }

        List<String> spinnerTextItems = new ArrayList<String>();

        for (RecordedItemInfo recordedItemInfo : allItems) {
            if (recordedItemInfo.DateTime != null)
                spinnerTextItems.add(recordedItemInfo.Name + " , " + android.text.format.DateFormat.format("dd-MM-yyyy h:mm:ss", recordedItemInfo.DateTime).toString());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerTextItems);
        spinnerSelectableRecording.setAdapter(adapter);
        spinnerSelectableRecording.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                GetWaveData(allItems.get(i).UniqueID);
                currentRecordingID = allItems.get(i).UniqueID;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Intent intent = getIntent(); //keep this here j
        // ust in case you need to pass something along
        String keyToOpen = intent.getStringExtra(getString(R.string.spectrum_view_recording_key));

        if (keyToOpen.equals("None")) //using none as a null key is not best practice
        {
            //open/create default spectrum
            GetWaveData(allItems.get(0).UniqueID);
        } else {
            GetWaveData(keyToOpen);
        }

        Bitmap defaultBMP = Bitmap.createBitmap(1000, 500, Bitmap.Config.ARGB_8888);
        Canvas defaultCanvas = new Canvas(defaultBMP);
        Paint paint = new Paint();
        paint.setColor(getColor(R.color.colorText));
        int textSize = 50;
        paint.setTextSize(textSize);

        String charSequence = "No Spectrum Loaded";
        defaultCanvas.drawText(charSequence, (defaultCanvas.getWidth()/2) - (textSize * charSequence.length() / 4) , (defaultCanvas.getHeight()/2) - textSize, paint );

        graph.setImageBitmap(defaultBMP);
    }

    //Needs to be done after loading
    public void LoadOnClick(View view){

        double[] numSettings = new double[7];

        //As this page is landscape, height is width and width is height
        numSettings[2] = MainActivity.ScreenHeight;
        numSettings[3] = MainActivity.ScreenWidth;
        numSettings[4] = 0;
        numSettings[5] = 0;
        numSettings[6] = (checkBoxJoinGraphPoints.isChecked())? 1:0; //do a conditional statement here

        try {
            numSettings[0] = Double.parseDouble(editTextMaxFreq.getText().toString());
            numSettings[1] = Double.parseDouble(editTextMaxAmp.getText().toString());
        }
        catch (NumberFormatException e){
            numSettings[0] = 10000;
            numSettings[1] = 2000;
            editTextMaxFreq.setText(Double.toString(10000));
            editTextMaxAmp.setText(Double.toString(2000));
        }

        AsyncCreateBmpTask asyncTask = (AsyncCreateBmpTask) new AsyncCreateBmpTask(new AsyncCreateBmpTask.AsyncResponse(){

            @Override
            public void processFinish(Bitmap output){
                Bitmap bmpScaled = Bitmap.createScaledBitmap(output, graph.getWidth(), graph.getHeight(), true);

                graph.setImageBitmap(bmpScaled);

            }
        }).execute(currentWaveDataDouble, numSettings);

    }

    private void GetWaveData(String uniqueId) { //not sure why i made this a function
        try {
            WavReader wav = new WavReader(MainActivity.RecordingsDirectory + uniqueId + ".wav", true);
            currentWaveDataDouble = wav.getByteArrayDouble();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void OpenClassifyView(View view){

        Intent intent = new Intent(this, ClassifyView.class);
        intent.putExtra(getString(R.string.classify_view_recording_key), currentRecordingID);

        try {
            startActivity(intent);
        }
        catch(Exception e){


        }
    }

}
