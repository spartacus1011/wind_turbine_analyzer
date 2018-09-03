package com.example.rossf.audiospectrumanalyzer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainMenu extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void OpenRecordingWindow(View view){

        Intent intent = new Intent(this, RecordingView.class);

        try {
            startActivity(intent);
        }
        catch(Exception e){


        }

    }

    public void OpenSpectrumViewer(View view) {

        Intent intent = new Intent(this, SpectrumView.class);

        intent.putExtra(getString(R.string.spectrum_view_recording_key),"None");

        try {
            startActivity(intent);
        }
        catch(Exception e){


        }

    }

    public void OpenDatabaseViewer(View view) {

        Intent intent = new Intent(this, ShowDatabaseView.class);



        try {
            startActivity(intent);
        }
        catch(Exception e){


        }

    }

}
