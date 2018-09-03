package com.example.rossf.audiospectrumanalyzer;

import android.graphics.Bitmap;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.ImageView;

//might need to refactor this name
public class DataAudioRecorder {

    private final int sampleRate = 44100;

    private ImageView graph;

    private boolean Continue; // kinda like an isRecording
    private boolean doRecord;
    private String fullPath;
    private String fileName;

    public DataAudioRecorder(boolean doRecord, String fullPath, String fileName, ImageView graph) {

        this.doRecord = doRecord;
        this.fullPath = fullPath;
        this.fileName = fileName;
        this.graph = graph;
    }

    public void StartRecord(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

                int bufferSize = AudioRecord.getMinBufferSize(sampleRate,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                    bufferSize = sampleRate * 2;
                }

                short[] audioBuffer = new short[bufferSize / 2];

                AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                        sampleRate,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize);

                if (record.getState() != AudioRecord.STATE_INITIALIZED) {
                    return;
                }


                WavWriter wavWriter = new WavWriter(sampleRate, fullPath, fileName);

                record.startRecording();

                if(doRecord)
                    wavWriter.start(); //need to put an if on this start to make sure that the file has been created successfully

                Continue = true;

                long shortsRead = 0;
                while (Continue) {
                    int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
                    shortsRead += numberOfShort;

                    if(doRecord)
                        wavWriter.pushAudioShort(audioBuffer, numberOfShort);

                    if(graph == null) //just in case we want to record but not show the graph
                        continue;

                    //convert short[] to double[]
                    double[] dataForGraph = new double[audioBuffer.length];
                    for(int i = 0; i<audioBuffer.length;i++){
                        dataForGraph[i] = audioBuffer[i];
                    }

                    double[] numSettings = new double[] {6000,1000, 1000, 1000, 1, 0, 0};

                    //graphing
                    //This could be a bad idea. I guess we will find out
                    new AsyncCreateBmpTask(new AsyncCreateBmpTask.AsyncResponse() {

                        @Override
                        public void processFinish(Bitmap output) {
                            Bitmap bmpScaled = Bitmap.createScaledBitmap(output, graph.getWidth(), graph.getHeight(), true);

                            graph.setImageBitmap(bmpScaled);

                        }
                    }).execute(dataForGraph, numSettings);
                } //while loop

                if(doRecord)
                    record.stop();

                record.release();
                wavWriter.stop();
            }
        }).start();
    }

    //of course i can just set the bool normally from outside the class but this just looks neater
    public void StopRecord() {
        Continue = false;

    }

}
