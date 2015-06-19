package fingerprint.test;

import java.io.File;

/**
 * Created by Sebastian on 2015-06-10.
 */
public class FingerprintData {
	private String name;
	private int[] verticalData = new int[9];
	private int[] horizontalData = new int[9];
	private double horizontalAverage = 0;
	private double verticalAverage = 0;
	private double average = 0;
	private File file;

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int[] getVerticalData() {
		return verticalData;
	}

	public int getVerticalData(int index) {
		return verticalData[index];
	}

	public void setVerticalData(int[] verticalData) {
		this.verticalData = verticalData;
	}

	public void setVerticalData(int index, int value) {
		this.verticalData[index] = value;
	}

	public int[] getHorizontalData() {
		return horizontalData;
	}

	public int getHorizontalData(int index) {
		return horizontalData[index];
	}

	public void setHorizontalData(int[] horizontalData) {
		this.horizontalData = horizontalData;
	}

	public void setHorizontalData(int index, int value) {
		this.horizontalData[index] = value;
	}

	public double getHorizontalAverage() {
		return horizontalAverage;
	}

	public void setHorizontalAverage(double horizontalAverage) {
		this.horizontalAverage = horizontalAverage;
	}

	public double getVerticalAverage() {
		return verticalAverage;
	}

	public void setVerticalAverage(double verticalAverage) {
		this.verticalAverage = verticalAverage;
	}

	public void calcutateVerticalAverage() {
		int average = 0;
		for (int i = 0; i < verticalData.length; i++) {
			average += verticalData[i];
		}
		this.verticalAverage = average / 3;
	}

	public void calcutateHorizontalAverage() {
		int average = 0;
		for (int i = 0; i < horizontalData.length; i++) {
			average += horizontalData[i];
		}
		this.horizontalAverage = average / 3;
	}

	public double getAverage() {
		return average;
	}

	public void setAverage(double average) {
		this.average = average;
	}

	public void calculateAverage() {
		average = (horizontalAverage + verticalAverage) / 2;
	}
}
