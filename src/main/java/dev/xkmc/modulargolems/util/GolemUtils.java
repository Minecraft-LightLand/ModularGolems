package dev.xkmc.modulargolems.util;

public class GolemUtils {

	public static float adjustedDamage(float base, float bonus) {
		if (bonus > base) {
			return (float) Math.sqrt(bonus / base) * base * 2;
		} else return base + bonus;
	}

}
