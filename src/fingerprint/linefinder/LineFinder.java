package fingerprint.linefinder;

import fingerprint.trend.Trend;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

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
		for (int xIndex : params.horizontal) {
			countHorizontal(xIndex);
		}
		for (int yIndex : params.vertical) {
			countVertical(yIndex);
		}
		last.clear();
		return this;
	}

	private int getHorizontalPixel(Image image, int line, int pixel) {
		return image.getPixelReader().getArgb(pixel, line);
	}

	private int getVerticalPixel(Image image, int line, int pixel) {
		return image.getPixelReader().getArgb(line, pixel);
	}

	/**
	 * Counts slopes from left to right
	 * @param row
	 */
	private void countHorizontal(int row) {
		count(row, image::getWidth, this::getHorizontalPixel, result.horizontal);
	}

	/**
	 * Counts slopes from top to bottom
	 * @param col
	 */
	private void countVertical(int col) {
		count(col, image::getHeight, this::getVerticalPixel, result.vertical);
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
