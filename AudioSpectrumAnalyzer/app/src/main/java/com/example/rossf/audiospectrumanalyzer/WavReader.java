package com.example.rossf.audiospectrumanalyzer;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class WavReader {

    private byte[] entireFileData;

    /*
    //SR = sampling rate
    public double getSR(){
        ByteBuffer wrapped = ByteBuffer.wrap(Arrays.copyOfRange(entireFileData, 24, 28)); // big-endian by default
        double SR = wrapped.order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
        return SR;
    }

    */
    public WavReader(String filepath, boolean print_info) throws IOException {

        //forced to comment out as this requires api 27
        //Path path = Paths.get(filepath);
        //this.entireFileData = Files.readAllBytes(path);

        File file = new File(filepath);


        if(file == null || !file.exists())
            throw new FileNotFoundException("FILE NOT FOUND IN WAV READER");

        entireFileData = convertFileToByteArray(file);

        if (print_info){

            //extract format
            String format = new String(Arrays.copyOfRange(entireFileData, 8, 12), "UTF-8");

            //extract number of channels
            int noOfChannels = entireFileData[22];
            String noOfChannels_str;
            if (noOfChannels == 2)
                noOfChannels_str = "2 (stereo)";
            else if (noOfChannels == 1)
                noOfChannels_str = "1 (mono)";
            else
                noOfChannels_str = noOfChannels + "(more than 2 channels)";

            //extract sampling rate (SR)
            //int SR = (int) this.getSR();
            int SR = 44100; //TESTING ONLY. can put this here because we know that it is the sampling rate from the wav reader

            //extract Bit Per Second (BPS/Bit depth)
            int BPS = entireFileData[34];
        }
    }

    //This basically gets rid of the headers and endings of wav file and returns data only
    public short[] getByteArrayShort(){
        byte[] data_raw = Arrays.copyOfRange(entireFileData, 44, entireFileData.length);
        int totalLength = data_raw.length;

        //declare double array for mono
        int new_length = totalLength/4;
        short[] data_mono = new short[new_length];

        double left, right;
        for (int i = 0; 4*i+3 < totalLength; i++){
            left = (short)((data_raw[4*i+1] & 0xff) << 8) | (data_raw[4*i] & 0xff);
            right = (short)((data_raw[4*i+3] & 0xff) << 8) | (data_raw[4*i+2] & 0xff);
            data_mono[i] = (short) ((left+right)/2.0);
        }
        return data_mono;
    }

    public double[] getByteArrayDouble() {

        byte[] data_raw = Arrays.copyOfRange(entireFileData, 44, entireFileData.length);
        int totalLength = data_raw.length;

        //declare double array for mono
        int new_length = totalLength/4;
        double[] data_mono = new double[new_length];

        double left, right;
        for (int i = 0; 4*i+3 < totalLength; i++){
            left = (short)((data_raw[4*i+1] & 0xff) << 8) | (data_raw[4*i] & 0xff);
            right = (short)((data_raw[4*i+3] & 0xff) << 8) | (data_raw[4*i+2] & 0xff);
            data_mono[i] = (short) ((left+right)/2.0);
        }
        return data_mono;

    }

    private byte[] convertFileToByteArray(File f)
    {
        byte[] byteArray = null;
        try
        {
            InputStream inputStream = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024*8];
            int bytesRead =0;

            while ((bytesRead = inputStream.read(b)) != -1)
            {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return byteArray;
    }

}
