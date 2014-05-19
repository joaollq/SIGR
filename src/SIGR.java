import geolocators.AbstractGeolocator;
import geolocators.IDFGeolocator;
import geolocators.InverseConvexHullAreaGeolocator;
import geolocators.RandomGeolocator;
import geolocators.TagMatchGeolocator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cleaners.LatLgnCleaner;
import cleaners.MetaCleaner;

public class SIGR {
    static double BUFFER = 1.5;

    public static void main(String[] args) {

        double[] lat = { 40.714550, 38.725670 };
        double[] lon = { -74.007118, -9.150370 };
        float[] trainingSize = { (float) 0.95, (float) 0.9, (float) 0.8, (float) 0.7, (float) 0.5 };

        String[] fileName = { "ny", "lisbon" };

        for (int j = 0; j < lat.length; j++) {

            for (float size : trainingSize) {

                try {
                    LatLgnCleaner.run(lat[j], lon[j], 1.5);
                    MetaCleaner.run();
                    File output = new File("docs//" + fileName[j] + "_" + size);

                    output.createNewFile();

                    BufferedWriter bw = new BufferedWriter(new FileWriter(output));
                    AbstractGeolocator geolocator;
                    geolocator = new RandomGeolocator(bw, "meta.csv", "latlon", size, lat[j], lon[j], 1.5);
                    geolocator.run();
                    bw.write("\n #### \n");
                    geolocator = new TagMatchGeolocator(bw, "meta.csv", "latlon", size);
                    geolocator.run();
                    bw.write("\n #### \n");
                    geolocator = new IDFGeolocator(bw, "meta.csv", "latlon", size);
                    geolocator.run();
                    bw.write("\n #### \n");
                    geolocator = new InverseConvexHullAreaGeolocator(bw, "meta.csv", "latlon", size);
                    geolocator.run();
                    bw.write("\n\n");
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
