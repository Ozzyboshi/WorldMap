package com.ozzyboshi.worldmap;

import java.io.File;
import java.io.IOException;

public interface WorldMapDrawable <T> {
	public void readFromFiles();
	public void setDayImageFile(File dayImageFile);
	public void setNightImageFile(File dayImageFile);
	public void createDestinationImage();
	public int getWidth();
	public int getHeight();
	public int getDayRgbAt(int x,int y);
	public int getNightRgbAt(int x,int y);
	public void setDestinationRgbAt(int x,int y,int rgb);
	
	public T getDestination();
		
}
