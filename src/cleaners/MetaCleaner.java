package cleaners;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class MetaCleaner {

    public static void run() throws IOException {

        String fileNames[] = { "_1", "_2", "_3", "_4", "_5", "_6", "_7", "_8", "_9", "_missingBlocks" };

        BufferedReader brLatLon = null;
        BufferedReader reader = null;
        String dataLine;
        HashSet<String> id = new HashSet<String>();
        int num = 0;

        brLatLon = new BufferedReader(new FileReader("latlon"));

        String line;
        while ((line = brLatLon.readLine()) != null) {
            id.add(line.split(" ")[0]);
        }

        brLatLon.close();
        int namesIndexLatLon = 0;

        // Create output file
        File fileMeta = new File("meta.csv");
        if (!fileMeta.exists()) {
            fileMeta.createNewFile();
        }

        // Open Writer
        FileWriter fwMeta = new FileWriter(fileMeta.getAbsoluteFile());
        BufferedWriter writer = new BufferedWriter(fwMeta);

        reader = new BufferedReader(new FileReader("meta.csv"));

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
                    reader = new BufferedReader(new FileReader("metadata" + fileNames[namesIndexLatLon++] + ".csv"));
                    dataLine = reader.readLine();
                }
                String[] latlonSplit = dataLine.split(",");
                if (id.contains(latlonSplit[0])) {
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
