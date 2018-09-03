package com.example.rossf.audiospectrumanalyzer;

import android.os.AsyncTask;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TCPSocket extends AsyncTask<Void,Void,Void> {


    @Override
    protected Void doInBackground(Void... voids) {
        int port = 11000;
        String ipAddress = "192.168.0.12"; //investgate getting the IP the same way that you do in c#

        Socket socket;

        DataOutputStream dataOutputStream;
        DataInputStream dataInputStream;

        try {
            InetAddress serverAddress = InetAddress.getByName(ipAddress);
            socket = new Socket(serverAddress, port); //This is where the initial connection is made
            String testMessage = "World Hello";


            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            //sending stuffs
            dataOutputStream.writeUTF(testMessage);

            //String ack = dataInputStream.readUTF();

            //who needs to properly close a socket
            dataInputStream.close();
            dataOutputStream.close();
            socket.close();
        } catch (Exception e) {
            //mkmsg("Error happened sending/receiving\n");
            System.out.printf(e.getMessage());

        } finally {

        }
        return null;
    }
}
