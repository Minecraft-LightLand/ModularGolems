package dev.xkmc.modulargolems.util;

/** 等级显示用罗马数字，避免高等级依赖 {@code potion.potency.*} 这类有限的语言键。 */
public final class RomanNumerals {

	private static final String[] ROMAN = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
	private static final int[] VALUE = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

	private RomanNumerals() {
	}

	public static String of(int n) {
		if (n <= 0) return "0";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < VALUE.length; i++) {
			while (n >= VALUE[i]) {
				n -= VALUE[i];
				sb.append(ROMAN[i]);
			}
		}
		return sb.toString();
	}

}
