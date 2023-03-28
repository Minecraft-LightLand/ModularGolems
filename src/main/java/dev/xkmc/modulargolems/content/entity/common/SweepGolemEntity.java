package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

@SerialClass
public abstract class SweepGolemEntity<T extends SweepGolemEntity<T, P>, P extends IGolemPart<P>> extends AbstractGolemEntity<T, P> {

	protected SweepGolemEntity(EntityType<T> type, Level level) {
		super(type, level);
	}

	protected boolean performRangedDamage(Entity target, float damage, double kb) {
		boolean flag = performDamageTarget(target, damage, kb);
		double range = getAttributeValue(GolemTypes.GOLEM_SWEEP.get());
		if (range > 0) {
			var list = getLevel().getEntities(target, target.getBoundingBox().inflate(range),
					e -> e instanceof LivingEntity le && e instanceof Enemy && (!(e instanceof Creeper)) && this.canAttack(le));
			for (Entity t : list) {
				flag |= performDamageTarget(t, damage, kb);
			}
		}
		return flag;
	}

	protected AABB getTargetBoundingBox(Entity target) {
		return target.getBoundingBox();
	}

	/**
	 * please be aware of lastHurtByPlayer
	 */
	protected abstract boolean performDamageTarget(Entity target, float damage, double kb);

}
