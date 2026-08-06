package dev.xkmc.modulargolems.editor.util;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nullable;

public enum GolemEditorLang {
	MATERIALS("editor.materials", "Materials", 0, null),
	PARTS("editor.parts", "Parts", 0, null),
	MATERIALS_FILE("editor.materials_file", "Materials file", 0, null),
	PARTS_FILE("editor.parts_file", "Parts file", 0, null),
	INGREDIENT("editor.ingredient", "Ingredient", 0, null),
	REPAIR("editor.repair", "Repair ingredient", 0, null),
	LIMITATION("editor.limitation", "Part limitation: %s", 1, null),
	STATS("editor.stats", "Stats (%s)", 1, null),
	MODIFIERS("editor.modifiers", "Modifiers (%s)", 1, null),
	STAT_FILTER_HEALTH("editor.stat_filter.HEALTH", "Health", 0, null),
	STAT_FILTER_ATTACK("editor.stat_filter.ATTACK", "Attack", 0, null),
	STAT_FILTER_MOVEMENT("editor.stat_filter.MOVEMENT", "Movement", 0, null),
	STAT_FILTER_MASS("editor.stat_filter.MASS", "Mass", 0, null),
	STAT_FILTER_HEAD("editor.stat_filter.HEAD", "Head", 0, null),
	ADD_PART("editor.add_part", "Add part", 0, null),
	ADD_MAGNIFIER("editor.add_magnifier", "Add entity", 0, null),
	SELECT_TYPE("editor.select_type", "Select golem type", 0, null),
	SELECT_MODIFIER("editor.select_modifier", "Select modifier", 0, null),
	SELECT_PART("editor.select_part", "Select part", 0, null),
	SELECT_ENTITY("editor.select_entity", "Select entity type", 0, null),
	FILTERS("editor.filters", "Filters (%s)", 1, null),
	MAGNIFIERS("editor.magnifiers", "Magnifiers (%s)", 1, null),
	NO_MATERIALS("editor.no_materials", "No materials loaded. Open a world first.", 0, ChatFormatting.GRAY),
	MATERIAL("editor.material", "Material", 0, null);

	private final String key, def;
	private final int arg;
	private final ChatFormatting format;

	GolemEditorLang(String key, String def, int arg, @Nullable ChatFormatting format) {
		this.key = ModularGolems.MODID + "." + key;
		this.def = def;
		this.arg = arg;
		this.format = format;
	}

	public String key() {
		return key;
	}

	public MutableComponent get(Object... args) {
		if (args.length != arg)
			throw new IllegalArgumentException("for " + name() + ": expect " + arg + " parameters, got " + args.length);
		MutableComponent ans = Component.translatable(key, args);
		if (format != null) {
			ans = ans.withStyle(format);
		}
		return ans;
	}

	public static void genLang(RegistrateLangProvider pvd) {
		for (GolemEditorLang lang : GolemEditorLang.values()) {
			pvd.add(lang.key, lang.def);
		}
	}

}
