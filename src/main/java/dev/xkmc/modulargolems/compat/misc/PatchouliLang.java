package dev.xkmc.modulargolems.compat.misc;

import dev.xkmc.l2library.repack.registrate.providers.RegistrateLangProvider;
import dev.xkmc.modulargolems.init.ModularGolems;

public enum PatchouliLang {
	TITLE("title", "Modular Golem Guide"),
	LANDING("landing", "Welcome to Tinker-like golem assembly and upgrade mod");

	private final String key, def;

	PatchouliLang(String key, String def) {
		this.key = "patchouli." + ModularGolems.MODID + "." + key;
		this.def = def;
	}

	public static void genLang(RegistrateLangProvider pvd) {
		for (PatchouliLang lang : PatchouliLang.values()) {
			pvd.add(lang.key, lang.def);
		}
	}

}
