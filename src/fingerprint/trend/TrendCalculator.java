package fingerprint.trend;

import java.util.List;

public class TrendCalculator {
	private List<Double> data;
	Trend result;

	public TrendCalculator data(List<Double> input) {
		data = input;
		return this;
	}

	public TrendCalculator calculate() {
		double sumX = 0, sumY = 0, a = 0, b, c = 0, d, slope;

		double x = 1;
		for (double y : data) {
			sumX += x;
			sumY += y;
			a += x * y;
			c += x * x;

			x += 1;
		}
		a *= data.length();
		b = sumX * sumY;
		c *= data.length();
		d = sumX * sumX;
		slope = (a-b)/(c-d);
		result = Trend.of(slope);
		return this;
	}

	public Trend getResult() {
		return result;
	}
}
