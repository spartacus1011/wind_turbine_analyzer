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

        String dataFileInfo;
        byte[] dataWav;
        try{
            dataWav = dataToSend[0];
            dataFileInfo = new String(dataToSend[1]);
        }
        catch(Exception e) {
            //I need to do something here to show that an error occured with the input params
            return null;
        }
        int port = 11000;
        //String ipAddress = getIPAddress(true);
        String ipAddress = "192.168.0.12"; //Should make this an enterable parameter

        Socket socket;

        DataOutputStream dataOutputStream;
        DataInputStream dataInputStream;

        try {
            InetAddress serverAddress = InetAddress.getByName(ipAddress);
            socket = new Socket(serverAddress, port); //This is where the initial connection is made

            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            //the while loops should have some sort of timeout

            //Sending Part A: File Name
            byte[] textData = dataFileInfo.getBytes("UTF-8");
            textData = Arrays.copyOf(textData, 1024); //This should do the padding

            dataOutputStream.write(textData);

            dataOutputStream.close();
            //Sending Part B:Wav file
            socket = new Socket(serverAddress, port); //Not keen on this, but it looks to be the only solution
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.write(dataWav);

            dataOutputStream.writeUTF("<EOF>"); //file sending is done

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
