package org.tohu.util;

/**
 * Very general utility code
 * @author dp1086
 *
 */
public class Utils {
	public static final String DELIMITER = "||";
	public static final String DELIMITER_REGEX = "\\|\\|";
	
	/**
	 * Split up the answer, taking care that the delimiter may have special regex meaning
	 * @param answer
	 * @return
	 */
	public static String[] splitMultipleAnswer(String answer) {
		if (answer == null) {
			return new String[0];
		}
		return answer.split(DELIMITER_REGEX);
	}
	
	/**
	 * Join up the answers using a delimiter
	 * @param answer
	 * @return
	 */
	public static String joinMultipleAnswer(String[] answer) {
		StringBuilder b = new StringBuilder();
		String delimiter = "";
		for (int i = 0; i < answer.length; i++) {
			b.append(delimiter).append(answer[i]);
			delimiter = DELIMITER;
		}
		return b.toString();
	}
}
