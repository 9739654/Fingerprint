package fingerprint.linefinder;

import fingerprint.trend.Trend;
import javafx.scene.image.Image;

import java.util.*;

public class LineFinder {

	;

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
		for (int index : params.horizontal) {
			countHorizontal(index);
		}
		last.clear();
		return this;
	}

	private void countHorizontal(int row) {
		int linesFound = 0;
		fillQueue(row);
		Trend lastTrend = Trend.CONSTANT;
		for (int pixel = queueSize; pixel<image.getHeight(); pixel++) {
			int value = image.getPixelReader().getArgb(row, pixel);
			feedQueue(value);
			Trend trend = Trend.of(last);
			if (lastTrend.maximum(trend)) {
				linesFound++;
			}
			lastTrend = trend;
		}
		result.horizontal.put(row, linesFound);
	}

	private void countVertical(int col) {
		int linesFound = 0;
		fillQueue(col);
		Trend lastTrend = Trend.CONSTANT;
		for (int pixel = queueSize; pixel<image.getHeight(); pixel++) {
			int value = image.getPixelReader().getArgb(pixel, col);
			feedQueue(value);
			Trend trend = Trend.of(last);
			if (lastTrend.maximum(trend)) {
				linesFound++;
			}
			lastTrend = trend;
		}
		result.horizontal.put(col, linesFound);
	}

	/**
	 * Inserts first elements into the queue
	 * @param row
	 */
	private void fillQueue(int row) {
		for (int pixel = 0; pixel < queueSize; pixel++) {
			int value = image.getPixelReader()
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
