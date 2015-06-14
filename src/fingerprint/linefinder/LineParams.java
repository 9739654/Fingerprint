package fingerprint.linefinder;

/**
 * Created by Paluszki on 2015-05-16.
 */
public class LineParams {
	public enum Unit {
		PIXEL, PERCENTAGE
	}

	public Unit unit;

	/**
	 * Indexes at which horizontal lines should be found
	 */
	public int[] horizontalIndexes;

	/**
	 * Indexes at which vertical lines should be found
	 */
	public int[] verticalIndexes;
}
