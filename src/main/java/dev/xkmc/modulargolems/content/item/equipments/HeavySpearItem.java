package dev.xkmc.modulargolems.content.item.equipments;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class HeavySpearItem extends MetalGolemWeaponItem {

	private final int damage;

	public HeavySpearItem(Properties properties, int attackDamage, double percentAttack, float range, float sweep) {
		super(properties, attackDamage, percentAttack, range, sweep);
		this.damage = attackDamage;
	}

	public static boolean canSmashAttack(LivingEntity user) {
		return user.fallDistance > 1.5F && !user.isFallFlying();
	}

	@Override
	public float getAttackDamageBonus(Entity target, float amount, DamageSource source) {
		if (source.getDirectEntity() instanceof LivingEntity user) {
			if (!canSmashAttack(user)) {
				return 0;
			} else {
				double fall = user.fallDistance;
				double bonus;
				if (fall <= 3) {
					bonus = 4 * fall;
				} else if (fall <= 8) {
					bonus = 12 + 2 * (fall - 3);
				} else {
					bonus = 22 + fall - 8;
				}
				if (user.level() instanceof ServerLevel sl) {
					bonus += EnchantmentHelper.modifyFallBasedDamage(sl, user.getWeaponItem(), target, source, 0) * fall;
				}
				return (float) bonus * Math.max(1, amount / damage);
			}
		}
		return 0;
	}

}
