package com.example.rossf.audiospectrumanalyzer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;

public class TCPReceiveClassification  extends AsyncTask<Void,Void,String[]> {

    public interface AsyncResponse {
        void processFinish(String[] output);
    }

    public AsyncResponse delegate = null;

    public TCPReceiveClassification(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onPostExecute(String[] result) {
        delegate.processFinish(result);
    }

    @Override
    protected String[] doInBackground(Void... voids) {

        int port = 11000;

        Socket socket;
        byte[] dataBuffer = new byte[1024];

        DataOutputStream dataOutputStream; //shouldnt need the output stream
        DataInputStream dataInputStream;

        try
        {
            ServerSocket serverSocket = new ServerSocket(port);

            socket = serverSocket.accept();

            dataInputStream = new DataInputStream(socket.getInputStream());

            String inputString = "";
            while(true) {

                dataInputStream.read(dataBuffer);

                inputString += new String(dataBuffer,"UTF-8");

                if(inputString.contains("<EOF>"))
                    break;
            }

            return inputString.split(Pattern.quote("|"));

        }
        catch(Exception e)
        {

        }
        return null;
    }

}
