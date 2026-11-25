package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public interface GolemImpls extends
		PowerableMob, TransientStateContainer.View,
		GolemReforgeImpl, GolemConfigImpl, GolemTrackerImpl, GolemInvManager.View, GolemPermissionImpl, GolemTargetImpl,
		GolemBeamImpl, GolemUseItemImpl {

	/**
	 * 傀儡自身攻击附带的击退。冲锋技能撞到目标时复用这一份，让"撞到"与"打到"是同一手感。
	 * 大型傀儡覆写成整个掀起来（{@code MetalGolemEntity}），其余体型保持原版水平击退。
	 */
	default void applyAttackKnockback(LivingEntity target) {
		double kb = asGolem().getAttributeValue(Attributes.ATTACK_KNOCKBACK);
		if (kb <= 0) return;
		AbstractGolemEntity<?, ?> golem = asGolem();
		target.knockback(kb * 0.5,
				Mth.sin(golem.getYRot() * Mth.DEG_TO_RAD),
				-Mth.cos(golem.getYRot() * Mth.DEG_TO_RAD));
	}

	@Nullable
	default AbstractGolemEntity<?, ?> getGolemController() {
		var p = asGolem().getControllingPassenger();
		return p instanceof AbstractGolemEntity<?, ?> g ? g : null;
	}

	/** 是否有玩家正骑着我（区分"玩家操控"与"载着其它傀儡时的 AI 自主行动"）。 */
	@Nullable
	default Player getPlayerPassenger() {
		for (var e : asGolem().getPassengers()) {
			if (e instanceof Player p) return p;
		}
		return null;
	}

	default boolean hasPlayerPassenger() {
		return getPlayerPassenger() != null;
	}

}
