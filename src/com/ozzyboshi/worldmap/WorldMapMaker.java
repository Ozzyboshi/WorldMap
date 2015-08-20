/*
The MIT License (MIT)

Copyright (c) 2015, Alessio Garzi <gun101@email.it>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/ 

//World light map based on http://www.edesign.nl/2009/05/14/math-behind-a-world-sunlight-map/

package com.ozzyboshi.worldmap;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import javax.imageio.ImageIO;

public class WorldMapMaker {
	
	private BufferedImage imgDay;
	private BufferedImage imgNight;
	
	private final double M_PI = Math.PI;
	
	private double time;
	private int daysInYear;
	private int dayOfYear;
	
	private BufferedImage destImg;
	
	private boolean drawDuskAndDawn ;
	private boolean printBlackPixelAtNight;
	
	public WorldMapMaker(File dayImage,File nightImage,boolean drawDuskAndDawn,boolean printBlackPixelAtNight) throws IOException, ImageSizeDifferentException {
		readInputFiles(dayImage, nightImage);
		this.drawDuskAndDawn = drawDuskAndDawn;
		this.printBlackPixelAtNight = printBlackPixelAtNight;
	}
	
	public WorldMapMaker(File dayImage,File nightImage) throws IOException, ImageSizeDifferentException {
		readInputFiles(dayImage,nightImage);
		this.drawDuskAndDawn = true;
		printBlackPixelAtNight=false;
	}
	
	public boolean isPrintBlackPixelAtNight() {
		return printBlackPixelAtNight;
	}

	public void setPrintBlackPixelAtNight(boolean printBlackPixelAtNight) {
		this.printBlackPixelAtNight = printBlackPixelAtNight;
	}

	private void readInputFiles(File dayImage,File nightImage) throws IOException, ImageSizeDifferentException {
		imgDay = ImageIO.read(dayImage);
		imgNight = ImageIO.read(nightImage);
		
		if (imgDay.getWidth()!=imgNight.getWidth() || imgDay.getWidth()!=imgNight.getWidth()) {
			throw new ImageSizeDifferentException();
		}
	}

	public boolean isDrawDuskAndDawn() {
		return drawDuskAndDawn;
	}

	public void setDrawDuskAndDawn(boolean drawDuskAndDawn) {
		this.drawDuskAndDawn = drawDuskAndDawn;
	}

	public BufferedImage BuildMapFromUnixTimestamp(long unixTime) {
		return Build(unixTime);
	}
	
	public void WriteToPNGFile(File destinationFile) throws IOException {
		
		ImageIO.write(this.destImg, "PNG", destinationFile);
	}
	
	public byte[] WriteToByteArray() throws IOException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( this.destImg, "PNG", baos );
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		return imageInByte;
	}
	
	public void WriteToPNGFile(File destinationFile,BufferedImage distImg) throws IOException {
		
		ImageIO.write(destImg, "PNG", destinationFile);
	}
	
	private BufferedImage Build(long unixTime) {
		
		TimeCalculations(unixTime);
		
		destImg = new BufferedImage(imgDay.getWidth(), imgDay.getHeight(),BufferedImage.TYPE_INT_RGB);
		
		Vec3 pointingFromEarthToSun = new Vec3(Math.sin((2*M_PI) * time), 0, Math.cos((2*M_PI) * time));
		double tilt = 23.5 * Math.cos((2 * M_PI * (dayOfYear - 173)) / daysInYear);
		Vec3 seasonOffset = new Vec3(0, Math.tan(M_PI *2* (tilt/360)), 0);
		pointingFromEarthToSun = pointingFromEarthToSun.add(seasonOffset);
		
		int maxU = imgDay.getWidth();
		int maxV=imgDay.getHeight();
		int doubleMaxV = maxV * 2;
		
		pointingFromEarthToSun.normalize();
		
		for(int u = 0; u < imgDay.getWidth(); u++) {
	        for(int v = 0; v < imgDay.getHeight(); v++) {
	        	
	        	double phi = (((double)v / (double)doubleMaxV) - 1)*(2*M_PI);
	        	double theta = ((double)u/(double)maxU)*(2*M_PI);
	            
	            double y = Math.cos(phi);
	            double x = Math.sin(phi) * Math.cos(theta);
	            double z = Math.sin(phi) * Math.sin(theta);
	            	            
	            Vec3 earthNormal = new Vec3(x, y, z);
	            earthNormal.normalize();
	            	            
	            double angleBetweenSurfaceAndSunlight = pointingFromEarthToSun.dot(earthNormal);	            
	        	
	        	// Night
	        	if(angleBetweenSurfaceAndSunlight <= -.1) {
	        		int rgb =0;
	        		if (printBlackPixelAtNight==false)
	        			rgb = imgNight.getRGB(u, v);
	        		destImg.setRGB(u, v,rgb);
	            }
	        	// The pointingFromEarthToSun almost misses the earth
	        	else if(drawDuskAndDawn==true && angleBetweenSurfaceAndSunlight < .1) { 

	        		double fractionDay = (angleBetweenSurfaceAndSunlight+.1)* 5;
	        		ArrayList<MixColor> colors = new ArrayList<MixColor>();
	        		
	        		MixColor color1 = new MixColor(imgDay.getRGB(u, v), fractionDay);
	        		MixColor color2 = new MixColor(imgNight.getRGB(u, v), 1-fractionDay);
	        		
	        		colors.add(color1);
	        		colors.add(color2);
	        		
	        		Color color = mixColors(colors);
	        		
	        		int rgb = color.getRGB();
	        		destImg.setRGB(u, v,rgb);
	            }
	        	
	        	//Day
	        	else {
	        		int rgb = imgDay.getRGB(u, v);
	        		destImg.setRGB(u, v,rgb);
	        	}
	        		
	        }
		}
		return destImg;
	}
	
	private void TimeCalculations(long unixTime) {
		Date date = new Date((long)unixTime*1000);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		daysInYear = cal.getActualMaximum(Calendar.DAY_OF_YEAR);
		dayOfYear = cal.get(Calendar.DAY_OF_YEAR)-1;  
		time=(double)cal.get(Calendar.HOUR_OF_DAY)+((double)cal.get(Calendar.MINUTE)/60)+((double)cal.get(Calendar.SECOND)/3600);
		TimeZone tz = cal.getTimeZone();
		int timeZoneOffset = tz.getOffset(date.getTime())/1000;
		time = (time + 24 + 6 - (timeZoneOffset/3600));
		
		while (time>24)
			time=time-24;
		time=time/24;
	}
	
	private Color mixColors(ArrayList<MixColor> colors) {
        double shares = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        
        for (MixColor color : colors) {
        	
        	int rgb = color.getRgb();
        	double share = color.getFractionDay();
        	
        	if (share>0) {
        		r += ((rgb >> 16) & 0xFF) * share;
        		g += ((rgb >> 8) & 0xFF) * share;
        		b += (rgb & 0xFF) * share;
        		shares += share;
        	}
        }
        
        r = (int) Math.round(r / shares);
        g = (int) Math.round(g / shares);
        b = (int) Math.round(b / shares);
        
        return new Color(r,g,b);
    }
}
