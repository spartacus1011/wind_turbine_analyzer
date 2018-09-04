package com.example.rossf.audiospectrumanalyzer;

import android.os.AsyncTask;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

//Will handle both sending the audio file data and receiving the classification
public class TCPSocket extends AsyncTask<byte[],Void,Void> {

    @Override
    protected Void doInBackground(byte[]... dataToSend) { //uhh what if i dont want these to be params??

        String dataFileInfo;
        byte[] dataWav;
        try{
            dataWav = dataToSend[0];
            dataFileInfo = new String(dataToSend[1], );
        }
        catch(Exception e) {
            //I need to do something here to show that an error occured with the input params
            return null;
        }
        int port = 11000;
        String ipAddress = "10.132.102.36"; //investgate getting the IP the same way that you do in c#

        Socket socket;

        DataOutputStream dataOutputStream;
        DataInputStream dataInputStream;

        try {
            InetAddress serverAddress = InetAddress.getByName(ipAddress);
            socket = new Socket(serverAddress, port); //This is where the initial connection is made
            String testMessage = "World Hello";


            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            //the while loops should have some sort of timeout

            //Sending Part A: File Name
            String partAAck = "";
            while (partAAck.contains("A Received")){

                dataOutputStream.writeUTF(dataFileInfo);
                partAAck = dataInputStream.readUTF();
            }

            dataOutputStream.write(dataWav);

            dataOutputStream.writeUTF("<EOF>"); //file sending is done
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
