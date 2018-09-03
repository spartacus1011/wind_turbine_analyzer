package com.example.rossf.audiospectrumanalyzer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RecordingView extends AppCompatActivity {

    private Button buttonRecording;
    private Button buttonGoToSpectrumViewer;
    private TextView textViewRecordingLength;
    private TextView textViewLocation;
    private AutoCompleteTextView autoCompleteTextViewRecordingName;
    private ImageView imageViewSpectrum;

    private DataAudioRecorder dataAudioRecorder;
    private Boolean isRecording;

    private String currentRecordingUniqueId;
    private int currentRecordingId;

    private long StartTime, TimeBuff, UpdateTime;
    int Seconds, Minutes, MilliSeconds ;
    private Handler handler;
    private final int timerTick = 10;

    private LocationManager locationManager;
    private LocationListener locationListener;


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_view);

        //Intent intent = getIntent(); //keep this here just in case you need to pass something along

        //setup timer handler
        handler = new Handler();

        buttonRecording = (Button) findViewById(R.id.buttonRecording);
        buttonGoToSpectrumViewer = (Button) findViewById(R.id.buttonGoToSpectrumViewer);
        textViewRecordingLength = (TextView) findViewById(R.id.textViewRecordingLength);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        autoCompleteTextViewRecordingName = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewRecordingName);
        imageViewSpectrum = (ImageView) findViewById(R.id.ImageViewQuickShow);

        isRecording = false;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }
        else {
            SetupBaseFolder();
        }

        SetupLocationStuff();

        //draw the defualt bmp because its just easier
        Bitmap defaultBMP = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        Canvas defaultCanvas = new Canvas(defaultBMP);
        Paint paint = new Paint();
        paint.setColor(getColor(R.color.colorText));
        int textSize = 50;
        paint.setTextSize(textSize);

        String charSequence = "No recording Yet";
        defaultCanvas.drawText(charSequence, (defaultCanvas.getWidth()/2) - (textSize * charSequence.length() / 4) , (defaultCanvas.getHeight()/2) - textSize, paint );

        imageViewSpectrum.setImageBitmap(defaultBMP);

    }

    public void onClickButtonRecord(View view) throws IOException, InterruptedException {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},0); //the 0 is for recording audio, i think
        }
        else {
            RecordAudio();
        }
    }

    public void onClickButtonViewSpectrum(View view){

        if(currentRecordingUniqueId != null){
            Intent intent = new Intent(this, SpectrumView.class);
            intent.putExtra(getString(R.string.spectrum_view_recording_key),currentRecordingUniqueId);
            try {
                startActivity(intent);
            }
            catch(Exception e){
            }

        }

    }

    public void UpdateGraph(double[] bufferData){

        AsyncCreateBmpTask asyncTask = (AsyncCreateBmpTask) new AsyncCreateBmpTask(new AsyncCreateBmpTask.AsyncResponse(){

            @Override
            public void processFinish(Bitmap output){
                Bitmap bmpScaled = Bitmap.createScaledBitmap(output, imageViewSpectrum.getWidth(), imageViewSpectrum.getHeight(), true);

                imageViewSpectrum.setImageBitmap(bmpScaled);

            }
        }).execute(bufferData);

    }

    public Runnable runnable = new Runnable() {

        public void run() {
            long MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            MilliSeconds = (int) (UpdateTime % 1000);
            textViewRecordingLength.setText(String.format("%01d : %02d : %03d",Minutes, Seconds, MilliSeconds));

            handler.postDelayed(this, timerTick);
        }
    };



    private void SetupBaseFolder(){
        //create the folder for the audio spectrum analyzer if it hasnt already been created
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name));
        boolean success = false;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }

    }

    private void SetupLocationStuff() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                textViewLocation.setText("Current Location \nLatitude: " + String.format("%.5f",location.getLatitude()) + "\nLongitude: " + String.format("%.5f",location.getLongitude()));
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }
            @Override
            public void onProviderEnabled(String s) {
            }
            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},0);
        }

    }

    private void RecordAudio() throws IOException, InterruptedException {

        if(!isRecording) {
            autoCompleteTextViewRecordingName.setEnabled(false); //this doesnt matter at all, but disable it anyway to avoid confusion

            currentRecordingUniqueId = UUID.randomUUID().toString();

            dataAudioRecorder = new DataAudioRecorder(true,MainActivity.BaseWorkingDirectory + "/Recordings", currentRecordingUniqueId, imageViewSpectrum);
            isRecording = false;

            //setup and then start the timer
            StartTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, timerTick);
            buttonRecording.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.record_stop,0);

            dataAudioRecorder.StartRecord();

            isRecording = true;

        }
        else {
            autoCompleteTextViewRecordingName.setEnabled(true);

            //stop the timer
            handler.removeCallbacks(runnable);
            buttonRecording.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.record_recording,0);

            dataAudioRecorder.StopRecord();

            isRecording = false;

            Thread.sleep(500); //give it a sec to finish saving the recording //this might not be needed

            if(new File(MainActivity.BaseWorkingDirectory + "/Recordings/" + currentRecordingUniqueId + ".wav").exists())
            {
                buttonGoToSpectrumViewer.setEnabled(true);

                String locationLat = "No location given";
                String locationLong = "No location given";

                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    //location = locationManager.getLastKnownLocation("gps").toString(); //This doesnt work so well
                    locationLat = Double.toString(locationManager.getLastKnownLocation("gps").getLatitude());
                    locationLong = Double.toString(locationManager.getLastKnownLocation("gps").getLongitude());
                }

                //String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
                String currentDateTime = android.text.format.DateFormat.format("dd-MM-yyyy h:mm:ss", new java.util.Date()).toString();
                MainActivity.dbHelper.InsertNew(0,currentRecordingUniqueId,currentDateTime,locationLat, locationLong, autoCompleteTextViewRecordingName.getText().toString());
            }
            else {
                buttonGoToSpectrumViewer.setEnabled(false);
                textViewRecordingLength.setText("Something has gone wrong with the Recording. File not found");
            }
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){

        List<String> permissionsList = Arrays.asList(permissions);

        //audio
        if(permissionsList.contains(Manifest.permission.RECORD_AUDIO) && permissionsList.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            if(grantResults[permissionsList.indexOf(Manifest.permission.RECORD_AUDIO)] == PackageManager.PERMISSION_GRANTED
                    && grantResults[permissionsList.indexOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)] == PackageManager.PERMISSION_GRANTED)
            {
                try {
                    RecordAudio();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                //Eh we can use this one to display the error
                textViewRecordingLength.setText("Cannot record audio due to not having permissions");
            }
        }

        //location
        if(permissionsList.contains(Manifest.permission.ACCESS_COARSE_LOCATION) && permissionsList.contains(Manifest.permission.ACCESS_FINE_LOCATION)){
            if(grantResults[permissionsList.indexOf(Manifest.permission.ACCESS_COARSE_LOCATION)] == PackageManager.PERMISSION_GRANTED
                    && grantResults[permissionsList.indexOf(Manifest.permission.ACCESS_FINE_LOCATION)] == PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
            }
            else {
                textViewLocation.setText("Unable to get location");
            }

        }

    }

}
