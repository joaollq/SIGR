package geolocaters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import photoRepresentation.AbstractPhotoRepresentation;
import photoRepresentation.LowerCaseTagRepresentation;
import utils.PhotoLocation;

public class TagMatchTermFrequencyGeolocator extends AbstractGeolocator {


	public TagMatchTermFrequencyGeolocator(String featurespath,
			String metapath, String locationpath, float trainingSetSize) {
		super(featurespath, metapath, locationpath, trainingSetSize);
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


	protected void Test() {
		int ignored = 0;
		while (!photos.isEmpty()) {
			AbstractPhotoRepresentation selectedPhoto = photos.remove(0);

			List<Integer> tags = selectedPhoto.getTags();
			List<AbstractPhotoRepresentation> matches = new LinkedList<AbstractPhotoRepresentation>();

			for (Integer tag : tags) {
				if (trainingSet.containsKey(tag)
						&& trainingSet.get(tag).size() / totalImages < 0.5) {
					matches.addAll(trainingSet.get(tag));
				}
			}

			if (!matches.isEmpty()) {
				calculateExtimatedCoordinates(selectedPhoto, matches);
			} else {
				ignored++;
			}
			System.out.println("Remaining photos to be tested = "
					+ photos.size());
		}

		System.out.println("Found " + ignored + " photos without matches");
	}

	

	protected void InitializePhotos() throws IOException {
		BufferedReader metaReader = new BufferedReader(new FileReader(metapath));
		BufferedReader locationReader = new BufferedReader(new FileReader(
				locationpath));

		HashMap<String, PhotoLocation> photoLocation = new HashMap<String, PhotoLocation>();

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
}
