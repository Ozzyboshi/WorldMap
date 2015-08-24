# WorldMap
Worldmap is a java library jar file for creating a sun/night world map at a specific time.
Inside the jar file you'll find 2 packages:
- com.ozzyboshi.worldmap.androidgraphics : meant to be used in android projects
- com.ozzyboshi.worldmap.aawt : meant to be used in awt projects
- 
This project is based on [This tutorial](http://www.edesign.nl/2009/05/14/math-behind-a-world-sunlight-map/).
The php code in this tutorial was translated by me into java style classes, thank you edesign.nl.

WorldMap output live demo
This is a live demo of the library, this image is created dynamically with java Jersey and the WorldMap library on top of a TOMCAT 8 Java applicaton server.You can find the code in the example paragraph. 
![WorldMap demo](http://meteo.ozzyboshi.com:8082/WeatherStation/MeteoServices/Readings/WorldImage)

Prerequisites
-------------
In order to use this class you'll need 2 world png maps: the WorldMapMaker class is in charge of generating an output image based on the images you provide. 
You can take advantage of the ones under images/ or create your own, just be sure they both have the same size (same width and height).
To generate the jar file you'll also need Eclipse with Android Developer Tools (ADT) or at least this is what I used.
You could also get my precompiled jar file from the release section of github.

Example
-------------
In this example I am going to create a new map based on the current pc date and save it as a regular PNG file on my images folder.
    public class WorldMapCli {
	    public static void main(String[] args) {
    		WorldMapDrawable<Object, Object> image = new WorldMapAwtDraw();
    		image.setDayImageFile(new File("images/day.png"));
    		image.setNightImageFile(new File("images/night.png"));
    		try {
    			WorldMapMaker maker = new WorldMapMaker(image, true, false);
    			maker.BuildMapFromUnixTimestamp(System.currentTimeMillis()/1000L);
    			BufferedImage output = (BufferedImage) image.getDestination();
    			ImageIO.write(output, "PNG", new File("images/output.png"));
    		}
    		catch (ImageSizeDifferentException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
	    }
    }

If you run this code you should get a new images/output.png file with the current Sun/Night WorldMap

You can also generate the output image from InputStream instead using the File class, just use the set(Day|Night)ImageInputStream methods instead of set(Day|Night)ImageFile, this was useful in combination with Jersey (see the demo before):
    WorldMapDrawable<Object, Object> image = new WorldMapAwtDraw();
	image.setDayImageInputStream(Meteo.class.getResourceAsStream("/images/day.png"));
	image.setNightImageInputStream(Meteo.class.getResourceAsStream("/images/night.png"));
	try {
		WorldMapMaker maker = new WorldMapMaker(image, true, false);
		maker.BuildMapFromUnixTimestamp(System.currentTimeMillis()/1000L);
	}
	catch (ImageSizeDifferentException e) {
		e.printStackTrace();
	}

If you want to use this library for your Android project you must use android.graphics instead of awt (or at least this is what Google suggests in his own Android documentation).
In the following example I am going to create a Bitmap (instead of an BufferedImage) that is ready to be drawn in a Canvas object.
    Drawable day = getResources().getDrawable(R.drawable.day);
    Drawable night = getResources().getDrawable(R.drawable.night);
    WorldMapAndroidGraphicsDraw image = new WorldMapAndroidGraphicsDraw();
    image.setDayImageFile(day);
    image.setNightImageFile(night);
    WorldMapMaker maker = new WorldMapMaker(image, true, false);
    maker.BuildMapFromUnixTimestamp(System.currentTimeMillis() / 1000L);
    Bitmap output = image.getDestination();
    
In all the above examples I created the WorldMapMaker object with 2 switched (true/false):
- the first switch (second parameter) if true prints mixed pixels in the dawn/dusk zone so it is smoother.
- the second switch (third parameter) if true prints a black pixel in the night zone, in this case the night image is ignored even if (for now) the setNightImageFile call is mandatory.
    
License
-------------
This project (as the code I found in the tutorial I mentioned before) is licensed under the MIT License, you can freely use it in your project and distribute.
As always I am looking for contribution so, if you improve my code please submit a pull request, I will be happy to merge your code.
