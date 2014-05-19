package cleaners;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LatLgnCleaner {

    public static void run(double lat, double lon, double limit) throws IOException {

        String fileNames[] = { "", "_missingBlocks" };

        BufferedReader reader = null;
        String dataLine;

        int namesIndexLatLon = 0;

        // Create output file
        File fileLatLon = new File("latlon");
        if (!fileLatLon.exists()) {
            fileLatLon.createNewFile();
        }

        //Open Writer
        FileWriter fwLatLon = new FileWriter(fileLatLon.getAbsoluteFile());
        BufferedWriter writer = new BufferedWriter(fwLatLon);

        reader = new BufferedReader(new FileReader("training" + fileNames[namesIndexLatLon++] + "_latlng"));

        // First Line without data
        reader.readLine();
        while (true) {
            try {
                dataLine = reader.readLine();
                if (dataLine == null) {
                    if (namesIndexLatLon >= fileNames.length) {
                        break;
                    }
                    reader.close();
                    reader = new BufferedReader(new FileReader("training" + fileNames[namesIndexLatLon++] + "_latlng"));
                    dataLine = reader.readLine();
                    dataLine = reader.readLine();
                }
                String[] latlonSplit = dataLine.split(" ");
                double longitude = Double.parseDouble(latlonSplit[2]);
                double latitude = Double.parseDouble(latlonSplit[1]);
                if (latitude > lat + limit || latitude < lat - limit || longitude > lon + limit || longitude < lon - limit) {
                    continue;
                } else {
                    writer.write(dataLine + "\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            writer.flush();
        }

        writer.close();

    }
}
