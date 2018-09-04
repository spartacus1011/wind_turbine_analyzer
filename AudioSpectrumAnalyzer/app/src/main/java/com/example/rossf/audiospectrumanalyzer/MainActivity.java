package com.example.rossf.audiospectrumanalyzer;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;

import java.io.File;
import java.io.IOException;
import java.util.List;

//This is actually splash. Might be a pain, but it should be renamed at some point
public class MainActivity extends AppCompatActivity {

    public static DatabaseHelper dbHelper;
    public static String BaseWorkingDirectory;
    public static String RecordingsDirectory;

    public static Context context; //use ONLY for getting resources to places you couldnt otherwise access them from

    public static int ScreenWidth;
    public static int ScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // there should be a check somewhere to make sure that the database still exists
        // only real way to do this would be to store some rough details in a text file and save it
        // not the worst idea to do for db integrity check
        dbHelper = new DatabaseHelper(this);
        //dbHelper.DeleteAll(); //RIP
        BaseWorkingDirectory = Environment.getExternalStorageDirectory().getPath() + "/" + getString(R.string.app_name);
        RecordingsDirectory = BaseWorkingDirectory + "/Recordings/";
        context = this.getApplicationContext();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ScreenHeight = displayMetrics.heightPixels;
        ScreenWidth = displayMetrics.widthPixels;

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(MainActivity.this,MainMenu.class);
                MainActivity.this.startActivity(mainIntent);
                MainActivity.this.finish();
            }
        }, 2000);



    }
}
