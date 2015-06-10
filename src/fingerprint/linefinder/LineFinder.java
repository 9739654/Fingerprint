package fingerprint.linefinder;

import fingerprint.trend.Trend;
import javafx.scene.image.Image;

import java.util.*;
import java.util.function.Supplier;

public class LineFinder {
	private static final int queueSize = 5;

	private Image image;
	private LineParams params;
	private LineResult result;

	private List<Double> last = new LinkedList<>();

	public LineFinder image(Image source) {
		this.image = source;
		return this;
	}

	public LineFinder params(LineParams params) {
		this.params = params;
		return this;
	}

	public LineFinder find() {
		result = new LineResult();
		switch (params.unit) {
			case PERCENTAGE:
				for (int xIndex : params.horizontalIndexes) {
					int where = (int) (xIndex * image.getWidth() / 100.0);
					countHorizontalLines(where);
				}
				for (int yIndex : params.verticalIndexes) {
					int where = (int) (yIndex * image.getHeight() / 100.0);
					countVerticalLines(where);
				}
				break;
			case PIXEL:
				for (int xIndex : params.horizontalIndexes) {
					countHorizontalLines(xIndex);
				}
				for (int yIndex : params.verticalIndexes) {
					countVerticalLines(yIndex);
				}
				break;
		}
		last.clear();
		return this;
	}

	private int getPixelOnHorizontalLine(Image image, int lineYIndex, int pixelXIndex) {
		return image.getPixelReader().getArgb(pixelXIndex, lineYIndex);
	}

	private int getPixelOnVerticalLine(Image image, int lineXIndex, int pixelYIndex) {
		return image.getPixelReader().getArgb(lineXIndex, pixelYIndex);
	}

	/**
	 * Counts slopes from left to right
	 * @param atRow
	 */
	private void countVerticalLines(int atRow) {
		count(atRow, image::getWidth, this::getPixelOnHorizontalLine, result.vertical);
	}

	/**
	 * Counts slopes from top to bottom
	 * @param atCol
	 */
	private void countHorizontalLines(int atCol) {
		count(atCol, image::getHeight, this::getPixelOnVerticalLine, result.horizontal);
	}

	/**
	 *
	 * @param index index of row/col
	 * @param lengthFunc function that return row/col length
	 * @param results
	 */
	private void count(
			int index,
			Supplier<Double> lengthFunc,
			TriFunction<Image, Integer, Integer, Integer> valueGetter,
			Map<Integer, Integer> results) {
		int found = 0;
		int length = (int) (double) lengthFunc.get();
		fillQueue(index);
		Trend lastTrend = Trend.CONSTANT;
		for (int pixel = queueSize; pixel<length; pixel++) {
			int value = valueGetter.accept(image, index, pixel);
			feedQueue(value);
			Trend trend = Trend.of(last);
			if (lastTrend.maximum(trend)) {
				found += 1;
			}
			lastTrend = trend;
		}
		results.put(index, found);
		last.clear();
	}

	/**
	 * Inserts first elements into the queue
	 * @param row
	 */
	private void fillQueue(int row) {
		for (int pixel = 0; pixel < queueSize; pixel++) {
			int value = image
					.getPixelReader()
					.getArgb(row, pixel);
			feedQueue(value);
		}
	}

	/**
	 * Adds element to the queue and removes one element at the head
	 * @param value
	 */
	private void feedQueue(double value) {
		last.add(value);
		if (last.size() > queueSize) {
			last.remove(0);
		}
	}

	public LineResult getResult() {
		return result;
	}

}
