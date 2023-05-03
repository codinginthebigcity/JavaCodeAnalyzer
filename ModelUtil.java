package com.myapp.myapplication;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class ModelUtil {
    private static String TAG = "ModelUtil";
    private int bitmapWidthInPixel;
    private int bitmapHeightInPixel;
    private int startXInPixel;
    private int startYInPixel;
    private int floorPlanWidthInMeter;
    private int floorPlanHeightInMeter;
    private int defaultColor = Color.parseColor("#886750A4");
    private String csvOrPlyFilePath;

    public ModelUtil(int bitmapWidthInPixel, int bitmapHeightInPixel, int startXInPixel, int startYInPixel
            , int floorPlanWidthInMeter, int floorPlanHeightInMeter, String csvOrPlyFilePath) {
        this.bitmapWidthInPixel = bitmapWidthInPixel;
        this.bitmapHeightInPixel = bitmapHeightInPixel;
        this.startXInPixel = startXInPixel;
        this.startYInPixel = startYInPixel;
        this.floorPlanWidthInMeter = floorPlanWidthInMeter;
        this.floorPlanHeightInMeter = floorPlanHeightInMeter;
        this.csvOrPlyFilePath = csvOrPlyFilePath;
        Log.i(TAG, String.format("Info %s %s %s %s %s %s %s",
                bitmapWidthInPixel, bitmapHeightInPixel, startXInPixel, startYInPixel
                , floorPlanWidthInMeter, floorPlanHeightInMeter, csvOrPlyFilePath));
    }

    public Bitmap exportBitmap() {
        int[][] map = new int[bitmapWidthInPixel][bitmapHeightInPixel];
        Bitmap myBitmap = Bitmap.createBitmap(bitmapWidthInPixel, bitmapHeightInPixel, Bitmap.Config.ARGB_8888);
        try {
            Reader reader = new InputStreamReader(
                    new FileInputStream(csvOrPlyFilePath), "UTF-8");
            BufferedReader fin = new BufferedReader(reader);
            String s;
            String splitChar = ";";
            fin.readLine();
            if (csvOrPlyFilePath.endsWith(".ply")) {
                while (!(s = fin.readLine()).equals("end_header")) ;
                splitChar = " ";
            } else {
                fin.readLine(); //read first
            }
            String[] split;
            double x, y;
            int xPaint, yPaint;
            int count = 0;
            while ((s = fin.readLine()) != null) {
                split = s.split(splitChar);
                x = Double.parseDouble(split[0]);
                y = Double.parseDouble(split[2]);
                xPaint = (int) (x * bitmapWidthInPixel / floorPlanWidthInMeter) + startXInPixel;
                yPaint = (int) (-y * bitmapHeightInPixel / floorPlanHeightInMeter) + startYInPixel;
                if (xPaint >= 0 && xPaint < bitmapWidthInPixel
                        && yPaint >= 0 && yPaint < bitmapHeightInPixel) {
                    map[xPaint][yPaint] = 1;
                } else {
                    int z = 0;
                }
                count++;
            }
            for (int i = 0; i < bitmapWidthInPixel; i++) {
                for (int j = 0; j < bitmapHeightInPixel; j++) {
                    myBitmap.setPixel(i, j, map[i][j] == 1 ? defaultColor : Color.TRANSPARENT);
                }
            }
            int l = bitmapWidthInPixel / 20;
            for (int i = 0; i < l; i++) {
                xPaint = startXInPixel + i;
                if (xPaint >= 0 && xPaint < bitmapWidthInPixel) {
                    if (startYInPixel > 0)
                        myBitmap.setPixel(xPaint, startYInPixel - 1, Color.YELLOW);
                    myBitmap.setPixel(xPaint, startYInPixel, Color.YELLOW);
                    if (startYInPixel + 1 < bitmapHeightInPixel)
                        myBitmap.setPixel(xPaint, startYInPixel + 1, Color.YELLOW);
                }
            }
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Done");
        return myBitmap;
    }
}
