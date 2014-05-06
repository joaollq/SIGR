import geolocators.AbstractGeolocator;
import geolocators.IDFGeolocator;
import geolocators.InverseConvexHullAreaGeolocator;
import geolocators.RandomGeolocator;
import geolocators.TagMatchGeolocator;

import java.io.IOException;

import cleaners.LatLgnCleaner;
import cleaners.MetaCleaner;


public class SIGR {
	private static final float TRAININGSIZE = (float) 0.9;
	static double LAT = 38.725670;
	static double LON =  -9.150370;
	static double BUFFER = 1;

	public static void main(String[] args) {
		try {
			LatLgnCleaner.run(LAT, LON, BUFFER);
			MetaCleaner.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
		AbstractGeolocator geolocator;
		geolocator = new RandomGeolocator(null, "C:\\Users\\João\\workspace\\SIGR\\meta.csv", "C:\\Users\\João\\workspace\\SIGR\\latlon", TRAININGSIZE);
		geolocator.run();
		System.out.println("\n #### \n");
		geolocator = new TagMatchGeolocator(null, "C:\\Users\\João\\workspace\\SIGR\\meta.csv", "C:\\Users\\João\\workspace\\SIGR\\latlon", TRAININGSIZE);
		geolocator.run();
		System.out.println("\n #### \n");
		geolocator = new IDFGeolocator(null, "C:\\Users\\João\\workspace\\SIGR\\meta.csv", "C:\\Users\\João\\workspace\\SIGR\\latlon", TRAININGSIZE);
		geolocator.run();
		System.out.println("\n #### \n");
		geolocator = new InverseConvexHullAreaGeolocator(null, "C:\\Users\\João\\workspace\\SIGR\\meta.csv", "C:\\Users\\João\\workspace\\SIGR\\latlon", TRAININGSIZE);
		geolocator.run();
	}
}
