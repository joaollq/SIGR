import geolocaters.AbstractGeolocator;
import geolocaters.TagMatchGeolocator;


public class SIGR {

	public static void main(String[] args) {
		AbstractGeolocator geolocator = new TagMatchGeolocator(null, "C:\\Users\\Jo�o\\workspace\\SIGRCleaner\\metaFiltered.csv", "C:\\Users\\Jo�o\\workspace\\SIGRCleaner\\latlonFiltered", (float) 0.9);
		geolocator.run();
	}
}
