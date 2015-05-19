package fingerprint.linefinder;

import java.util.Map;
import java.util.TreeMap;

public class LineResult {

	/**
	 * Maps line index to line count
	 */
	public final Map<Integer, Integer> horizontal;

	/**
	 * Maps line index to line count
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
			result += "\t" + entry.getKey() + " " + entry.getValue() + "\n";
		}
		result += "vertical:\n";
		for (Map.Entry<Integer, Integer> entry : vertical.entrySet()) {
			result += "\t" + entry.getKey() + " " + entry.getValue() + " \n";
		}
		return result;
	}
}
