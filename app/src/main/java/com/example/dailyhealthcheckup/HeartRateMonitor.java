package com.example.dailyhealthcheckup;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.example.dailyhealthcheckup.util.Utility;
import java.util.ArrayList;
import java.util.List;

public class HeartRateMonitor {

    private final Integer HEART_RATE_OUTPUT_TIME_IN_SEC = 45;
    private final Integer FRAMES_PER_SECOND = 25;
    private final Integer SECONDS_TO_SKIP_FROM_START = 0; // avoid flash's white pixels
    private final Integer SECONDS_TO_SKIP_FROM_END = 0; // avoid manual error

    private static List<Float> RED;
    private static MediaMetadataRetriever media;
    private static double heartRate;
    private static String heartRateOutputFilePath;

    HeartRateMonitor() {

        RED = null;
        heartRate = 0.0;
        heartRateOutputFilePath = null;
        media = new MediaMetadataRetriever();
    }

    public void setHeartRateOutputFilePath(String heartRateOutputFilePath) {

        this.heartRateOutputFilePath = heartRateOutputFilePath;
        System.out.println("heart rate output file path = " + this.heartRateOutputFilePath);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public double calculateHeartRate() {

        media.setDataSource(heartRateOutputFilePath);
        int FRAME_HEIGHT = Integer.parseInt(media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        int FRAME_WIDTH = Integer.parseInt(media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int increments = 1000000/FRAMES_PER_SECOND;
        int HEART_RATE_OUTPUT_TIME_IN_mS = HEART_RATE_OUTPUT_TIME_IN_SEC * 1000000;


        RED = new ArrayList<>();

        for(int sec=0; sec < HEART_RATE_OUTPUT_TIME_IN_mS; sec += increments){

            int sum = 0;
            Bitmap bitmap = media.getFrameAtTime(sec,MediaMetadataRetriever.OPTION_CLOSEST);

            for (int y = 0; y < FRAME_HEIGHT; y++){

                for (int x = 0; x < FRAME_WIDTH; x++){ sum += (bitmap.getPixel(x, y) & 0xff0000) >> 16; }
            }

            RED.add((float)sum / (FRAME_WIDTH * FRAME_HEIGHT));
        }

        System.out.println("Total red pixel readings(average value per frame): " + RED.size());
        return findRate(RED);
    }

    public double findRate(List<Float> averageREDPixelValues) {

        int n = averageREDPixelValues.size();

        Float[] values = new Float[n];
        int i = 0;
        for(Float f : averageREDPixelValues) { values[i++] = f; }

        Float[] smoothed = Utility.applySmoothingTo1DArray(values);

        int zeroCrossings = Utility.findZeroCrossingsIn1DArray(smoothed);

        float peaks = (float)(zeroCrossings / 2);

        heartRate = ((peaks / (HEART_RATE_OUTPUT_TIME_IN_SEC - (SECONDS_TO_SKIP_FROM_START + SECONDS_TO_SKIP_FROM_END))) * 60f);

        System.out.println("Heart rate: " + heartRate);
        return heartRate;
    }
}
