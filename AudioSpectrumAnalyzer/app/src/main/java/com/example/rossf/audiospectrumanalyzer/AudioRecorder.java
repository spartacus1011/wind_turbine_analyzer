package com.example.rossf.audiospectrumanalyzer;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import java.io.File;
import java.io.IOException;

public class AudioRecorder {

    //recording variables. Need to figure out what is the advantage to making them final
    final MediaRecorder recorder = new MediaRecorder();

    final String mainPath;

    public AudioRecorder(String filePath){
        mainPath = sanitizePath(filePath);

    }

    //I have no idea what the point of this is
    private String sanitizePath(String path) {

        if(!path.startsWith("/")){

            path = "/" + path;
        }

        //I dont really understand the point of this, but it looks like a bad idea
        if(!path.contains(".")){
            path += ".mp4";
        }

        return Environment.getExternalStorageDirectory().getAbsolutePath() + path;
    }

    public void StartRecording() throws IOException {
        String state = Environment.getExternalStorageState();

        if(!state.equals(Environment.MEDIA_MOUNTED)) {
            throw new IOException("SD Card isnt mounted. State: " + state);
        }

        File directory = new File(mainPath).getParentFile();
        if(!directory.exists() && directory.mkdirs()){
            throw new IOException("Path to file could not be created");
        }

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(mainPath);
        recorder.prepare();
        recorder.start();


    }

    public void StopRecording() throws IOException {
        recorder.stop();
        recorder.release();
    }

}
