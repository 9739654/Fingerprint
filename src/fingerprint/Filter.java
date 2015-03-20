package fingerprint;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;

class HorizontalPrewitt extends Filter {
	public HorizontalPrewitt() {
		super("poziomy Prewitt",
				new int[][] {{-1, -1, -1},{0, 0, 0},{1, 1, 1}});
	}
}

class HorizontalSobel extends Filter {
	public HorizontalSobel() {
		super("poziomy Sobel",
				new int[][] {{-1, -2, -1},{0, 0, 0},{1, 2, 1}});
	}
}

class VerticalPrewitt extends Filter {
	public VerticalPrewitt() {
		super("pionowy Prewitt",
				new int[][] {{-1, 0, 1},{-1, 0, 1},{-1, 0, 1}});
	}
}

class VerticalSobel extends Filter {
	public VerticalSobel() {
		super("pionowy Sobel",
				new int[][] {{-1, 0, 1},{-2, 0, 2},{-1, 0, 1}});
	}
}

class Gauss5x5 extends Filter {
	public Gauss5x5() {
		super("Gauss 5x5",
				new int[][] {{2,4,5,4,2},{4,9,12,9,4},{5,12,15,12,5},{4,9,12,9,4},{2,4,5,4,2}});
	}
}

class Gauss3x3 extends Filter {
	public Gauss3x3() {
		super("Gauss 3x3",
				new int[][] {{1,2,1},{2,4,2},{1,2,1}});
	}
}

class Smooth extends Filter {
	public Smooth() {
		super("Wygładzenie R1",
				new int[][] {{1,1,1},{1,1,1},{1,1,1}});
	}
}

class Binarize extends Filter {
	public Binarize() {
		super("Binaryzacja", new int[][] {{1}});
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

	String name;
	BiConsumer<Integer, Integer> pixelProcedure;

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

	protected Filter(String name, int[][] filter) {
		this.name = name;
		this.filter = filter;
		this.imageType = BufferedImage.TYPE_BYTE_GRAY;
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
		if (totalFilterWeight == 0)
			totalFilterWeight = 1;
	}

    /**
     * Set the image to be filtered.
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
     * @return
     */
	public Filter filter() {
		for (int row = filterHalfWidth; row < source.getHeight() - filterHalfWidth; row++) {
			filterImageRow(row);
		}
		source = dest;
		dest = null;
		return this;
	}

    /**
     * Applies filter to a single row.
     * @param row index of the row to be filtered
     */
	private void filterImageRow(int row) {
		for (int col = filterHalfHeight; col < source.getWidth() - filterHalfHeight; col++) {
			pixelProcedure.accept(col, row);
		}
	}

    /**
     * Output the filtered image to an ImageView.
     * @param target
     */
    public void setImage(ImageView target) {
        if (targetImage == null) {
            targetImage = SwingFXUtils.toFXImage(source, null);
        }
        target.setImage(targetImage);
    }

	@Override
	public String toString() {
		return name;
	}
}
