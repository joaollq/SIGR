package photoRepresentation;

import java.util.LinkedList;

public class TagRepresentation extends AbstractPhotoRepresentation{


	public TagRepresentation(String id, String[] tags, double realLat,
			double reatLon) {
		super(id,realLat,reatLon);
		this.tags = new LinkedList<Integer>();
		for (String string : tags) {
			this.tags.add(string.hashCode());
		}
	}
}
