package com.ozzyboshi.worldmap.awt;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.ozzyboshi.worldmap.ImageSizeDifferentException;
import com.ozzyboshi.worldmap.WorldMapDrawable;
import com.ozzyboshi.worldmap.WorldMapMaker;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WorldMapDrawable<Object, Object> image = new WorldMapAwtDraw();
		image.setDayImageFile(new File("images/day.png"));
		image.setNightImageFile(new File("images/night.png"));
		try {
			WorldMapMaker maker = new WorldMapMaker(image, true, false);
			maker.BuildMapFromUnixTimestamp(System.currentTimeMillis()/1000L);
			BufferedImage output = (BufferedImage) image.getDestination();
			System.out.println(output);
			ImageIO.write(output, "PNG", new File("images/awtoutput2.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ImageSizeDifferentException e) {
			e.printStackTrace();
		}
		
	}

}
