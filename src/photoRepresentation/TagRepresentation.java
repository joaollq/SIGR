package photoRepresentation;

import java.util.LinkedList;
import java.util.List;

public class TagRepresentation {

	private String id;
	private List<Integer> tags;
	private double realLat;
	private double reatLon;
	private double extimatedLat;
	private double extimatedLon;

	public TagRepresentation(String id, String[] tags, double realLat,
			double reatLon) {
		super();
		this.id = id;
		this.tags = new LinkedList<Integer>();
		for (String string : tags) {
			this.tags.add(string.hashCode());
		}
		this.realLat = realLat;
		this.reatLon = reatLon;
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
		return reatLon;
	}

}
