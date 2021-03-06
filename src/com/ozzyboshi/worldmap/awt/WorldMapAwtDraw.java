package com.ozzyboshi.worldmap.awt;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.ozzyboshi.worldmap.ImageSizeDifferentException;
import com.ozzyboshi.worldmap.WorldMapDrawable;

public class WorldMapAwtDraw implements WorldMapDrawable<Object,Object> {
	
	private BufferedImage imgDay;
	private BufferedImage imgNight;
	private File dayImageFile;
	private File nightImageFile;
	private InputStream dayImageStream;
	private InputStream nightImageStream;
	private boolean streamInput;
	private BufferedImage destImg;

	// Read png files from filesystem and checks that they have the same size
	@Override
	public void readFromFiles() throws ImageSizeDifferentException {
		try {
			if (streamInput==true) 	{imgDay = ImageIO.read(dayImageStream);imgNight = ImageIO.read(nightImageStream);}
			else					{imgDay = ImageIO.read(dayImageFile); imgNight = ImageIO.read(nightImageFile);}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		
		if (imgDay.getWidth()!=imgNight.getWidth() || imgDay.getWidth()!=imgNight.getWidth()) {
			throw new ImageSizeDifferentException();
		}
	}
	
	// Create new image in memory with the same size of the input files
	@Override
	public void createDestinationImage() {
		destImg = new BufferedImage(imgDay.getWidth(), imgDay.getHeight(),BufferedImage.TYPE_INT_RGB);
	}

	//Sets the day png file path
	@Override
	public void setDayImageFile(Object dayImageFile) {
		this.dayImageFile = (File)dayImageFile;
		streamInput=false;
	}
	
	@Override
	public void setDayImageInputStream(InputStream dayImageStream) {
		this.dayImageStream = dayImageStream;
		streamInput=true;
	}

	//Sets the night png file path
	@Override
	public void setNightImageFile(Object nightImageFile) {
		this.nightImageFile = (File)nightImageFile;
		streamInput=false;
	}
	
	@Override
	public void setNightImageInputStream(InputStream nightImageStream) {
		this.nightImageStream = nightImageStream;
		streamInput=true;
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
		return imgDay.getRGB(x, y);
	}

	@Override
	public void setDestinationRgbAt(int x, int y,int rgb) {
		destImg.setRGB(x, y,rgb);
	}

	@Override
	public int getNightRgbAt(int x, int y) {
		return imgNight.getRGB(x, y);
	}

	@Override
	public BufferedImage getDestination() {
		return this.destImg;
	}

	@Override
	public int getRGB(int r, int g, int b) {
		Color col = new Color (r,g,b);
		return col.getRGB();
	}
}
