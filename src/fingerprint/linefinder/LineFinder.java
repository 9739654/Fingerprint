package fingerprint.linefinder;

import fingerprint.trend.Trend;
import javafx.scene.image.Image;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class LineFinder {
	private static final int queueSize = 5;
	private static final int beginIndex = queueSize;
	private static Function<Integer, Double> rgbFunction;

	static {
		rgbFunction = LineFinder::rgbToAvarage;
	}

	private Image image;
	private LineParams params;
	private LineResult result;

	private List<Double> last = new LinkedList<>();

	public LineFinder image(Image source) {
		this.image = source;
		checkParams();
		return this;
	}

	public LineFinder params(LineParams params) {
		this.params = params;
		checkParams();
		return this;
	}

	private void checkParams() {
		if (params != null && image != null) {
			for (int horizontalIndex : params.horizontalIndexes) {
				if (horizontalIndex >= image.getWidth()) {
					System.err.println("Horizontal index too high");
				}
			}
			for (int verticalIndex : params.verticalIndexes) {
				if (verticalIndex >= image.getHeight()) {
					System.err.println("Vertical index too high");
				}
			}
		}
	}

	public LineFinder find() {
		result = new LineResult();
		List<Integer> horizontalIndexes = null;
		List<Integer> verticalIndexes = null;
		switch (params.unit) {
			case PERCENTAGE:
				horizontalIndexes = new ArrayList<>();
				verticalIndexes = new ArrayList<>();
				for (int xIndex : params.horizontalIndexes) {
					int where = (int) (xIndex / 100.0 * image.getWidth());
					horizontalIndexes.add(where);
				}
				for (int yIndex : params.verticalIndexes) {
					int where = (int) (yIndex / 100.0 * image.getHeight());
					verticalIndexes.add(where);
				}
				break;
			case PIXEL:
				for (int xIndex : params.horizontalIndexes) {
					horizontalIndexes.add(xIndex);
				}
				for (int yIndex : params.verticalIndexes) {
					verticalIndexes.add(yIndex);
				}
				break;
		}
		horizontalIndexes.forEach(this::countHorizontalLines);
		verticalIndexes.forEach(this::countVerticalLines);
		last.clear();
		return this;
	}

	/**
	 * Gets pixel value
	 * @param lineIndex y coordinate of pixel
	 * @param pixelIndex x coordinate of pixel
	 * @return
	 */
	private int getPixelOnHorizontalLine(int lineIndex, int pixelIndex) {
		return image
				.getPixelReader()
				.getArgb(pixelIndex, lineIndex);
	}

	/**
	 * Gets pixel value
	 * @param lineIndex x coordinate of pixel
	 * @param pixelIndex y coordinate of pixel
	 * @return
	 */
	private int getPixelOnVerticalLine(int lineIndex, int pixelIndex) {
		return image
				.getPixelReader()
				.getArgb(lineIndex, pixelIndex);
	}

	/**
	 * Counts slopes from left to right
	 * @param atRow
	 */
	private void countVerticalLines(int atRow) {
		count(atRow, image::getWidth, this::getPixelOnHorizontalLine, result.verticalLines);
	}

	/**
	 * Counts slopes from top to bottom
	 * @param atCol
	 */
	private void countHorizontalLines(int atCol) {
		count(atCol, image::getHeight, this::getPixelOnVerticalLine, result.horizontalLines);
	}

	/**
	 *
	 * @param lineIndex lineIndex of row/col
	 * @param lineLengthFunc function that return row/col length
	 * @param results
	 */
	private void count(
			int lineIndex,
			Supplier<Double> lineLengthFunc,
			BiFunction<Integer, Integer, Integer> pixelGetter,
			Map<Integer, Integer> results) {
		int found = 0;
		int lineLength = (int) (double) lineLengthFunc.get();
		initQueue(lineIndex, pixelGetter);
		Trend lastTrend = Trend.CONSTANT;
		for (int pixel = beginIndex; pixel<lineLength; pixel++) {
			int value = pixelGetter.apply(lineIndex, pixel);
			feedQueue(value);
			Trend trend = Trend.of(last);
			if (lastTrend.maximum(trend)) {
				found += 1;
			}
			lastTrend = trend;
		}
		results.put(lineIndex, found);
		last.clear();
	}

	/**
	 * Inserts first elements into the queue
	 * @param line
	 */
	private void initQueue(int line, BiFunction<Integer, Integer, Integer> pixelGetter) {
		for (int pixel = 0; pixel < queueSize; pixel++) {
			int rgb = pixelGetter.apply(line, pixel);
			double result = rgbFunction.apply(rgb);
			feedQueue(result);
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

	/**
	 * Calculates the avarage of RGB
	 * @param rgb
	 * @return 1/3 * (r + g + b)
	 */
	public static final double rgbToAvarage(int rgb) {
		int r, g, b;
		r = 0xFF & (rgb >> 16);
		g = 0xFF & (rgb >> 8);
		b = 0xFF & (rgb >> 0);
		double avarage;
		avarage = (r + g + b) * 0.333333333333333333333;
		return avarage;
	}

	/**
	 * Calculates V from HSV model given RGB values
	 * @param rgb
	 * @return Value (HSV model) in range <0, 1>
	 */
	public static final double rgbToValue(int rgb) {
		int r,g,b;
		r = 0xFF * (rgb >> 16);
		g = 0xFF * (rgb >> 8);
		b = 0xFF * (rgb >> 0);
		double value;
		value = Math.max(Math.max(r, g), b);
		value = value / 255.0 * 100.0;
		return value;
	}

	public LineResult getResult() {
		return result;
	}

}
