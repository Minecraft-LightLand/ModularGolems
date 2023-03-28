package dev.xkmc.modulargolems.init.data;

import dev.xkmc.l2library.repack.registrate.providers.RegistrateLangProvider;
import dev.xkmc.modulargolems.compat.materials.common.CompatManager;
import dev.xkmc.modulargolems.compat.patchouli.PatchouliLang;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemPartType;
import dev.xkmc.modulargolems.content.entity.humanoid.HumaniodGolemPartType;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

import javax.annotation.Nullable;
import java.util.Locale;

public enum LangData {
	HEALTH("tooltip.health", "Health: %s/%s", 2, null),
	SLOT("tooltip.slot", "Remaining Upgrade Slot: %s", 1, null),
	SHIFT("tooltip.shift", "Press SHIFT to show modifier details", 0, ChatFormatting.GRAY),
	MODE_FOLLOWING("tooltip.following", "Golem will follow you", 0, ChatFormatting.AQUA),
	MODE_GUARDING("tooltip.guarding", "Golem will stay around (%s, %s, %s)", 3, ChatFormatting.AQUA);

	private final String key, def;
	private final int arg;
	private final ChatFormatting format;


	LangData(String key, String def, int arg, @Nullable ChatFormatting format) {
		this.key = ModularGolems.MODID + "." + key;
		this.def = def;
		this.arg = arg;
		this.format = format;
	}

	public static String asId(String name) {
		return name.toLowerCase(Locale.ROOT);
	}

	public static MutableComponent getTranslate(String s) {
		return Component.translatable(ModularGolems.MODID + "." + s);
	}

	public MutableComponent get(Object... args) {
		if (args.length != arg)
			throw new IllegalArgumentException("for " + name() + ": expect " + arg + " parameters, got " + args.length);
		MutableComponent ans = MutableComponent.create(new TranslatableContents(key, args));
		if (format != null) {
			return ans.withStyle(format);
		}
		return ans;
	}

	public static void genLang(RegistrateLangProvider pvd) {
		for (LangData lang : LangData.values()) {
			pvd.add(lang.key, lang.def);
		}
		pvd.add("itemGroup." + ModularGolems.MODID + ".golems", "Modular Golems");
		pvd.add("attribute.name.golem_regen", "Golem Regen");
		pvd.add("attribute.name.golem_sweep", "Sweep Range");

		pvd.add("golem_material." + ModularGolems.MODID + ".copper", "Copper");
		pvd.add("golem_material." + ModularGolems.MODID + ".iron", "Iron");
		pvd.add("golem_material." + ModularGolems.MODID + ".gold", "Gold");
		pvd.add("golem_material." + ModularGolems.MODID + ".netherite", "Netherite");
		pvd.add("golem_material." + ModularGolems.MODID + ".sculk", "Sculk");

		CompatManager.dispatchGenLang(pvd);

		for (var type : MetalGolemPartType.values()) {
			String name = type.name().toLowerCase(Locale.ROOT);
			pvd.add("golem_part.metal_golem." + name, RegistrateLangProvider.toEnglishName(name) + ": %s");
		}
		for (var type : HumaniodGolemPartType.values()) {
			String name = type.name().toLowerCase(Locale.ROOT);
			pvd.add("golem_part.humanoid_golem." + name, RegistrateLangProvider.toEnglishName(name) + ": %s");
		}
		for (var type : DogGolemPartType.values()) {
			String name = type.name().toLowerCase(Locale.ROOT);
			pvd.add("golem_part.dog_golem." + name, RegistrateLangProvider.toEnglishName(name) + ": %s");
		}

		PatchouliLang.genLang(pvd);

	}

}
