package fingerprint.linefinder;

/**
 * Created by Paluszki on 2015-05-16.
 */
public class LineParams {
	public enum Unit {
		PIXEL, PERCENTAGE
	}

	public Unit unit;
	public int[] horizontal;
	public int[] vertical;
}
