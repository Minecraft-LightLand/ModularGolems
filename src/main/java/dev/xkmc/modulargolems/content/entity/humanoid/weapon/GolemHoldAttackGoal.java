package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.mob_weapon_api.api.goals.IMeleeGoal;
import dev.xkmc.mob_weapon_api.example.goal.SmartHoldRangedAttackGoal;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

/**
 * 让「近战型 HOLD 武器」（灾变绞肉锯、歼灭刃锯等持续近战武器）**进入持握射程后**才由 hold goal 接管；
 * 目标在射程外时让位给 melee goal。
 * <p>
 * 原因：大跳逻辑在 {@code GolemMeleeGoal.doRealAttack → JumpAttackHelper.tryJumpAttack}，而 hold goal 与
 * melee goal 同占 {@code MOVE+LOOK}，会互相屏蔽；hold goal 一旦常驻，melee goal 就不 tick → 永不放大跳。
 * 远处交给 melee goal（可大跳/位移），贴近后才持握切割。
 */
public class GolemHoldAttackGoal<E extends Mob> extends SmartHoldRangedAttackGoal<E> {

	public GolemHoldAttackGoal(E mob, IMeleeGoal melee, double speed) {
		super(mob, melee, speed);
	}

	@Override
	public boolean canUse() {
		if (!super.canUse()) return false;
		ItemStack stack = mob.getItemInHand(holder.getWeaponHand());
		var status = getWeaponStatus(stack);
		if (status.map(WeaponStatus::isMelee).orElse(false)) {
			var target = mob.getTarget();
			if (target == null) return false;
			return mob.distanceTo(target) < range(stack);
		}
		return true;
	}

}
