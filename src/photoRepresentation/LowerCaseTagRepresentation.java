package photoRepresentation;

import java.util.LinkedList;

public class LowerCaseTagRepresentation extends AbstractPhotoRepresentation {

	public LowerCaseTagRepresentation(String id, String[] tags, double realLat,
			double realLon) {
		super(id, realLat, realLon);
		this.tags = new LinkedList<Integer>();
		for (String string : tags) {
			this.tags.add(string.toLowerCase().hashCode());
		}
	}
}
