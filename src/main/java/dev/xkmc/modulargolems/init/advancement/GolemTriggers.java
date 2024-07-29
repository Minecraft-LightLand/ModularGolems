package dev.xkmc.modulargolems.init.advancement;

import dev.xkmc.l2core.init.reg.simple.SR;
import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.Registries;

public class GolemTriggers {

	public static final SR<CriterionTrigger<?>> REG = SR.of(ModularGolems.REG, Registries.TRIGGER_TYPE);

	public static final Val<PlayerTrigger> HOT_FIX = REG.reg("hot_fix", PlayerTrigger::new);
	public static final Val<PlayerTrigger> BREAK = REG.reg("break", PlayerTrigger::new);
	public static final Val<PlayerTrigger> THUNDER = REG.reg("thunder", PlayerTrigger::new);

	public static final Val<GolemAnvilFixTrigger> ANVIL_FIX = REG.reg("anvil_fix", GolemAnvilFixTrigger::new);
	public static final Val<PartCraftTrigger> PART_CRAFT = REG.reg("part_craft", PartCraftTrigger::new);
	public static final Val<UpgradeApplyTrigger> UPGRADE_APPLY = REG.reg("upgrade_apply", UpgradeApplyTrigger::new);
	public static final Val<GolemEquipTrigger> EQUIP = REG.reg("equip", GolemEquipTrigger::new);
	public static final Val<GolemKillTrigger> KILL = REG.reg("kill", GolemKillTrigger::new);
	public static final Val<GolemMassSummonTrigger> MAS_SUMMON = REG.reg("mass_summon", GolemMassSummonTrigger::new);

	public static void register() {

	}

}
