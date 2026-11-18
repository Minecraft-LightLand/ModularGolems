package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;
import java.util.List;

/**
 * 雷电拳（对应 Cloud_Golem state31）：朝目标方向三段召唤雷柱。
 * 受击电爆已移到飞天重砸的落地结算里（{@link SkyfallSmashModifier}）。
 */
public class ThunderModifier extends GolemModifier {

	public static final int COOLDOWN = 200;
	/** 施放距离上限（格），对应 Boss 的 attackrange 16。 */
	public static final int MAX_RANGE = 16;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	/**
	 * 目标不足时，多余那列绕傀儡张开的**角度（弧度）**，取自 Boss t37 的 {@code {f - 0.3, f, f + 0.3}}。
	 * 是角度不是横向平移：改它只改张开角，雷柱起点始终在傀儡身上。
	 */
	public static final float SIDE_ANGLE = 0.3F;
	/** 找副目标的搜索半径（格）。 */
	public static final int SEARCH_RANGE = 16;

	/** 每级减半冷却（参考灾变鸣雷槊）。 */
	public static int cooldownFor(int lv) {
		return Math.max(1, COOLDOWN >> (lv - 1));
	}

	public ThunderModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new ThunderAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(
				Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.GREEN),
				LMArmorHelper.phaseTwoGate().withStyle(ChatFormatting.GREEN));
	}
}
