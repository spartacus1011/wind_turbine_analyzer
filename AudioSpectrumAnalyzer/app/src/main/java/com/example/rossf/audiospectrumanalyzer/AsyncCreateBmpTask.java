package com.example.rossf.audiospectrumanalyzer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;

import java.util.Arrays;

public class AsyncCreateBmpTask extends AsyncTask<double[], Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(double[]... doubles) {

        double[] currentWaveDataDouble = doubles[0]; //Main data
        double[] numSettings = doubles[1];
        /* Structure for numSettings
        *  0 : Maximum Frequency
        *  1 : Maximum Amplitude
        *  2 : Base bmp Width
        *  3 : Base bmp Height
        *  4 : Skip x-axis marker
        *  5 : Skip y-axis marker
        *  6 : draw line between points (0 no, 1 yes)
        * */

        int length = currentWaveDataDouble.length;
        int pow2 = Integer.highestOneBit(length) << 1; //the next power of 2
        int sampleRate = 44100;
        int heightModifier = 1000; //Uhhhhhh

        double[] complexHalf = new double[length];
        double[] realHalf = new double[length];
        System.arraycopy(currentWaveDataDouble, 0, realHalf, 0, length);

        Arrays.fill(complexHalf, 0.0); //as we dont have any complex data to input to the fft, make it all 0's
        FFT fft = new FFT(Integer.highestOneBit(length));
        fft.fft(realHalf, complexHalf);


        double[] p2 = new double[length];
        for (int i = 0;i<length;i++){
            p2[i] = Math.abs((realHalf[i])/ heightModifier);
        }

        int maxFreq = (int) numSettings[0]; //basically x width
        int maxAmp = (int) numSettings[1];  //basically y height
        int baseX = (int) numSettings[2];
        int baseY = (int) numSettings[3];

        int xAxisMargin = 80;
        int yAxisMargin = 150;
        int numAxisPoints = 8;
        int maxFreqPlus1 = maxFreq + ((maxFreq)/numAxisPoints); //use these instead of maxFreq to push the graph a bit past the limit
        int maxAmpPlus1 = maxAmp + ((maxAmp)/numAxisPoints);

        ValueScaler scaleY = new ValueScaler(0,maxAmpPlus1 - yAxisMargin,0,baseY);
        ValueScaler scaleX = new ValueScaler(0,maxFreqPlus1 - xAxisMargin,0,baseX);

        Bitmap fullBmp = Bitmap.createBitmap(baseX, baseY,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(fullBmp);
        canvas.scale(1,-1,canvas.getWidth() / 2, canvas.getHeight()/ 2); //flip the canvas so (0,0) is in bottom left rather than top left

        int markerWidth = 10; //these could get decided outside this function aswell
        int markerWidthMax = markerWidth * 4;
        int axisWidth = 20;

        int axisMarginTextSize = 40;

        Paint marker = new Paint();
        marker.setColor(MainActivity.context.getColor(R.color.colorGraphPoint));
        marker.setStrokeWidth(markerWidth);
        Paint lineMarker = new Paint(marker);
        lineMarker.setStrokeWidth((markerWidth*2)/3);
        Paint maxPointMarker = new Paint();
        maxPointMarker.setColor(MainActivity.context.getColor(R.color.colorGraphPointMax));
        maxPointMarker.setStrokeWidth(markerWidthMax);
        Paint axisPaint = new Paint();
        axisPaint.setColor(MainActivity.context.getColor(R.color.colorGraphAxis));
        axisPaint.setStrokeWidth(axisWidth);
        Paint axisMarkerPaint = new Paint();
        axisMarkerPaint.setColor(MainActivity.context.getColor(R.color.colorGraphAxis));
        axisMarkerPaint.setStrokeWidth(axisWidth/2);
        axisMarkerPaint.setTextSize(axisMarginTextSize);
        Paint maxPointLine = new Paint();
        maxPointLine.setColor(MainActivity.context.getColor(R.color.colorGraphLineMax));
        maxPointLine.setStrokeWidth(markerWidth);

        float xMax = Float.MIN_VALUE; //dont really need to be min
        float yMax = Float.MIN_VALUE;

        float prevXVal = -1;    //its ok to use negative 1 because the graph values will never be negative
        float prevYVal = -1;    //would have preffered to use null, but it wouldn't let me

        //actual plot part
        for(int i =0; i<p2.length; i++) {
            float xVal = (float)(i *  scaleX.ScaleValue(((double)(sampleRate) / (pow2 >> 1))));
            if (xVal < maxFreqPlus1) {
                xVal += axisWidth + yAxisMargin;
                float yVal = (float) scaleY.ScaleValue(p2[i]);
                yVal += axisWidth + xAxisMargin;

                canvas.drawPoint(xVal,yVal, marker); //main point

                if(prevXVal != -1 && prevYVal != -1 && numSettings[6] == 1)
                    canvas.drawLine(prevXVal, prevYVal, xVal, yVal, lineMarker); //Line joining the points

                prevXVal = xVal;
                prevYVal = yVal;

                if(yVal > yMax)
                {
                    yMax = yVal;
                    xMax = xVal;
                }
            }
        }

        canvas.drawPoint(xMax, yMax , maxPointMarker);

        canvas.drawLine(xMax, xAxisMargin, xMax, fullBmp.getHeight(), maxPointLine); //max point line

        canvas.drawLine(yAxisMargin,xAxisMargin,fullBmp.getWidth(),xAxisMargin, axisPaint); //bottom  axis line
        canvas.drawLine(yAxisMargin,xAxisMargin,yAxisMargin,fullBmp.getHeight(), axisPaint); //left axis line

        double bottomAxisLength = fullBmp.getWidth() - yAxisMargin * 2;
        double leftAxisLength = fullBmp.getHeight() - xAxisMargin * 2;

        double axisXPointLength = bottomAxisLength / numAxisPoints;
        double axisYPointLength = leftAxisLength / numAxisPoints;

        //flip the canvas so that our bottom axis text doesnt come out flipped. Make sure to restore it later
        canvas.save();
        canvas.scale(1,-1, canvas.getWidth() / 2, canvas.getHeight()/ 2);

        int skipXMarker = (int) numSettings[4];
        int skipXMarkerCount = skipXMarker;
        int skipYMarker = (int) numSettings[5];
        int skipYMarkerCount = skipYMarker;

        for(int i = 0; i < numAxisPoints + 2; i++){ //the + 2 is so that we include 0 and the max value itself
            if(skipXMarkerCount ==  skipXMarker) {
                skipXMarkerCount = 0; //reset the count
                double lineMark = ((i * axisXPointLength) + yAxisMargin);
                canvas.drawLine((float) lineMark, canvas.getHeight() - (xAxisMargin / 1.5f), (float) lineMark, (float) (canvas.getHeight() - (xAxisMargin * 1.25)), axisMarkerPaint);
                String freqMark = Double.toString((maxFreq / numAxisPoints) * i);
                canvas.drawText(freqMark, (float) (lineMark - axisMarkerPaint.getTextSize() * (freqMark.length() / 4)), canvas.getHeight() - (xAxisMargin / 4), axisMarkerPaint);
            }
            else
                skipXMarkerCount ++;
        }

        for(int i = 0; i < numAxisPoints + 2; i++) {
            if (skipYMarkerCount == skipYMarker) {
                skipYMarkerCount = 0;
                double lineMark = (canvas.getHeight() - xAxisMargin) - (i * axisYPointLength);
                canvas.drawLine((yAxisMargin / 1.25f), (float) lineMark, (yAxisMargin * 1.25f), (float) lineMark, axisMarkerPaint);
                String ampMark = Double.toString((maxAmp / numAxisPoints) * i);
                canvas.drawText(ampMark, 0, (float) lineMark + axisMarkerPaint.getTextSize() / 2, axisMarkerPaint);
            }
            else
                skipYMarkerCount++;
        }
        canvas.restore();

        return fullBmp;
    }

    // you may separate this or combined to caller class.
    interface AsyncResponse {
        void processFinish(Bitmap output);
    }

    public AsyncResponse delegate = null;

    public AsyncCreateBmpTask(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onPostExecute(Bitmap bmp) {
        delegate.processFinish(bmp);
    }
}

