package com.example.rossf.audiospectrumanalyzer;

import android.os.AsyncTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TCPReceiveClassification  extends AsyncTask<Void,Void,String[]> {
    @Override
    protected String[] doInBackground(Void... voids) {

        int port = 11000;
        String ipAddress = "192.168.0.9"; //Should make this an enterable parameter

        Socket socket;

        DataOutputStream dataOutputStream; //shouldnt need the output stream
        DataInputStream dataInputStream;

        try
        {
            InetAddress serverAddress = InetAddress.getByName(ipAddress);
            socket = new Socket(serverAddress, port); //This is where the initial connection is made
        }
        catch(Exception e)
        {

        }


        return null;
    }
}
