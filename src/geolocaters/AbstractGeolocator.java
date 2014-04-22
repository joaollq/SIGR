package geolocaters;

public abstract class AbstractGeolocator {

	protected String featurespath;
	protected String metapath;
	protected String locationpath;
	protected float trainingSetSize;
	
	public AbstractGeolocator(String featurespath, String metapath,
			String locationpath, float trainingSetSize) {
		super();
		this.featurespath = featurespath;
		this.metapath = metapath;
		this.locationpath = locationpath;
		this.trainingSetSize = trainingSetSize;
	}
	
	
	
	
	public abstract void run();
}
