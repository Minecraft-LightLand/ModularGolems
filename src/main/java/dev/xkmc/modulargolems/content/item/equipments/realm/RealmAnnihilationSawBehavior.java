package dev.xkmc.modulargolems.content.item.equipments.realm;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IInfiniteHoldBehavior;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;

/**
 * 歼灭刃锯行为（参考灾变绞肉锯）：不普通挥击（{@code canMelee=false}），无限持握让锯刃持续旋转，
 * 每 tick 对「触碰距离 = 攻击距离 - 0.5」内的敌人高频切割。
 */
public class RealmAnnihilationSawBehavior implements IInfiniteHoldBehavior {

	private static final int INTERVAL = 3;
	private static final double SWEEP_RADIUS = 2.0;
	private static final float RATIO = 0.35F;

	@Override
	public boolean canMelee() {
		return false;
	}

	@Override
	public double range(LivingEntity user, ItemStack stack) {
		return Math.max(1.0, user.getAttributeValue(ForgeMod.ENTITY_REACH.get()) - 0.5);
	}

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target, int time) {
		return 1;
	}

	@Override
	public void tickUsing(ProjectileWeaponUser user, ItemStack stack, int time) {
		if (time % INTERVAL != 0) return;
		if (!(user.user() instanceof AbstractGolemEntity<?, ?> golem)) return;
		if (golem.level().isClientSide()) return;
		var target = golem.getTarget();
		if (target == null) return;
		float dmg = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE) * RATIO;
		if (dmg <= 0) return;
		// 以「被攻击的目标」为中心做范围攻击（半径 = 武器 +2 sweep），而不是以傀儡自身为中心。
		AABB box = target.getBoundingBox().inflate(SWEEP_RADIUS);
		for (Entity e : golem.level().getEntities(golem, box)) {
			if (e instanceof LivingEntity le && golem.canAttack(le)) {
				le.invulnerableTime = 0;
				le.hurt(golem.level().damageSources().mobAttack(golem), dmg);
			}
		}
	}
}
