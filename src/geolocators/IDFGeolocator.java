package geolocators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import photoRepresentation.AbstractPhotoRepresentation;
import photoRepresentation.LowerCaseTagRepresentation;
import utils.PhotoLocation;

public class IDFGeolocator extends AbstractGeolocator {

    int totalImages;

    public IDFGeolocator(BufferedWriter bw, String metapath, String locationpath, float trainingSetSize) {
        super(bw, metapath, locationpath, trainingSetSize);
    }

    @Override
    protected void Test() {
        while (!photos.isEmpty()) {
            AbstractPhotoRepresentation selectedPhoto = photos.remove(0);
            double accumulatedIDF = 0;
            int matches = 0;

            double extimatedLat = 0;
            double extimatedLon = 0;

            List<Integer> tags = selectedPhoto.getTags();

            for (Integer tag : tags) {
                if (trainingSet.containsKey(tag)) {
                    double idf = Math.log(photosInTrainingSet / trainingSet.get(tag).size());
                    for (AbstractPhotoRepresentation photo : trainingSet.get(tag)) {
                        matches++;
                        double x =
                                Math.cos(photo.getRealLat() * (Math.PI / 180)) * Math.cos(photo.getRealLon() * (Math.PI / 180));
                        double y =
                                Math.cos(photo.getRealLat() * (Math.PI / 180)) * Math.sin(photo.getRealLon() * (Math.PI / 180));
                        double z = Math.sin(photo.getRealLat() * (Math.PI / 180));

                        extimatedLon += Math.atan2(y, x) * (idf) * (180 / Math.PI);
                        double hyp = Math.sqrt(x * x + y * y);
                        extimatedLat += Math.atan2(z, hyp) * idf * (180 / Math.PI);
                        accumulatedIDF += idf;
                    }
                }
            }

            if (matches > 0) {
                extimatedLat = extimatedLat / accumulatedIDF;
                extimatedLon = extimatedLon / accumulatedIDF;

                selectedPhoto.setExtimatedLat(extimatedLat);
                selectedPhoto.setExtimatedLon(extimatedLon);

                results.add(selectedPhoto);
            }
        }
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
    protected String getName() {
        return "IDF Geolocator";
    }
}
