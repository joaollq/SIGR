package geolocators;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;

import photoRepresentation.AbstractPhotoRepresentation;
import utils.Distance;

public abstract class AbstractGeolocator {

    protected BufferedWriter bw;
    protected String metapath;
    protected String locationpath;
    protected int totalImages;
    protected float trainingPercentege;
    protected int photosInTrainingSet;
    protected String graphName;

    protected List<AbstractPhotoRepresentation> photos;
    protected List<AbstractPhotoRepresentation> results;
    protected HashMap<Integer, List<AbstractPhotoRepresentation>> trainingSet;

    public AbstractGeolocator(BufferedWriter bw, String metapath, String locationpath, float trainingPercentage, String graphName) {
        super();
        this.totalImages = 0;
        this.photosInTrainingSet = 0;
        this.bw = bw;
        this.metapath = metapath;
        this.locationpath = locationpath;
        this.graphName = graphName;
        this.trainingPercentege = trainingPercentage;
        this.results = new LinkedList<AbstractPhotoRepresentation>();
        this.photos = new LinkedList<AbstractPhotoRepresentation>();
        this.trainingSet = new HashMap<Integer, List<AbstractPhotoRepresentation>>();
    }

    public void run() {
        try {
            bw.write("Started " + getName() + "\n");
            System.out.println("Started");
            InitializePhotos();
            System.out.println("Initialized");
            GenerateTrainingSet();
            System.out.println("Going to test");
            Test();
            bw.write("Results for " + getName() + "\n");
            AnalyseResults();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void InitializePhotos() throws IOException;

    protected abstract void Test();

    protected void calculateExtimatedCoordinates(AbstractPhotoRepresentation selectedPhoto,
            List<AbstractPhotoRepresentation> matches) {
        double x = 0;
        double y = 0;
        double z = 0;

        for (AbstractPhotoRepresentation match : matches) {
            x += Math.cos(match.getRealLat() * (Math.PI / 180)) * Math.cos(match.getRealLon() * (Math.PI / 180));
            y += Math.cos(match.getRealLat() * (Math.PI / 180)) * Math.sin(match.getRealLon() * (Math.PI / 180));
            z += Math.sin(match.getRealLat() * (Math.PI / 180));

        }

        x = x / matches.size();
        y = y / matches.size();
        z = z / matches.size();

        double extimatedLon = Math.atan2(y, x) * (180 / Math.PI);
        selectedPhoto.setExtimatedLon(extimatedLon);

        double hyp = Math.sqrt(x * x + y * y);
        double extimatedLat = Math.atan2(z, hyp) * (180 / Math.PI);
        selectedPhoto.setExtimatedLat(extimatedLat);

        results.add(selectedPhoto);
    }

    protected void AnalyseResults() throws IOException {
        double latError = 0;
        double lonError = 0;
        List<Double> kmError = new LinkedList<Double>();
        int[] bins = new int[6];

        for (AbstractPhotoRepresentation photo : results) {
            latError += Math.abs(photo.getRealLat() - photo.getExtimatedLat());
            lonError += Math.abs(photo.getRealLon() - photo.getExtimatedLon());
            double distance =
                    Distance.distance(photo.getExtimatedLat(), photo.getExtimatedLon(), photo.getRealLat(), photo.getRealLon());
            kmError.add(distance);
        }

        bw.write("Avg latitude error (in degrees) = " + latError / results.size() + "\n");

        bw.write("Avg longitude error (in degrees) = " + lonError / results.size() + "\n");

        Collections.sort(kmError);
        double avgKm = 0;

        for (double double1 : kmError) {
            avgKm += double1;
            if (double1 < 0.05) {
                bins[0]++;
            } else if (double1 < 0.25) {
                bins[1]++;
            } else if (double1 < 1) {
                bins[2]++;
            } else if (double1 < 10) {
                bins[3]++;
            } else if (double1 < 50) {
                bins[4]++;
            } else {
                bins[5]++;
            }

        }

        bw.write("Total images = " + totalImages + "\n");
        bw.write("Avg error (in km) = " + avgKm / kmError.size() + "\n");

        bw.write("Median error (in km) = " + kmError.get(kmError.size() / 2) + "\n");

        bw.write("Number of points within less than 50 m = " + bins[0] + "\n");
        bw.write("Number of points between 50 m and 250 m = " + bins[1] + "\n");
        bw.write("Number of points between 250 m and 1 km = " + bins[2] + "\n");
        bw.write("Number of points between 1 km and 10 km = " + bins[3] + "\n");
        bw.write("Number of points between 10 km and 50 km = " + bins[4] + "\n");
        bw.write("Number of points at more than 50 km km = " + bins[5] + "\n");

        Plot2DPanel plot = new Plot2DPanel();

        double[] yAxis = new double[kmError.size()];
        double[] xAxis = new double[kmError.size()];

        for (int i = 0; i < kmError.size(); i++) {
            yAxis[i] = kmError.get(i);
            xAxis[i] = i;
        }

        plot.addLinePlot("Error", xAxis, yAxis);
        plot.setAxisLabel(1, "Km");
        double minAxis[] = { 0d, 0d };
        double maxAxis[] = { 45000d, 400d };
        plot.setFixedBounds(minAxis, maxAxis);

        JFrame frame = new JFrame("Error plot for " + getName());
        frame.setSize(700, 700);
        frame.setContentPane(plot);
        frame.setVisible(true);
        BufferedImage image = new BufferedImage(700, 700, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        frame.paint(graphics);
        ImageIO.write(image, "png", new File("plots//" + graphName + "_" + getName() + ".png"));
    }

    protected void GenerateTrainingSet() {
        int imagesForTraining = (int) (photos.size() * trainingPercentege);
        Random random = new Random(new Date().getTime());

        while (imagesForTraining > 0) {

            AbstractPhotoRepresentation selectedPhoto = photos.remove(random.nextInt(photos.size()));

            List<Integer> tags = selectedPhoto.getTags();

            for (Integer tag : tags) {
                if (!trainingSet.containsKey(tag)) {
                    trainingSet.put(tag, new LinkedList<AbstractPhotoRepresentation>());
                }
                trainingSet.get(tag).add(selectedPhoto);
            }
            photosInTrainingSet++;
            imagesForTraining--;
        }
    }

    protected abstract String getName();
}
