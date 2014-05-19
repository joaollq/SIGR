package geolocators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import photoRepresentation.AbstractPhotoRepresentation;
import photoRepresentation.LowerCaseTagRepresentation;
import utils.PhotoLocation;

import com.vividsolutions.jts.algorithm.ConvexHull;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class InverseConvexHullAreaGeolocator extends AbstractGeolocator {

    Map<Integer, Double> area;

    public InverseConvexHullAreaGeolocator(BufferedWriter bw, String metapath, String locationpath, float trainingPercentage) {
        super(bw, metapath, locationpath, trainingPercentage);
        area = new HashMap<Integer, Double>();
    }

    @Override
    protected void InitializePhotos() throws IOException {
        BufferedReader metaReader = new BufferedReader(new FileReader(metapath));
        BufferedReader locationReader = new BufferedReader(new FileReader(locationpath));

        HashMap<String, PhotoLocation> photoLocation = new HashMap<String, PhotoLocation>();

        String line = null;
        while ((line = locationReader.readLine()) != null) {
            String[] linesplit = line.split(" ");
            photoLocation
                    .put(linesplit[0], new PhotoLocation(Double.parseDouble(linesplit[1]), Double.parseDouble(linesplit[2])));
        }

        while ((line = metaReader.readLine()) != null) {
            String[] linesplit = line.split(",");
            if (linesplit[4].startsWith("\"") && !linesplit[4].equals("\"\"")) {
                String id = linesplit[0];
                photos.add(new LowerCaseTagRepresentation(id, linesplit[4].split(" "), photoLocation.get(id).getLat(),
                        photoLocation.get(id).getLon()));
                totalImages++;
            }
        }

        metaReader.close();
        locationReader.close();
    }

    @Override
    protected void GenerateTrainingSet() {
        super.GenerateTrainingSet();
        GeometryFactory factory = new GeometryFactory();
        for (Integer tag : trainingSet.keySet()) {
            int i = 0;
            Geometry[] points = new Geometry[trainingSet.get(tag).size()];
            for (AbstractPhotoRepresentation photo : trainingSet.get(tag)) {
                Coordinate[] coordinates = new Coordinate[1];
                coordinates[0] = new Coordinate(photo.getRealLon(), photo.getRealLat());
                points[i++] = new Point(new CoordinateArraySequence(coordinates), factory);
            }
            Geometry geometry = new GeometryCollection(points, factory);
            ConvexHull hull = new ConvexHull(geometry);
            area.put(tag, hull.getConvexHull().getArea());
        }
    };

    @Override
    protected void Test() {
        while (!photos.isEmpty()) {
            AbstractPhotoRepresentation selectedPhoto = photos.remove(0);
            double accumulatedArea = 0;
            int matches = 0;

            double extimatedLat = 0;
            double extimatedLon = 0;

            List<Integer> tags = selectedPhoto.getTags();

            for (Integer tag : tags) {
                if (trainingSet.containsKey(tag)) {
                    double hullArea = 1 / (area.get(tag) + 1);
                    for (AbstractPhotoRepresentation photo : trainingSet.get(tag)) {
                        matches++;
                        double x =
                                Math.cos(photo.getRealLat() * (Math.PI / 180)) * Math.cos(photo.getRealLon() * (Math.PI / 180));
                        double y =
                                Math.cos(photo.getRealLat() * (Math.PI / 180)) * Math.sin(photo.getRealLon() * (Math.PI / 180));
                        double z = Math.sin(photo.getRealLat() * (Math.PI / 180));

                        extimatedLon += Math.atan2(y, x) * (hullArea) * (180 / Math.PI);
                        double hyp = Math.sqrt(x * x + y * y);
                        extimatedLat += Math.atan2(z, hyp) * hullArea * (180 / Math.PI);
                        accumulatedArea += hullArea;
                    }
                }
            }

            if (matches > 0) {
                extimatedLat = extimatedLat / accumulatedArea;
                extimatedLon = extimatedLon / accumulatedArea;

                selectedPhoto.setExtimatedLat(extimatedLat);
                selectedPhoto.setExtimatedLon(extimatedLon);

                results.add(selectedPhoto);
            }
        }
    }

    @Override
    protected String getName() {
        return "Inverse Convex Hull Area Geolocator";
    }

}
