package fingerprint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author mati
 */
public final class Filters extends ArrayList<Filter> {

	private static Filters filters;

	private Filters() {
		add(new BinaryScelet());
		add(new Negate());
		add(new BinaryErosion());
		add(new BinaryDilation());
		add(new Gauss5x5());
		add(new Gauss3x3());
		add(new Smooth());
		add(new Binarize());
		add(new CustomFilter());
        add(new AllEdges());
        add(new AllEdges5x5());
        add(new AllEdgesInverted());
        add(new BinarySmooth7x7());
		add(new HorizontalPrewitt());
		add(new HorizontalSobel());
		add(new VerticalPrewitt());
		add(new VerticalSobel());
	}

	public static Filters getFilters() {
		if (filters == null) {
			filters = new Filters();
		}
		return filters;
	}

	public static Optional<Filter> getByName(String name) {
		for (Filter f : filters) {
			if (f.toString().contains(name)) {
				return Optional.of(f);
			}
		}
		return Optional.empty();
	}

	public static Optional<Filter> getByClass(Class<?> x) {
		for (Filter f : filters) {
			if (f.getClass().equals(x)) {
				return Optional.of(f);
			}
		}
		return Optional.empty();
	}

}
