package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;

@SerialClass
public class SweepGolemEntity<T extends SweepGolemEntity<T>> extends AbstractGolemEntity<T> {

	protected SweepGolemEntity(EntityType<T> type, Level level) {
		super(type, level);
	}

	protected boolean performRangedDamage(Entity target, float damage) {
		boolean flag = performDamageTarget(target, damage);
		double range = getAttributeValue(GolemTypeRegistry.GOLEM_SWEEP.get());
		if (range > 0) {
			var list = getLevel().getEntities(target, target.getBoundingBox().inflate(range),
					e -> e instanceof LivingEntity le && e instanceof Enemy && (!(e instanceof Creeper)) && this.canAttack(le));
			for (Entity t : list) {
				flag |= performDamageTarget(t, damage);
			}
		}
		return flag;
	}

	private boolean performDamageTarget(Entity target, float damage) {
		boolean succeed = target.hurt(DamageSource.mobAttack(this), damage);
		if (succeed) {
			double kb;
			if (target instanceof LivingEntity livingentity) {
				kb = livingentity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
			} else {
				kb = 0.0D;
			}
			double d1 = Math.max(0.0D, 1.0D - kb);
			double dokb = getAttributeValue(Attributes.ATTACK_KNOCKBACK);
			target.setDeltaMovement(target.getDeltaMovement().add(0.0D, dokb * d1, 0.0D));
			this.doEnchantDamageEffects(this, target);
		}
		return succeed;
	}

}
