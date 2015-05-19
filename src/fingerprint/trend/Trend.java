package fingerprint.trend;

import java.util.List;

/**
 * Created by Paluszki on 2015-05-16.
 */
public enum Trend {
	FALLING {
		@Override
		public boolean changed(Trend other) {
			return other == RISING;
		}

		@Override
		public boolean maximum(Trend other) {
			return false;
		}

		@Override
		public boolean minimum(Trend other) {
			return other == RISING;
		}
	},
	RISING {
		@Override
		public boolean changed(Trend other) {
			return other == FALLING;
		}

		@Override
		public boolean maximum(Trend other) {
			return other == FALLING;
		}

		@Override
		public boolean minimum(Trend other) {
			return false;
		}
	},
	CONSTANT {
		@Override
		public boolean changed(Trend other) {
			return false;
		}

		@Override
		public boolean maximum(Trend other) {
			return false;
		}

		@Override
		public boolean minimum(Trend other) {
			return false;
		}
	};

	private static TrendCalculator calculator;

	public abstract boolean changed(Trend other);

	public abstract boolean maximum(Trend other);

	public abstract boolean minimum(Trend other);

	/**
	 * Implementation based on http://classroom.synonym.com/calculate-trendline-2709.html
	 *
	 * @param input
	 *
	 * @return
	 */
	public static Trend of(List<Double> input) {
		if (calculator == null) {
			calculator = new TrendCalculator();
		}
		return calculator.data(input)
				.calculate()
				.getResult();
	}

	public static Trend of(double derivative) {
		if (derivative > 0) {
			return Trend.RISING;
		}
		if (derivative < 0) {
			return Trend.FALLING;
		}
		return Trend.CONSTANT;
	}
}
