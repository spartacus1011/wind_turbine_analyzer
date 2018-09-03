package com.example.rossf.audiospectrumanalyzer;

import android.graphics.Bitmap;
import android.os.AsyncTask;

//This function wont be needed unless there is a problem with creating all the buffer threads
public class AsyncDataAudioRecorder extends AsyncTask<Void, Bitmap, Void>{
    @Override
    protected Void doInBackground(Void... voids) {
        //This will need to call the other async task and bubble back up a on progress


        return null;
    }



    interface AsyncResponseRecorder {
        Bitmap onProgressUpdate(Bitmap output);
    }

    public AsyncCreateBmpTask.AsyncResponse delegate = null;

    public AsyncDataAudioRecorder(AsyncCreateBmpTask.AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onProgressUpdate(Bitmap... values) {
        delegate.processFinish(values[0]);
    }

}
