import geolocaters.AbstractGeolocator;
import geolocaters.TagMatchGeolocator;


public class SIGR {

	public static void main(String[] args) {
		AbstractGeolocator geolocator = new TagMatchGeolocator(null, "C:\\Users\\João\\workspace\\SIGRCleaner\\metaFiltered.csv", "C:\\Users\\João\\workspace\\SIGRCleaner\\latlonFiltered", (float) 0.9);
		geolocator.run();
	}
}
