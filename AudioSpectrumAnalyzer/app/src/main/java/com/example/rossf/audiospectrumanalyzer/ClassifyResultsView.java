package com.example.rossf.audiospectrumanalyzer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.regex.Pattern;

public class ClassifyResultsView extends AppCompatActivity {

    TextView textViewResult;
    TextView textViewWindTurbinePercent;
    TextView textViewWindPercent;
    TextView textViewOtherPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify_results_view);

        textViewResult = (TextView) findViewById(R.id.textViewResult);
        textViewWindTurbinePercent = (TextView) findViewById(R.id.textViewWindTurbinePercent);
        textViewWindPercent = (TextView) findViewById(R.id.textViewWindPercent);
        textViewOtherPercent = (TextView) findViewById(R.id.textViewOtherPercent);

        Intent received = getIntent();

        String[] percentages = received.getStringExtra("Percentages").split(Pattern.quote(","));

        textViewResult.setText("Result: " + received.getStringExtra("Result"));

        if(percentages.length >= 3) {
            textViewWindTurbinePercent.setText("Wind turbine percentage: " + (Float.parseFloat(percentages[1])*100));
            textViewWindPercent.setText("Wind percentage: " + (Float.parseFloat(percentages[0])*100));
            textViewOtherPercent.setText("Other percentage: " + (Float.parseFloat(percentages[2])*100));
        }
    }
}
