package dev.xkmc.modulargolems.init.advancement;

import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.resources.ResourceLocation;

public class GolemTriggers {

	public static final GolemHotFixTrigger HOT_FIX = new GolemHotFixTrigger(new ResourceLocation(ModularGolems.MODID, "hot_fix"));
	public static final GolemAnvilFixTrigger ANVIL_FIX = new GolemAnvilFixTrigger(new ResourceLocation(ModularGolems.MODID, "anvil_fix"));
	public static final PartCraftTrigger PART_CRAFT = new PartCraftTrigger(new ResourceLocation(ModularGolems.MODID, "part_craft"));
	public static final UpgradeApplyTrigger UPGRADE_APPLY = new UpgradeApplyTrigger(new ResourceLocation(ModularGolems.MODID, "upgrade_apply"));
	public static final GolemEquipTrigger EQUIP = new GolemEquipTrigger(new ResourceLocation(ModularGolems.MODID, "equip"));
	public static final GolemBreakToolTrigger BREAK = new GolemBreakToolTrigger(new ResourceLocation(ModularGolems.MODID, "break"));
	public static final GolemKillTrigger KILL = new GolemKillTrigger(new ResourceLocation(ModularGolems.MODID, "kill"));
	public static final GolemThunderTrigger THUNDER = new GolemThunderTrigger(new ResourceLocation(ModularGolems.MODID, "thunder"));
	public static final GolemMassSummonTrigger MAS_SUMMON = new GolemMassSummonTrigger(new ResourceLocation(ModularGolems.MODID, "mass_summon"));

	public static void register() {

	}

}
