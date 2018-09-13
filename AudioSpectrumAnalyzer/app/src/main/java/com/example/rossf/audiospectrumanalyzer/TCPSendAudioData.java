package com.example.rossf.audiospectrumanalyzer;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.format.Formatter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

//Will handle both sending the audio file data and receiving the classification
public class TCPSendAudioData extends AsyncTask<byte[],Void,Void> {

    @Override
    protected Void doInBackground(byte[]... dataToSend) { //uhh what if i dont want these to be params??

        String dataPurose; //either classification or training
        String dataFileInfo;
        String ipAddress;
        byte[] dataWav;
        try{
            ipAddress = new String(dataToSend[0]);
            dataWav = dataToSend[1];
            dataFileInfo = new String(dataToSend[2]);
            dataPurose = new String(dataToSend[3]);
        }
        catch(Exception e) {
            //I need to do something here to show that an error occured with the input params
            return null;
        }
        int port = 11000;


        Socket socket;

        DataOutputStream dataOutputStream;
        DataInputStream dataInputStream;

        try {
            InetAddress serverAddress = InetAddress.getByName(ipAddress);
            socket = new Socket(serverAddress, port); //This is where the initial connection is made

            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream()); //pretty sure in input serves no purpose here

            //NewSending stuff

            String infoToSend = String.format("%1$s|%2$s|%3$s", getIPAddress(true),dataPurose,dataFileInfo); //care data purpose encompasses two fields, both purpose and training destination
            byte[] infoToSendByte = infoToSend.getBytes("UTF-8");
            infoToSendByte = Arrays.copyOf(infoToSendByte, 1024); //This should do the padding //Shouldnt be needed if we open and close the sockets

            dataOutputStream.write(infoToSendByte);
            dataOutputStream.close();

            //Sending Part C:Wav file
            socket = new Socket(serverAddress, port); //Not keen on this, but it looks to be the only solution
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            dataOutputStream.write(dataWav);

            dataOutputStream.writeUTF("<EOF>"); //file sending is done

            //who needs to properly close a socket

            dataInputStream.close();
            dataOutputStream.close();
            socket.close();
        } catch (Exception e) {
            System.out.printf(e.getMessage());

        } finally {

        }
        return null;
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
