package geolocators;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;

import photoRepresentation.AbstractPhotoRepresentation;
import utils.Distance;

public abstract class AbstractGeolocator {

	protected String featurespath;
	protected String metapath;
	protected String locationpath;
	protected int totalImages;
	protected float trainingPercentege;
	protected int photosInTrainingSet;

	protected List<AbstractPhotoRepresentation> photos;
	protected List<AbstractPhotoRepresentation> results;
	protected HashMap<Integer, List<AbstractPhotoRepresentation>> trainingSet;

	public AbstractGeolocator(String featurespath, String metapath,
			String locationpath, float trainingPercentage) {
		super();
		this.totalImages = 0;
		this.photosInTrainingSet = 0;
		this.featurespath = featurespath;
		this.metapath = metapath;
		this.locationpath = locationpath;
		this.trainingPercentege = trainingPercentage;
		this.results = new LinkedList<AbstractPhotoRepresentation>();
		this.photos = new LinkedList<AbstractPhotoRepresentation>();
		this.trainingSet = new HashMap<Integer, List<AbstractPhotoRepresentation>>();
	}

	public void run() {
		try {
			System.out.println("Started " + getName());
			InitializePhotos();
			GenerateTrainingSet();
			Test();
			System.out.println("Results for " + getName());
			AnalyseResults();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected abstract void InitializePhotos() throws IOException;

	protected abstract void Test();

	protected void calculateExtimatedCoordinates(
			AbstractPhotoRepresentation selectedPhoto,
			List<AbstractPhotoRepresentation> matches) {
		double x = 0;
		double y = 0;
		double z = 0;

		for (AbstractPhotoRepresentation match : matches) {
			x += Math.cos(match.getRealLat() * (Math.PI / 180))
					* Math.cos(match.getRealLon() * (Math.PI / 180));
			y += Math.cos(match.getRealLat() * (Math.PI / 180))
					* Math.sin(match.getRealLon() * (Math.PI / 180));
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

	protected void AnalyseResults() {
		double latError = 0;
		double lonError = 0;
		List<Double> kmError = new LinkedList<Double>();

		for (AbstractPhotoRepresentation photo : results) {
			latError += Math.abs(photo.getRealLat() - photo.getExtimatedLat());
			lonError += Math.abs(photo.getRealLon() - photo.getExtimatedLon());
			double distance = Distance.distance(photo.getExtimatedLat(),
					photo.getExtimatedLon(), photo.getRealLat(),
					photo.getRealLon());
			if(Double.isNaN(distance)) {
				System.out.println(distance);
			}
			kmError.add(distance);
		}

		System.out.println("Avg latitude error (in degrees) = " + latError
				/ results.size());

		System.out.println("Avg longitude error (in degrees) = " + lonError
				/ results.size());

		Collections.sort(kmError);
		double avgKm = 0;

		for (double double1 : kmError) {
			if(Double.isNaN(double1)) {
				System.out.println(double1);
			} else {
				avgKm += double1;
			}
			if(Double.isNaN(avgKm)) {
				System.out.println(double1);
			}
		}

		System.out.println("Avg error (in km) = " + avgKm / kmError.size());

		System.out.println("Median error (in km) = "
				+ kmError.get(kmError.size() / 2));

		Plot2DPanel plot = new Plot2DPanel();

		double[] yAxis = new double[kmError.size()];
		double[] xAxis = new double[kmError.size()];

		for (int i = 0; i < kmError.size(); i++) {
			yAxis[i] = kmError.get(i);
			xAxis[i] = i;
		}

		plot.addLinePlot("Error", xAxis, yAxis);
		plot.setAxisLabel(1, "Km");

		JFrame frame = new JFrame("Error plot for " + getName());
		frame.setSize(700, 700);
		frame.setContentPane(plot);
		frame.setVisible(true);
	}

	protected void GenerateTrainingSet() {
		int imagesForTraining = (int) (photos.size() * trainingPercentege);
		Random random = new Random(new Date().getTime());

		while (imagesForTraining > 0) {

			AbstractPhotoRepresentation selectedPhoto = photos.remove(random
					.nextInt(photos.size()));

			List<Integer> tags = selectedPhoto.getTags();

			for (Integer tag : tags) {
				if (!trainingSet.containsKey(tag)) {
					trainingSet.put(tag,
							new LinkedList<AbstractPhotoRepresentation>());
				}
				trainingSet.get(tag).add(selectedPhoto);
			}
			photosInTrainingSet++;
			imagesForTraining--;
		}
	}
	
	protected abstract String getName();
}
