package dev.xkmc.modulargolems.compat.materials.eeeab.guardian;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * 无名守卫 - 招式4：砸地 / 叶刃攻击。
 * 对应 {@code GuardianLobedAttackGoal} 的 {@code SMASHATTACK_ANIMATION}（40 tick），
 * 充能时生成 8 个 {@code EntityGuardianBlade}。
 */
public class GuardianBladeModifier extends GolemModifier {	public static final int COOLDOWN = 160;
	public static final int MIN_RANGE = 3, MAX_RANGE = 16;
	/** 等级只缩短冷却（每级减半，最低 1），与其它技能一致。 */
	public static int cooldownFor(int lv) {
		return Math.max(1, COOLDOWN >> (lv - 1));
	}


	public GuardianBladeModifier() {
		super(StatFilterType.ATTACK, 3);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new GuardianBladeAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

}
