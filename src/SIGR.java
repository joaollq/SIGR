import utils.Distance;
import geolocaters.AbstractGeolocator;
import geolocaters.TagMatchGeolocator;
import geolocaters.TagMatchTermFrequencyGeolocator;


public class SIGR {

	public static void main(String[] args) {
		AbstractGeolocator geolocator = new TagMatchTermFrequencyGeolocator(null, "C:\\Users\\Jo�o\\workspace\\SIGRCleaner\\metaFiltered.csv", "C:\\Users\\Jo�o\\workspace\\SIGRCleaner\\latlonFiltered", (float) 0.9);
		geolocator.run();
	}
}
