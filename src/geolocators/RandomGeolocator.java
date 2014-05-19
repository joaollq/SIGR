package geolocators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import photoRepresentation.AbstractPhotoRepresentation;
import photoRepresentation.LowerCaseTagRepresentation;
import utils.PhotoLocation;

public class RandomGeolocator extends AbstractGeolocator {

    double lat = 51.507222;
    double lon = -0.1275;
    double limit = 1.5;

    public RandomGeolocator(BufferedWriter bw, String metapath, String locationpath, float trainingPercentage, double lat,
            double lon, double limit, String graphName) {
        super(bw, metapath, locationpath, trainingPercentage, graphName);
        this.lat = lat;
        this.lon = lon;
        this.limit = limit;
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
    protected void Test() {
        int testSize = (int) (totalImages * (1 - trainingPercentege));
        Random random = new Random();
        int[] signal = { -1, 1 };

        for (int i = 0; i < testSize; i++) {
            AbstractPhotoRepresentation seleceted = photos.remove(random.nextInt(photos.size()));

            seleceted.setExtimatedLat(lat + signal[random.nextInt(2)] * random.nextDouble() * limit);
            seleceted.setExtimatedLon(lon + signal[random.nextInt(2)] * random.nextDouble() * limit);
            results.add(seleceted);
        }
    }

    @Override
    protected void GenerateTrainingSet() {
        // nothing to do
    }

    @Override
    protected String getName() {
        return "Random Geolocator";
    }

}
