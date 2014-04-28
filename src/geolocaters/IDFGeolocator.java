package geolocaters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import photoRepresentation.LowerCaseTagRepresentation;
import utils.Distance;

public class IDFGeolocator extends AbstractGeolocator {

	private List<LowerCaseTagRepresentation> photos;

	private HashMap<Integer, List<LowerCaseTagRepresentation>> trainingSet;

	private List<LowerCaseTagRepresentation> results;

	private List<Double> error;

	int totalImages;

	public IDFGeolocator(String featurespath,
			String metapath, String locationpath, float trainingSetSize) {
		super(featurespath, metapath, locationpath, trainingSetSize);
		photos = new LinkedList<LowerCaseTagRepresentation>();
		trainingSet = new HashMap<>();
		results = new LinkedList<LowerCaseTagRepresentation>();
		error = new LinkedList<Double>();
	}

	@Override
	public void run() {
		try {
			System.out.println("Starting program");
			InitializePhotos();
			System.out.println("Initialized photos. Found " + totalImages);
			System.out.println("Going to generate training set");
			GenerateTrainingSet();
			System.out.println("Training set generated");
			System.out.println("Going to test the model");
			Test();
			System.out.println("Tested the model");
			AnalyseResults();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void AnalyseResults() {
		double latError = 0;
		double lonError = 0;
		double kmError = 0;

		for (LowerCaseTagRepresentation photo : results) {
			latError += Math.abs(photo.getRealLat() - photo.getExtimatedLat());
			lonError += Math.abs(photo.getRealLon() - photo.getExtimatedLon());
			double distance = Distance.distance(photo.getRealLat(),
					photo.getRealLon(), photo.getExtimatedLat(),
					photo.getExtimatedLon());
			kmError += distance;
			error.add(distance);
		}

		System.out.println("Avg latitude error (in degrees) = " + latError
				/ results.size());

		System.out.println("Avg longitude error (in degrees) = " + lonError
				/ results.size());

		System.out.println("Avg error (in km) = " + kmError / results.size());

		Collections.sort(error);
		System.out.println("Median error (in km) = "
				+ error.get(error.size() / 2));

	}

	private void Test() {
		int ignored = 0;
		while (!photos.isEmpty()) {
			LowerCaseTagRepresentation selectedPhoto = photos.remove(0);

			List<Integer> tags = selectedPhoto.getTags();
			List<LowerCaseTagRepresentation> matches = new LinkedList<LowerCaseTagRepresentation>();

			for (Integer tag : tags) {
				if (trainingSet.containsKey(tag)) {
					matches.addAll(trainingSet.get(tag));
				}
			}

			if (!matches.isEmpty()) {
				double avgLat = 0;
				double avgLon = 0;

				for (LowerCaseTagRepresentation match : matches) {
					avgLat += match.getRealLat();
					avgLon += match.getRealLon();
				}

				selectedPhoto.setExtimatedLat(avgLat / matches.size());
				selectedPhoto.setExtimatedLon(avgLon / matches.size());

				results.add(selectedPhoto);
			} else {
				ignored++;
			}
			System.out.println("Remaining photos to be tested = "
					+ photos.size());
		}

		System.out.println("Found " + ignored + " photos without matches");
	}

	private void GenerateTrainingSet() {
		int imagesForTraining = (int) (photos.size() * trainingSetSize);
		Random random = new Random(new Date().getTime());

		while (imagesForTraining > 0) {
			System.out.println("Images until training set finished = "
					+ imagesForTraining);

			LowerCaseTagRepresentation selectedPhoto = photos.remove(random
					.nextInt(photos.size()));

			List<Integer> tags = selectedPhoto.getTags();

			for (Integer tag : tags) {
				if (!trainingSet.containsKey(tag)) {
					trainingSet.put(tag,
							new LinkedList<LowerCaseTagRepresentation>());
				}
				trainingSet.get(tag).add(selectedPhoto);
			}

			imagesForTraining--;
		}

	}

	private void InitializePhotos() throws IOException {
		BufferedReader metaReader = new BufferedReader(new FileReader(metapath));
		BufferedReader locationReader = new BufferedReader(new FileReader(
				locationpath));

		HashMap<String, PhotoLocation> photoLocation = new HashMap<String, IDFGeolocator.PhotoLocation>();

		String line = null;
		int imagesIgnored = 0;

		while ((line = locationReader.readLine()) != null) {
			String[] linesplit = line.split(" ");
			photoLocation.put(
					linesplit[0],
					new PhotoLocation(Double.parseDouble(linesplit[1]), Double
							.parseDouble(linesplit[2])));
		}

		while ((line = metaReader.readLine()) != null) {
			String[] linesplit = line.split(",");
			if (linesplit[4].startsWith("\"") && !linesplit[4].equals("\"\"")) {
				String id = linesplit[0];
				photos.add(new LowerCaseTagRepresentation(id, linesplit[4]
						.split(" "), photoLocation.get(id).getLat(),
						photoLocation.get(id).getLon()));
				totalImages++;
			} else {
				imagesIgnored++;
			}
		}

		metaReader.close();
		locationReader.close();
		System.out.println("Found " + imagesIgnored + " without tags");

	}

	class PhotoLocation {
		private double lat;
		private double lon;

		public PhotoLocation(double lat, double lon) {
			super();
			this.lat = lat;
			this.lon = lon;
		}

		public double getLat() {
			return lat;
		}

		public void setLat(double lat) {
			this.lat = lat;
		}

		public double getLon() {
			return lon;
		}

		public void setLon(double lon) {
			this.lon = lon;
		}

	}
}
