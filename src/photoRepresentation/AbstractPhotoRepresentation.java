package photoRepresentation;

import java.util.List;

public abstract class AbstractPhotoRepresentation {

	protected String id;
	protected List<Integer> tags;
	protected double realLat;
	protected double realLon;
	protected double extimatedLat;
	protected double extimatedLon;

	public AbstractPhotoRepresentation(String id, double realLat, double realLon) {
		super();
		this.id = id;
		this.realLat = realLat;
		this.realLon = realLon;
	}

	public double getExtimatedLat() {
		return extimatedLat;
	}

	public void setExtimatedLat(double extimatedLat) {
		this.extimatedLat = extimatedLat;
	}

	public double getExtimatedLon() {
		return extimatedLon;
	}

	public void setExtimatedLon(double extimatedLon) {
		this.extimatedLon = extimatedLon;
	}

	public String getId() {
		return id;
	}

	public List<Integer> getTags() {
		return tags;
	}

	public double getRealLat() {
		return realLat;
	}

	public double getRealLon() {
		return realLon;
	}
	
	

}
