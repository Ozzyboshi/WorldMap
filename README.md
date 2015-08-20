# WorldMap
Worldmap is a java library for creating a sun/night world map for a specific time.
This project is based on [This tutorial](http://www.edesign.nl/2009/05/14/math-behind-a-world-sunlight-map/), actually I translated the php code into java style classes therefore the real author is edesign.nl.

Prerequisites
-------------
In order to use this class you'll need 2 world png maps.
You can take advantage of the ones under images/ or create your own, just be sure they both have the same size.
Of course you'll also need a java compiler and a java development kit.

Dependencies
-------------
This library is built on top of javax.imageio, java awt , java.io, java.sql and java.util packages.
There are standard packages so you should not have trouble with dependencies.

Example
-------------
In this example I am going to create a new map based on the current pc date and save it.
    import java.io.File;
  import java.io.IOException;
  import com.ozzyboshi.worldmap.*;
  
  public class test {
  	public static void main(String[] args) {
  		try {
  			WorldMapMaker map = new WorldMapMaker(new File("images/day.png"),new File("images/night.png"));
  			map.BuildMapFromUnixTimestamp(System.currentTimeMillis() / 1000L);
  			map.WriteToPNGFile(new File("images/output.png"));
  		} catch (IOException | ImageSizeDifferentException e) {
  			e.printStackTrace();
  		}
  	}
  }

If you run this code you should get a new images/output.png file with the current Sun/Night WorldMap
