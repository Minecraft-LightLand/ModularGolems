package dev.xkmc.modulargolems.compat.materials.cataclysm.armor;

import javax.annotation.Nullable;
import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;

/**
 * Shared queries for the ender guardian, ancient metal and storm armor sets.
 * <p>
 * Every piece follows the same rule: the trait applies when the golem wears that exact piece. Keeping
 * the slot lookups here means the individual skills read as a sentence ("is this golem below half
 * health, or wearing the piece that pins it there") instead of repeating equipment checks everywhere.
 */
public class CataArmorHelper {

	/**
	 * The health threshold that the cataclysm skills use to decide they are in their final form.
	 */
	public static final float HALF_HEALTH = 0.5F;

	private static boolean has(AbstractGolemEntity<?, ?> golem, EquipmentSlot slot, Object item) {
		return golem.getItemBySlot(slot).is((net.minecraft.world.item.Item) item);
	}

	/**
	 * Whether the golem's current health is at or below {@code ratio} of its max.
	 */
	public static boolean isBelow(AbstractGolemEntity<?, ?> golem, float ratio) {
		return golem.getGuardedDataImpl() <= golem.getMaxHealth() * ratio;
	}

	/**
	 * Whether the golem should behave as if it were below the given health threshold, either because it
	 * is, or because the chestpiece pins its skills at the final form (the "ignores health requirement"
	 * armor trait). Every cataclysm set uses this same rule; only {@code ratio} differs per set.
	 */
	public static boolean isFinalForm(AbstractGolemEntity<?, ?> golem, EquipmentSlot slot, Object item, float ratio) {
		if (has(golem, slot, item)) return true;
		return isBelow(golem, ratio);
	}

	/** Convenience overload for the sets that unlock at half health. */
	public static boolean isFinalForm(AbstractGolemEntity<?, ?> golem, EquipmentSlot slot, Object item) {
		return isFinalForm(golem, slot, item, HALF_HEALTH);
	}

	public static boolean stormHelmet(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.HEAD, CataCompatRegistry.STORM_HELMET.get());
	}

	public static boolean ignisChest(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.CHEST, CataCompatRegistry.IGNIS_CHESTPLATE.get());
	}

	public static boolean ignisLegs(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.LEGS, CataCompatRegistry.IGNIS_SHINGUARD.get());
	}

	public static boolean stormChest(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.CHEST, CataCompatRegistry.STORM_CHESTPLATE.get());
	}

	public static boolean stormLegs(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.LEGS, CataCompatRegistry.STORM_SHINGUARD.get());
	}

	public static boolean ancientHelmet(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.HEAD, CataCompatRegistry.ANCIENT_METAL_HELMET.get());
	}

	public static boolean ancientChest(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.CHEST, CataCompatRegistry.ANCIENT_METAL_CHESTPLATE.get());
	}

	public static boolean ancientLegs(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.LEGS, CataCompatRegistry.ANCIENT_METAL_SHINGUARD.get());
	}

	public static boolean guardianHelmet(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.HEAD, CataCompatRegistry.ENDER_GUARDIAN_HELMET.get());
	}

	public static boolean guardianChest(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.CHEST, CataCompatRegistry.ENDER_GUARDIAN_CHESTPLATE.get());
	}

	public static boolean guardianLegs(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.LEGS, CataCompatRegistry.ENDER_GUARDIAN_SHINGUARD.get());
	}

	public static boolean ignisHelmet(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.HEAD, CataCompatRegistry.IGNIS_HELMET.get());
	}

	public static boolean harbingerHelmet(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.HEAD, CataCompatRegistry.HARBINGER_HELMET.get());
	}

	public static boolean harbingerChest(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.CHEST, CataCompatRegistry.HARBINGER_CHESTPLATE.get());
	}

	public static boolean maledictusHelmet(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.HEAD, CataCompatRegistry.MALEDICTUS_HELMET.get());
	}

	public static boolean maledictusChest(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.CHEST, CataCompatRegistry.MALEDICTUS_CHESTPLATE.get());
	}

	public static boolean maledictusLegs(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.LEGS, CataCompatRegistry.MALEDICTUS_SHINGUARD.get());
	}

	public static boolean monstrosityHelmet(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.HEAD, CataCompatRegistry.MONSTROSITY_HELMET.get());
	}

	public static boolean monstrosityChest(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.CHEST, CataCompatRegistry.MONSTROSITY_CHESTPLATE.get());
	}

	public static boolean monstrosityLegs(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.LEGS, CataCompatRegistry.MONSTROSITY_SHINGUARD.get());
	}

	/**
	 * Storm set: unlocks at 2/3 health, or the chestpiece.
	 */
	public static boolean stormFinalForm(AbstractGolemEntity<?, ?> golem) {
		return isFinalForm(golem, EquipmentSlot.CHEST, CataCompatRegistry.STORM_CHESTPLATE.get(), TWO_THIRDS);
	}

	/**
	 * Ignis set: soul form at 2/3 health, or the chestpiece.
	 */
	public static boolean ignisFinalForm(AbstractGolemEntity<?, ?> golem) {
		return isFinalForm(golem, EquipmentSlot.CHEST, CataCompatRegistry.IGNIS_CHESTPLATE.get(), TWO_THIRDS);
	}

	public static boolean ancientFinalForm(AbstractGolemEntity<?, ?> golem) {
		return isFinalForm(golem, EquipmentSlot.CHEST, CataCompatRegistry.ANCIENT_METAL_CHESTPLATE.get());
	}

	public static boolean guardianFinalForm(AbstractGolemEntity<?, ?> golem) {
		return isFinalForm(golem, EquipmentSlot.CHEST, CataCompatRegistry.ENDER_GUARDIAN_CHESTPLATE.get());
	}

	public static boolean maledictusFinalForm(AbstractGolemEntity<?, ?> golem) {
		return isFinalForm(golem, EquipmentSlot.CHEST, CataCompatRegistry.MALEDICTUS_CHESTPLATE.get());
	}

	/**
	 * The ancient metal chestpiece halves the cooldown of its four skills below half health. Because the
	 * piece itself pins the golem at the final form, wearing it makes the halving permanent.
	 */
	public static boolean ancientFastSkill(AbstractGolemEntity<?, ?> golem) {
		return ancientFinalForm(golem);
	}

	/**
	 * The ender guardian chestpiece halves Teleport Slam's cooldown below half health.
	 */
	public static boolean guardianFastSlam(AbstractGolemEntity<?, ?> golem) {
		return guardianFinalForm(golem);
	}

	/**
	 * The ender guardian chestpiece halves Void Rune's cooldown below half health.
	 */
	public static boolean guardianFastSkill(AbstractGolemEntity<?, ?> golem) {
		return guardianFinalForm(golem);
	}

	/**
	 * The ender guardian chestpiece makes Void Rune summon three rows per target below half health.
	 */
	public static boolean guardianTripleRune(AbstractGolemEntity<?, ?> golem) {
		return guardianFinalForm(golem);
	}

	/**
	 * The health threshold as a whole percentage, for the skill tooltips that quote it.
	 */
	public static int percentOf(float ratio) {
		return Math.round(ratio * 100);
	}

	/** The half-health threshold as a whole percentage. */
	public static int halfHealthPercent() {
		return percentOf(HALF_HEALTH);
	}

	/**
	 * A styled "NN%" component for a health threshold. Uses the same AQUA highlight every other
	 * numeric placeholder in tooltips uses, so it does not blend into the green description line.
	 */
	public static Component healthPct(float ratio) {
		return Component.literal(Math.round(ratio * 100) + "%").withStyle(ChatFormatting.AQUA);
	}

	/** A styled straight number (AQUA), for counts / cached values in tooltips. */
	public static Component number(Object value) {
		return Component.literal(String.valueOf(value)).withStyle(ChatFormatting.AQUA);
	}

	public static final float TWO_THIRDS = 2f / 3f;
	public static final float ONE_THIRD = 1f / 3f;

	/**
	 * Localized material name, used by the {@code skill_ignore_health} line.
	 */
	public static Component materialName(String id) {
		return Component.translatable("golem_material.cataclysm." + id);
	}

	/**
	 * Localized skill name, used by the shared armor tooltip lines.
	 */
	public static Component skillName(NamedEntry<?> entry) {
		return Component.translatable(entry.getDescriptionId());
	}

	/**
	 * Shared "+N% Skill Damage" line. The percentage is AQUA-highlighted like every other number.
	 */
	public static net.minecraft.network.chat.MutableComponent damageLine(double percent, Component skill) {
		return MGLangData.SKILL_DAMAGE_BOOST.get(Math.round(percent * 100) + "%", skill);
	}

	/**
	 * Below half health, or the witherite chestpiece pins the golem at its final form.
	 */
	public static boolean harbingerFinalForm(AbstractGolemEntity<?, ?> golem) {
		return isFinalForm(golem, EquipmentSlot.CHEST, CataCompatRegistry.HARBINGER_CHESTPLATE.get());
	}

	/** 把多个技能名用 {@code /} 拼成一个组件，供 tooltip 的单个 {@code %s} 使用。 */
	public static Component skillList(NamedEntry<?>... mods) {
		MutableComponent res = Component.empty();
		for (int i = 0; i < mods.length; i++) {
			if (i > 0) res.append("/");
			res.append(skillName(mods[i]));
		}
		return res;
	}


	/**
	 * 朝向目标的 yaw（度）。灾变那边的方向算法用的是 MC 的 yaw 约定：forward = (-sin yaw, cos yaw)。
	 * 目标为空或几乎重合时退回傀儡自身朝向。
	 */
	public static float yawToTarget(AbstractGolemEntity<?, ?> golem, @Nullable net.minecraft.world.entity.LivingEntity target) {
		if (target == null) return golem.yBodyRot;
		double dx = target.getX() - golem.getX();
		double dz = target.getZ() - golem.getZ();
		if (dx * dx + dz * dz < 1.0E-4) return golem.yBodyRot;
		return (float) (net.minecraft.util.Mth.atan2(-dx, dz) * (180.0 / Math.PI));
	}

	/** 把傀儡朝向扳到指定 yaw，让模型与招式的方向一致。 */
	public static void faceYaw(AbstractGolemEntity<?, ?> golem, float yaw) {
		golem.setYRot(yaw);
		golem.yBodyRot = yaw;
		golem.yHeadRot = yaw;
	}}
