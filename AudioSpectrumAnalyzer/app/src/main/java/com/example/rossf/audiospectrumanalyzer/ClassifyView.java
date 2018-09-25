package com.example.rossf.audiospectrumanalyzer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassifyView extends AppCompatActivity {

    private TextView textViewFileToSend;
    private EditText editTextRenameFile;
    private Spinner spinnerSelectableLayers;

    private TextView editTextIpToSendTo;

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

        editTextIpToSendTo = (EditText) findViewById(R.id.editTextIpToSendTo);

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

        String ipToSend = editTextIpToSendTo.getText().toString();
        if(ipToSend.isEmpty())
            ipToSend = "10.132.96.97";

        new TCPSendAudioData().execute(ipToSend.getBytes(),
                rawWavData,
                (textViewFileToSend.getText().toString().getBytes()),
                ("Classification|" + selectedLayer).getBytes());

        new TCPReceiveClassification(new TCPReceiveClassification.AsyncResponse(){

            @Override
            public void processFinish(String[] output){
                Intent intent = new Intent(ClassifyView.this, ClassifyResultsView.class);

                intent.putExtra("Result", output[0]);
                intent.putExtra("Percentages", output[1]);

                startActivity(intent);
            }
        }).execute();

    }



    public void AdjustNameForSending(View view) {

        textViewFileToSend.setText(editTextRenameFile.getText());


    }

    private static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }

}
