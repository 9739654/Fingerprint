package fingerprint;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

/**
 * @author mati
 */
public class ImageFilters {
	BufferedImage source;
	BufferedImage dest;
	int threshold = 127;
	int[][] horizontalEdgesFilter = {{-1, -1, -1}, {0, 0, 0}, {1, 1, 1}};
	int[][] verticalEdgesFilter = {{-1, 0, 1}, {-1, 0, 1}, {-1, 0, 1}};
	int[][] filter;
	int totalFilterWeight;

	private void calculateTotalFilterWeight() {
		totalFilterWeight = 0;
		for (int row = 0; row < filter.length; row++) {
			for (int col = 0; col < filter[row].length; col++) {
				totalFilterWeight += filter[row][col];
			}
		}
		if (totalFilterWeight == 0)
			totalFilterWeight = 1;
	}

	public ImageFilters withImage(Image image) {

		int width = ((int) image.getWidth());
		int height = ((int) image.getHeight());

		source = SwingFXUtils.fromFXImage(image, null);

		return this;
	}

	public ImageFilters withThreshold(int threshold) {
		this.threshold = threshold;
		return this;
	}

	public ImageFilters binarize() {
		dest = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		binarizeImage();
		source = dest;
		dest = null;
		return this;
	}

	private void binarizeImage() {
		for (int y = 0; y < source.getHeight(); y++) {
			binarizeRow(y);
		}
	}

	private void binarizeRow(int row) {
		int r, g, b, avarage, color;
		for (int x = 0; x < source.getWidth(); x++) {
			color = source.getRGB(x, row);
			r = (color & 0xFF0000) >> 16;
			g = (color & 0xFF00) >> 8;
			b = color & 0xFF;
			avarage = (r + g + b) / 3;
			if (avarage > threshold) {
				dest.setRGB(x, row, 0xFFFFFF);
			} else {
				dest.setRGB(x, row, 0x0);
			}
		}
	}

	public ImageFilters horizontalEdges() {
		filter = horizontalEdgesFilter;
		applyFilter();
		return this;
	}

	public ImageFilters verticalEdges() {
		filter = verticalEdgesFilter;
		applyFilter();
		return this;
	}

	private void applyFilter() {
		dest = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		calculateTotalFilterWeight();
		filterImage();
		source = dest;
		dest = null;
	}

	private void filterImage() {
		for (int y = 1; y < source.getHeight() - 1; y++) {
			filterImageRow(y);
		}
	}

	private void filterImageRow(int row) {
		for (int x = 1; x < source.getWidth() - 1; x++) {
			filterImagePixel(x, row);
		}
	}

	private void filterImagePixel(int x, int y) {
		int destColor = 0;
		for (int row = 0; row < filter.length; row++) {
			for (int col = 0; col < filter[row].length; col++) {
				destColor += getPixelAvarageFromSource(x - 1 + col, y - 1 + row) * filter[row][col];
			}
		}
		dest.setRGB(x, y, destColor);
	}

	private int getPixelAvarageFromSource(int x, int y) {
		int r, g, b, color;
		color = source.getRGB(x, y);
		r = (color & 0xFF0000) >> 16;
		g = (color & 0xFF00) >> 8;
		b = color & 0xFF;
		return (r + g + b) / 3;
	}

	public Image getResult() {
		return SwingFXUtils.toFXImage(source, null);
	}
}
