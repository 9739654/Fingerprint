package fingerprint.linefinder;

import java.util.Map;
import java.util.TreeMap;

public class LineResult {

	/**
	 * Maps line index to line count. The key is x index of line. The value is number of horizontalLines lines found at this x index
	 */
	public final Map<Integer, Integer> horizontalLines;

	/**
	 * Maps line index to line count. The key is y index of line. The value is number of lines verticalLines found at this y index
	 */
	public final Map<Integer, Integer> verticalLines;

	{
		horizontalLines = new TreeMap<>();
		verticalLines = new TreeMap<>();
	}

	@Override
	public String toString() {
		String result = "Horizontal:\n";
		for (Map.Entry<Integer, Integer> entry : horizontalLines.entrySet()) {
			result += "\tx = " + entry.getKey() + ":   " + entry.getValue() + "\n";
		}
		result += "Vertical:\n";
		for (Map.Entry<Integer, Integer> entry : verticalLines.entrySet()) {
			result += "\ty = " + entry.getKey() + ":   " + entry.getValue() + " \n";
		}
		return result;
	}
}
