package fingerprint.linefinder;

import java.util.Map;
import java.util.TreeMap;

public class LineResult {

	/**
	 * Maps line index to line count. The key is x index of line. The value is number of horizontal lines found at this x index
	 */
	public final Map<Integer, Integer> horizontal;

	/**
	 * Maps line index to line count. The key is y index of line. The value is number of lines vertical found at this y index
	 */
	public final Map<Integer, Integer> vertical;

	{
		horizontal = new TreeMap<>();
		vertical = new TreeMap<>();
	}

	@Override
	public String toString() {
		String result = "LineResult\nhorizontal:\n";
		for (Map.Entry<Integer, Integer> entry : horizontal.entrySet()) {
			result += "\tx=" + entry.getKey() + ": " + entry.getValue() + "\n";
		}
		result += "vertical:\n";
		for (Map.Entry<Integer, Integer> entry : vertical.entrySet()) {
			result += "\ty=" + entry.getKey() + ": " + entry.getValue() + " \n";
		}
		return result;
	}
}
