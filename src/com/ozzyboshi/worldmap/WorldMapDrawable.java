package com.ozzyboshi.worldmap;

import java.io.InputStream;

public interface WorldMapDrawable <T,RESOURCE> {
	public void readFromFiles() throws ImageSizeDifferentException;
	public void setDayImageFile(RESOURCE dayImageFile);
	public void setNightImageFile(RESOURCE dayImageFile);
	public void createDestinationImage();
	public int getWidth();
	public int getHeight();
	public int getDayRgbAt(int x,int y);
	public int getNightRgbAt(int x,int y);
	public void setDestinationRgbAt(int x,int y,int rgb);
	public int getRGB(int r,int g,int b);
	
	public T getDestination();
	void setDayImageInputStream(InputStream ImageFile);
	void setNightImageInputStream(InputStream dayImageStream);
		
}
