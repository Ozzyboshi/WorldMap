package com.ozzyboshi.worldmap.androidgraphics;

import android.graphics.Bitmap;

import com.ozzyboshi.worldmap.WorldMapDrawable;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;

/**
 * Created by ozzy on 21/08/15.
 */
public class WorldMapAndroidGraphicsDraw implements WorldMapDrawable<Object,Object> {
    private Bitmap imgDay;
    private Bitmap imgNight;
    private File dayImageFile;
    private File nightImageFile;
    private Drawable dayImageDrawable;
    private Drawable nightImageDrawable;
    private Bitmap destImg;

    // Read png files from filesystem and checks that they have the same size
    @Override
    public void readFromFiles() {
        imgDay =  ((BitmapDrawable)dayImageDrawable).getBitmap();
        imgNight =  ((BitmapDrawable)nightImageDrawable).getBitmap();
    }

    // Create new image in memory with the same size of the input files
    @Override
    public void createDestinationImage() {
        //destImg = new BufferedImage(imgDay.getWidth(), imgDay.getHeight(),BufferedImage.TYPE_INT_RGB);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        destImg = Bitmap.createBitmap(imgDay.getWidth(), imgDay.getHeight(), conf);
    }

    //Sets the day png file path
    @Override
    public void setDayImageFile(Object dayImageFile) {
        this.dayImageDrawable = (Drawable)dayImageFile;
    }

    //Sets the night png file path
    @Override
    public void setNightImageFile(Object nightImageFile) {
        this.nightImageDrawable = (Drawable)nightImageFile;
    }

    @Override
    public int getWidth() {
        return imgDay.getWidth();

    }

    @Override
    public int getHeight() {
        return imgDay.getHeight();
    }

    @Override
    public int getDayRgbAt(int x, int y) {
        return imgDay.getPixel(x, y);
    }

    @Override
    public void setDestinationRgbAt(int x, int y,int rgb) {
        destImg.setPixel(x, y, rgb);
    }

    @Override
    public int getNightRgbAt(int x, int y) {
        return imgNight.getPixel(x, y);
    }

    @Override
    public android.graphics.Bitmap getDestination() {
        return this.destImg;
    }

    @Override
    public int getRGB(int r, int g, int b) {

        return Color.rgb(r,g,b);
    }

}
