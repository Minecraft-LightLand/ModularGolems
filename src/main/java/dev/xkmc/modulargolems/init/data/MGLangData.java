package dev.xkmc.modulargolems.init.data;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import dev.xkmc.modulargolems.compat.materials.common.CompatManager;
import dev.xkmc.modulargolems.compat.misc.PatchouliLang;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemPartType;
import dev.xkmc.modulargolems.content.entity.humanoid.HumaniodGolemPartType;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nullable;
import java.util.Locale;

public enum MGLangData {
	HEALTH("tooltip.health", "Health: %s/%s", 2, null),
	SLOT("tooltip.slot", "Remaining Upgrade Slot: %s", 1, null),
	SHIFT("tooltip.shift", "Press SHIFT to show modifier details", 0, ChatFormatting.GRAY),
	POTION_ATTACK("tooltip.potion_attack", "Inflict %s to enemies attacking or attacked.", 1, ChatFormatting.GREEN),
	POTION_DEFENSE("tooltip.potion_defense", "Golem gain %s.", 1, ChatFormatting.GREEN),
	GOLEM_EQUIPMENT("tooltip.golem_equipment", "Only effective on %s", 1, ChatFormatting.LIGHT_PURPLE),

	MODE_FOLLOW("tooltip.follow", "Follow", 0, ChatFormatting.AQUA),
	MODE_GUARD("tooltip.guard", "Wander", 0, ChatFormatting.AQUA),
	MODE_STAND("tooltip.stand", "Stand", 0, ChatFormatting.AQUA),
	MODE_FOLLOWING("tooltip.following", "Golem will follow you", 0, ChatFormatting.AQUA),
	MODE_GUARDING("tooltip.guarding", "Golem will wander around (%s, %s, %s)", 3, ChatFormatting.AQUA),
	MODE_STANDING("tooltip.standing", "Golem will stay at (%s, %s, %s)", 3, ChatFormatting.AQUA),

	WAND_RETRIEVE("wand.retrieve", "Right click to retrieve all your surrounding golems. Shift right click faraway golems to retrieve golem back into inventory.", 0, ChatFormatting.GRAY),
	WAND_COMMAND("wand.command", "Right click to switch modes for golems. Shift right click Humanoid golem to configure inventory. Right click or attack entity with this wand to call all your surrounding golems to switch target to it.", 0, ChatFormatting.GRAY),
	WAND_SUMMON("wand.summon", "Right click to summon one golem to a faraway position pointed by the wand. Shift right click to summon all golems from your inventory.", 0, ChatFormatting.GRAY),
	WAND_RIDER("wand.rider", "Right click at your dog golem to ride on it.", 0, ChatFormatting.GRAY),
	CONFIG_CARD("wand.config", "Right click golem to set config. Right click again to remove. When in offhand, wands would only target golems having this config.", 0, ChatFormatting.GRAY),
	CONFIG_INIT("wand.config_init", "Right click to initialize config", 0, ChatFormatting.GRAY),
	CONFIG_OTHER("wand.config_other", "Not your card. You cannot edit it", 0, ChatFormatting.RED),

	DESTROY_ITEM("msg.destroy_item", "Golem %s destroyed %s items because it finds no place to store.", 2, ChatFormatting.RED),
	DESTROY_EXP("msg.destroy_exp", "Golem %s destroyed %s experience because it finds no place to store.", 2, ChatFormatting.RED),
	CALL_ATTACK("msg.call_attack", "%s Golems will attack %s", 2, null),

	LOADING("config.loading", "Loading...", 0, ChatFormatting.GRAY),
	NO_CONFIG("config.no", "No config card assigned", 0, ChatFormatting.GRAY),
	SUMMON_FAILED("msg.summon_failed", "Failed to summon %s, as target position is too far", 1, ChatFormatting.RED),
	SUMMON_FAR("msg.summon_far", "Summoned %s, at (%s,%s,%s)", 4, ChatFormatting.GOLD),

	CONFIG_SET("config.set", "Insert to set config", 0, null),
	CONFIG_MODE("config.mode", "Default Mode", 0, null),
	CONFIG_POS("config.pos", "Summon to", 0, null),
	CONFIG_TO_POSITION("config.pos_original", "Original Position", 0, ChatFormatting.AQUA),
	CONFIG_TO_TARGET("config.pos_target", "Pointed Position", 0, ChatFormatting.AQUA),
	CONFIG_MODE_TOOLTIP("config.mode_tooltip", "The default mode golem will take when summoned. Does not affect golem in world already. Retrieved golems will take this mode instead of old mode when summoned.", 0, ChatFormatting.GRAY),
	CONFIG_TO_POSITION_TOOLTIP("config.pos_original_tooltip", "If golem is retrieved, summon this golem to the original position. It would not take effect if the golem is fresh. If the original position is too far away, summon would fail.", 0, ChatFormatting.GRAY),
	CONFIG_TO_TARGET_TOOLTIP("config.pos_target_tooltip", "Summon golem to the position player points to.", 0, ChatFormatting.GRAY),

	UI_WHITELIST("config.whitelist", "Using whitelist. Click to switch to blacklist.", 0, null),
	UI_BLACKLIST("config.blacklist", "Using blacklist. Click to switch to whitelist.", 0, null),
	UI_MATCH_ITEM("config.match_item", "Match item only. Click to match NBT.", 0, null),
	UI_MATCH_TAG("config.match_tag", "Match item and NBT. Click to match item only.", 0, null),

	TAB_TOGGLE("tab.toggle", "General Config", 0, null),
	TAB_PICKUP("tab.pickup", "Pickup Filter Config", 0, null),
	TAB_EQUIPMENT("tab.equipment", "Golem Equipments", 0, null),
	TAB_ATTRIBUTE("tab.attribute", "Golem Attributes", 0, null),

	;

	private final String key, def;
	private final int arg;
	private final ChatFormatting format;


	MGLangData(String key, String def, int arg, @Nullable ChatFormatting format) {
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
		MutableComponent ans = Component.translatable(key, args);
		if (format != null) {
			return ans.withStyle(format);
		}
		return ans;
	}

	public static void genLang(RegistrateLangProvider pvd) {
		for (MGLangData lang : MGLangData.values()) {
			pvd.add(lang.key, lang.def);
		}
		pvd.add("attribute.name.golem_regen", "Golem Regen");
		pvd.add("attribute.name.golem_sweep", "Sweep Range");
		pvd.add("attribute.name.golem_size", "Golem Size");
		pvd.add("attribute.name.golem_jump", "Golem Jump Strength");

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
