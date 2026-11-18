package dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor;

import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMCompatRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;

/**
 * 传奇怪物联动的阶段判定与护甲查询，唯一入口。
 * <p>
 * Boss 的阶段由 {@code Crackiness} 驱动：低于 65% 进二阶段（{@code MEDIUM}），湮灭构造体低于 30% 再进三阶段
 * （{@code HIGH}）。判定与灾变 {@code CataArmorHelper.isFinalForm} 同构 —— 穿对应胸甲 <b>或</b> 血量到阈值，
 * 也就是胸甲的「无视生命值要求」特性会让这些技能与贴图在满血时就生效。
 */
public class LMArmorHelper {

	/** 二阶段阈值（Boss 的 {@code Crackiness.MEDIUM}）。 */
	public static final float PHASE_TWO = 0.65F;
	/** 三阶段阈值（Boss 的 {@code Crackiness.HIGH}），仅湮灭构造体使用。 */
	public static final float PHASE_THREE = 0.3F;

	/** 护腿的「技能伤害」加成比例（湮灭/浮云/堕魂三套护腿一致，+50%）。 */
	public static final float LEGS_DAMAGE_BONUS = 0.5F;

	/** 浮云头盔的「技能伤害」加成比例（落云/雷电拳/天穹激光，+50%）。 */
	public static final float HEAD_DAMAGE_BONUS = 0.5F;

	// ===== 部位查询（每套三件：头盔 / 胸甲 / 护腿） =====

	public static boolean obliteratorHelmet(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.HEAD, LMCompatRegistry.OBLITERATOR_HELMET.get());
	}

	public static boolean obliteratorLegs(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.LEGS, LMCompatRegistry.OBLITERATOR_SHINGUARD.get());
	}

	public static boolean cloudHelmet(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.HEAD, LMCompatRegistry.CLOUD_HELMET.get());
	}

	public static boolean cloudChest(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.CHEST, LMCompatRegistry.CLOUD_CHESTPLATE.get());
	}

	public static boolean cloudLegs(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.LEGS, LMCompatRegistry.CLOUD_SHINGUARD.get());
	}

	public static boolean paladinHelmet(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.HEAD, LMCompatRegistry.PALADIN_HELMET.get());
	}

	public static boolean paladinLegs(AbstractGolemEntity<?, ?> golem) {
		return has(golem, EquipmentSlot.LEGS, LMCompatRegistry.PALADIN_SHINGUARD.get());
	}

	/** 穿着指定护腿时返回 {@code 1 + LEGS_DAMAGE_BONUS}，否则 1。 */
	public static float legsMult(AbstractGolemEntity<?, ?> golem, Object legsItem) {
		return has(golem, EquipmentSlot.LEGS, legsItem) ? 1f + LEGS_DAMAGE_BONUS : 1f;
	}

	/** 浮云头盔：对落云/雷电拳/天穹激光的伤害倍率（穿头盔 1.5，否则 1）。 */
	public static float cloudHelmetMult(AbstractGolemEntity<?, ?> golem) {
		return cloudHelmet(golem) ? 1f + HEAD_DAMAGE_BONUS : 1f;
	}

	/** 本地化技能名，供护甲 tooltip 里的 %s 使用。 */
	public static Component skillName(NamedEntry<?> entry) {
		return Component.translatable(entry.getDescriptionId());
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

	private static boolean has(AbstractGolemEntity<?, ?> golem, EquipmentSlot slot, Object item) {
		return golem.getItemBySlot(slot).is((Item) item);
	}

	/** 是否穿着指定胸甲。胸甲的特性是「无视生命值要求」，对所有阶段门都成立。 */
	public static boolean hasChest(AbstractGolemEntity<?, ?> golem, Object item) {
		return has(golem, EquipmentSlot.CHEST, item);
	}

	/**
	 * 血量是否严格低于 {@code ratio}。Boss 的 {@code Crackiness.byFraction} 是严格小于：
	 * 恰好 65% 仍算上一阶段。
	 */
	public static boolean isBelow(AbstractGolemEntity<?, ?> golem, float ratio) {
		return golem.getHealth() < golem.getMaxHealth() * ratio;
	}

	/**
	 * 是否处于「终极形态」：穿了该部位，或血量到阈值。胸甲的特性就是第二条分支。
	 */
	public static boolean isFinalForm(AbstractGolemEntity<?, ?> golem, EquipmentSlot slot, Object item, float ratio) {
		if (has(golem, slot, item)) return true;
		return isBelow(golem, ratio);
	}

	public static boolean isFinalForm(AbstractGolemEntity<?, ?> golem, EquipmentSlot slot, Object item) {
		return isFinalForm(golem, slot, item, PHASE_TWO);
	}

	/** 堕落圣骑：二阶段效果（红光、追加斩、流血）。 */
	public static boolean paladinFinalForm(AbstractGolemEntity<?, ?> golem) {
		return isFinalForm(golem, EquipmentSlot.CHEST, LMCompatRegistry.PALADIN_CHESTPLATE.get());
	}

	/** 浮云：二阶段效果（暗色形态、雷电拳/天雷等）。 */
	public static boolean cloudFinalForm(AbstractGolemEntity<?, ?> golem) {
		return isFinalForm(golem, EquipmentSlot.CHEST, LMCompatRegistry.CLOUD_CHESTPLATE.get());
	}

	/**
	 * 浮云：一阶段专属技能（一字落云、落云）是否可用。
	 * <p>
	 * Boss 只在非 angry 状态放这两招，所以默认要求血量仍在 65% 以上。穿浮云胸甲时无视这条限制 ——
	 * 胸甲的意义是「解放全部技能」，一阶段与二阶段的门同时打开，而不是把一阶段的锁上。
	 */
	public static boolean cloudFirstPhaseSkill(AbstractGolemEntity<?, ?> golem) {
		return hasChest(golem, LMCompatRegistry.CLOUD_CHESTPLATE.get()) || !isBelow(golem, PHASE_TWO);
	}

	/** 湮灭构造体：二阶段效果。 */
	public static boolean obliteratorPhaseTwo(AbstractGolemEntity<?, ?> golem) {
		return isFinalForm(golem, EquipmentSlot.CHEST, LMCompatRegistry.OBLITERATOR_CHESTPLATE.get(), PHASE_TWO);
	}

	/** 湮灭构造体：三阶段效果。 */
	public static boolean obliteratorPhaseThree(AbstractGolemEntity<?, ?> golem) {
		return isFinalForm(golem, EquipmentSlot.CHEST, LMCompatRegistry.OBLITERATOR_CHESTPLATE.get(), PHASE_THREE);
	}

	/** AQUA 百分比文本，供 tooltip 内联。 */
	public static MutableComponent pct(float ratio) {
		return Component.literal(Math.round(ratio * 100) + "%").withStyle(ChatFormatting.AQUA);
	}

	/** 二阶段阈值文本（AQUA）。 */
	public static MutableComponent phaseTwoPct() {
		return pct(PHASE_TWO);
	}

	/** 「仅在低于 65% 时可用」提示行（调用方负责再套 GREEN）。 */
	public static MutableComponent phaseTwoGate() {
		return MGLangData.SKILL_HEALTH_GATE.get(pct(PHASE_TWO));
	}

	/** 「仅在高于 65% 时可用」提示行（调用方负责再套 GREEN）。 */
	public static MutableComponent phaseTwoAboveGate() {
		return MGLangData.SKILL_HEALTH_GATE_ABOVE.get(pct(PHASE_TWO));
	}

	/** 「仅在低于 30% 时可用」提示行（调用方负责再套 GREEN）。 */
	public static MutableComponent phaseThreeGate() {
		return MGLangData.SKILL_HEALTH_GATE.get(pct(PHASE_THREE));
	}

	/** 本地化材质名，供 {@code skill_ignore_health} 那一行使用。 */
	public static Component materialName(String id) {
		return Component.translatable("golem_material.legendary_monsters." + id);
	}

}
