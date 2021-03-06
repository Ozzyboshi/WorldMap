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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class WorldMapMaker {
	
	private WorldMapDrawable<?, ?> drawable;
	private final double M_PI = Math.PI;
	
	private double time;
	private int daysInYear;
	private int dayOfYear;
	
	private boolean drawDuskAndDawn ;
	private boolean printBlackPixelAtNight;
	private boolean drawDuskAndDawnLikeNight;

	public WorldMapMaker(WorldMapDrawable<?, ?> drawable, boolean drawDuskAndDawn,boolean printBlackPixelAtNight) throws ImageSizeDifferentException {
		this.drawable=drawable ;
		this.drawable.readFromFiles();
		this.drawDuskAndDawn = drawDuskAndDawn;
		this.printBlackPixelAtNight = printBlackPixelAtNight;
		this.drawDuskAndDawnLikeNight=false;
	}
	
	public boolean isPrintBlackPixelAtNight() {
		return printBlackPixelAtNight;
	}

	public void setPrintBlackPixelAtNight(boolean printBlackPixelAtNight) {
		this.printBlackPixelAtNight = printBlackPixelAtNight;
	}

	public boolean isDrawDuskAndDawn() {
		return drawDuskAndDawn;
	}

	public void setDrawDuskAndDawn(boolean drawDuskAndDawn) {
		this.drawDuskAndDawn = drawDuskAndDawn;
	}

	public Object BuildMapFromUnixTimestamp(long unixTime) {
		return Build(unixTime);
	}
	
	private Object Build(long unixTime) {
		
		TimeCalculations(unixTime);
				 
		Vec3 pointingFromEarthToSun = new Vec3(Math.sin((2*M_PI) * time), 0, Math.cos((2*M_PI) * time));
		double tilt = 23.5 * Math.cos((2 * M_PI * (dayOfYear - 173)) / daysInYear);
		Vec3 seasonOffset = new Vec3(0, Math.tan(M_PI *2* (tilt/360)), 0);
		pointingFromEarthToSun = pointingFromEarthToSun.add(seasonOffset);
		
		int maxU = drawable.getWidth();
		int maxV=drawable.getHeight();
		int doubleMaxV = maxV * 2;
		
		pointingFromEarthToSun.normalize();
		drawable.createDestinationImage();
		for(int u = 0; u < drawable.getWidth(); u++) {
	        for(int v = 0; v < drawable.getHeight(); v++) {
	        	
	        	double phi = (((double)v / (double)doubleMaxV) - 1)*(2*M_PI);
	        	double theta = ((double)u/(double)maxU)*(2*M_PI);
	            
	            double y = Math.cos(phi);
	            double x = Math.sin(phi) * Math.cos(theta);
	            double z = Math.sin(phi) * Math.sin(theta);
	            	            
	            Vec3 earthNormal = new Vec3(x, y, z);
	            earthNormal.normalize();
	            	            
	            double angleBetweenSurfaceAndSunlight = pointingFromEarthToSun.dot(earthNormal);	            
	        	
	        	// Night
	        	if(angleBetweenSurfaceAndSunlight <= -.1 || (drawDuskAndDawn==false && drawDuskAndDawnLikeNight==true && angleBetweenSurfaceAndSunlight < .1)) {
	        		int rgb =0;
	        		if (printBlackPixelAtNight==false) {
	        			rgb = drawable.getNightRgbAt(u, v);
	        		}
	        			
	        		drawable.setDestinationRgbAt(u,v,rgb);
	            }
	        	// The pointingFromEarthToSun almost misses the earth
	        	else if(drawDuskAndDawn==true && angleBetweenSurfaceAndSunlight < .1) { 

	        		double fractionDay = (angleBetweenSurfaceAndSunlight+.1)* 5;
	        		ArrayList<MixColor> colors = new ArrayList<MixColor>();
	        		
	        		MixColor color1 = new MixColor(drawable.getDayRgbAt(u, v), fractionDay);
	        		MixColor color2 = new MixColor(drawable.getNightRgbAt(u, v), 1-fractionDay);
	        		
	        		colors.add(color1);
	        		colors.add(color2);
	        		
	        		int[] sharedColors = mixColors(colors);
	        		
	        		int rgb=this.drawable.getRGB(sharedColors[0], sharedColors[1], sharedColors[2]);
	        		drawable.setDestinationRgbAt(u,v,rgb);
	            }
	        	
	        	//Day
	        	else {
	        		int rgb = drawable.getDayRgbAt(u, v);
	        		drawable.setDestinationRgbAt(u,v,rgb);
	        	}
	        		
	        }
		}
		return drawable.getDestination();
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
	
	private int[] mixColors(ArrayList<MixColor> colors) {
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
        
        return new int[] {r,g,b};
    }
	public boolean isDrawDuskAndDawnLikeNight() {
		return drawDuskAndDawnLikeNight;
	}

	public void setDrawDuskAndDawnLikeNight(boolean drawDuskAndDawnLikeNight) {
		this.drawDuskAndDawnLikeNight = drawDuskAndDawnLikeNight;
	}
}
