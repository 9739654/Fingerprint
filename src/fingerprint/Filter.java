package fingerprint;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;

class Negate extends Filter {
	public Negate() {
		super("Negacja", new int[][]{{1}});

		pixelProcedure = (x, y) -> {
			int color = source.getRGB(x, y);
			int r,g,b;
			r = (color >> 16) & 0xFF;
			g = (color >>  8) & 0xFF;
			b = (color >>  0) & 0xFF;
			r = 255 - r;
			g = 255 - g;
			b = 255 - b;
			color = (r << 16) | (g << 8) | b;
			dest.setRGB(x, y, color);
		};
	}
}

class BinaryErosion extends Filter {
	public BinaryErosion() {
		super(
				"Erozja",
				new int[][] {
						{1,1,1},
						{1,1,1},
						{1,1,1}
				}
		);

		imageType = BufferedImage.TYPE_BYTE_BINARY;
		pixelProcedure = erosionPixelFilter;
	}


}

class BinaryDilation extends Filter {
	public BinaryDilation() {
		super(
				"Dylatacja",
				new int[][] {
						{1,1,1},
						{1,1,1},
						{1,1,1}
				}
		);
		imageType = BufferedImage.TYPE_BYTE_BINARY;
		pixelProcedure = dilationPixelFilter;
	}
}

class BinaryScelet extends Filter {
	public BinaryScelet() {
		super(
				"Szkieletyzacja",
				new int[][]{
						{  0,   0,   0},
						{255, 255, 255},
						{255, 255, 255}
				}
		);

		this.imageType = BufferedImage.TYPE_BYTE_BINARY;
	}

	@Override
	public Filter filter() {
		pixelProcedure = erosionPixelFilter;
		filterImage();
		negateFilter();
		pixelProcedure = dilationPixelFilter;
		filterImage();
		dest = null;
		return this;
	}


}

class HorizontalPrewitt extends Filter {
	public HorizontalPrewitt() {
		super("poziomy Prewitt",
				new int[][]{{-1, -1, -1}, {0, 0, 0}, {1, 1, 1}});
	}
}

class HorizontalSobel extends Filter {
	public HorizontalSobel() {
		super("poziomy Sobel",
				new int[][]{{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}});
	}
}

class VerticalPrewitt extends Filter {
	public VerticalPrewitt() {
		super("pionowy Prewitt",
				new int[][]{
						{-1, 0, 1},
						{-1, 0, 1},
						{-1, 0, 1}}
		);
	}
}

class VerticalSobel extends Filter {
	public VerticalSobel() {
		super("pionowy Sobel",
				new int[][]{
						{-1, 0, 1},
						{-2, 0, 2},
						{-1, 0, 1}}
		);
	}
}

class Gauss5x5 extends Filter {
	public Gauss5x5() {
		super("Gauss 5x5",
				new int[][]{
						{2, 4, 5, 4, 2},
						{4, 9, 12, 9, 4},
						{5, 12, 15, 12, 5},
						{4, 9, 12, 9, 4},
						{2, 4, 5, 4, 2}});
	}
}

class BinarySmooth7x7 extends Filter {
	public BinarySmooth7x7() {
		super(
				"Wyładzanie binarne 7x7",
				new int[][]{
						{0, 1, 2, 4, 2, 1, 0},
						{1, 2, 4, 8, 4, 2, 1},
						{2, 4, 8, 16, 8, 4, 2},
						{4, 8, 16, 32, 16, 8, 4},
						{2, 4, 8, 16, 8, 4, 2},
						{1, 2, 4, 8, 4, 2, 1},
						{0, 1, 2, 4, 2, 1, 0}
				});
	}
}

class Gauss3x3 extends Filter {
	public Gauss3x3() {
		super("Gauss 3x3",
				new int[][]{{1, 2, 1}, {2, 4, 2}, {1, 2, 1}});
	}
}

class Smooth extends Filter {
	public Smooth() {
		super("Wygładzenie R1",
				new int[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}});
	}
}

class Binarize extends Filter {
	public Binarize() {
		super("Binaryzacja", new int[][]{{1}});
		imageType = BufferedImage.TYPE_BYTE_BINARY;
	}
}

class AllEdges extends Filter {
	public AllEdges() {
		super(
				"Wszystkie krawędzie",
				new int[][]{
						{-1, -1, -1},
						{-1, 8, -1},
						{-1, -1, -1}
				}
		);
		imageType = BufferedImage.TYPE_BYTE_BINARY;
	}
}

class AllEdges5x5 extends Filter {
	public AllEdges5x5() {
		super(
				"Wszystkie krawędzie 5x5",
				new int[][]{
						{-2,-2,-2,-2,-2},
						{-2,-1,-1,-1,-2},
						{-2,-1,32,-1,-2},
						{-2,-1,-1,-1,-2},
						{-2,-2,-2,-2,-2}});
	}
}

class AllEdgesInverted extends Filter {
	public AllEdgesInverted() {
		super(
				"Wszystkie krawędzie odwrócone",
				new int[][]{
						{1, 1, 1},
						{1, -8, 1},
						{1, 1, 1}
				}
		);

		imageType = BufferedImage.TYPE_BYTE_BINARY;
	}
}

class CustomFilter extends Filter {
	public CustomFilter() {
		super("filtr testowy", new int[][]{{1}});
	}

	@Override
	public Filter filter() {

		Gauss3x3 gs = (Gauss3x3) Filters.getByClass(Gauss3x3.class).get();
		VerticalPrewitt vp = ((VerticalPrewitt) Filters.getByClass(VerticalPrewitt.class).get());

		gs.source = source;
		gs.filter();

		vp.source = gs.source;
		vp.filter();

		source = vp.source;
		dest = null;
		return this;
	}
}

/**
 * An abstract Filter class
 *
 * @author mati
 */
public abstract class Filter {

	public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int BLUE = 2;

	protected BufferedImage source;
	protected BufferedImage dest;

	int[][] filter;
	int totalFilterWeight;
	int filterHalfWidth;
	int filterHalfHeight;
	int imageType;
	Image targetImage;

	private String name;
	protected BiConsumer<Integer, Integer> pixelProcedure;

	/**
	 * This is the actual filter method which is applied to every pixel.
	 */
	protected final BiConsumer<Integer, Integer> filterImagePixel = (x, y) -> {
		int dr = 0, dg = 0, db = 0, r, g, b, color;
		for (int row = 0; row < filter.length; row++) {
			for (int col = 0; col < filter[row].length; col++) {
				color = source.getRGB(x - filterHalfWidth + col, y - filterHalfHeight + row);
				r = color >> 16 & 0xFF;
				g = color >> 8 & 0xFF;
				b = color & 0xFF;
				r *= filter[row][col];
				g *= filter[row][col];
				b *= filter[row][col];
				dr += r;
				dg += g;
				db += b;
			}
		}
		dr /= totalFilterWeight;
		dg /= totalFilterWeight;
		db /= totalFilterWeight;
		color = (dr << 16) | (dg << 8) | db;
		dest.setRGB(x, y, color);
	};

	/**
	 * Sets current pixel's color to black if any of the masked pixels are black
	 */
	protected final BiConsumer<Integer, Integer> erosionPixelFilter = (x, y) -> {
		int r, color = 0;
		boolean setToBlack = false;

		for (int row = 0; row < filter.length; row++) {
			for (int col = 0; col < filter[row].length; col++) {
				color = source.getRGB(x - filterHalfWidth + col, y - filterHalfHeight + row);
				//color >>= 16;
				color &= 0xFF;
				if (filter[row][col] > 0) {
					setToBlack |= (color == 0);
				}
			}
		}
		if (setToBlack) {
			color &= 0xFF000000;
		} else {
			color |= 0x00FFFFFF;
		}

		dest.setRGB(x, y, color);
	};

	protected final BiConsumer<Integer, Integer> dilationPixelFilter = (x, y) -> {
		int r, color = 0;
		boolean setToWhite = false;

		for (int row = 0; row < filter.length; row++) {
			for (int col = 0; col < filter[row].length; col++) {
				color = source.getRGB(x - filterHalfWidth + col, y - filterHalfHeight + row);
				//color >>= 16;
				color &= 0xFF;
				if (filter[row][col] > 0) {
					setToWhite |= (color > 0);
				}
			}
		}
		if (setToWhite) {
			color |= 0x00FFFFFF;
		} else {
			color &= 0xFF000000;
		}

		dest.setRGB(x, y, color);
	};

	protected void negateFilter() {
		for (int i=0; i<filter.length; i++) {
			for (int j=0; j<filter[i].length; j++) {
				filter[i][j] = filter[i][j] == 0 ? 255 : 0;
			}
		}
	}

	protected void rotateFilter() {
		int n = filter.length;
		int tmp;
		for (int i = 0; i < n / 2; i++) {
			for (int j = i; j < n - i - 1; j++) {
				tmp = filter[i][j];
				filter[i][j] = filter[j][n - i - 1];
				filter[j][n - i - 1] = filter[n - i - 1][n - j - 1];
				filter[n - i - 1][n - j - 1] = filter[n - j - 1][i];
				filter[n - j - 1][i] = tmp;
			}
		}
	}
	protected Filter(String name, int[][] filter) {
		this.name = name;
		this.filter = filter;
		//this.imageType = BufferedImage.TYPE_BYTE_GRAY;
		this.imageType = BufferedImage.TYPE_INT_RGB;
		this.pixelProcedure = filterImagePixel;

		calculateTotalFilterWeight();
	}

	/**
	 * Calculate the sum of weights of all filter fields.
	 */
	private void calculateTotalFilterWeight() {
		totalFilterWeight = 0;
		filterHalfHeight = (filter.length - 1) / 2;
		filterHalfWidth = (filter[0].length - 1) / 2;
		for (int row = 0; row < filter.length; row++) {
			for (int col = 0; col < filter[row].length; col++) {
				totalFilterWeight += filter[row][col];
			}
		}
		if (totalFilterWeight == 0) {
			totalFilterWeight = 1;
		}
	}

	/**
	 * Set the image to be filtered.
	 *
	 * @param image source image
	 * @return
	 */
	public Filter withImage(Image image) {
		source = SwingFXUtils.fromFXImage(image, null);
		dest = new BufferedImage(source.getWidth(), source.getHeight(), imageType);
		return this;
	}

	/**
	 * Do the filtering.
	 *
	 * @return
	 */
	public Filter filter() {
		filterImage();
		dest = null;
		return this;
	}

	protected final void filterImage() {
		for (int row = filterHalfWidth; row < source.getHeight() - filterHalfWidth; row++) {
			filterImageRow(row);
		}
		source = dest;
	}

	/**
	 * Applies filter to a single row.
	 *
	 * @param row index of the row to be filtered
	 */
	protected final void filterImageRow(int row) {
		for (int col = filterHalfHeight; col < source.getWidth() - filterHalfHeight; col++) {
			pixelProcedure.accept(col, row);
		}
	}

	/**
	 * Output the filtered image to an ImageView.
	 *
	 * @param targetView
	 */
	public void setImage(ImageView targetView) {
		targetView.setImage(SwingFXUtils.toFXImage(source, null));
		source = null;
		dest = null;
	}

	@Override
	public String toString() {
		return name;
	}
}
