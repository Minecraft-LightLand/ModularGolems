package dev.xkmc.modulargolems.content.entity.goals;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.Vec3;

public class MaceHelper {

	public static void doMaceAttack(AbstractGolemEntity<?, ?> golem, LivingEntity target) {
		if (golem.fallDistance < 1) return;
		if (!(golem.level() instanceof ServerLevel sl)) return;
		golem.setDeltaMovement(Vec3.ZERO);
		sl.levelEvent(LevelEvent.PARTICLES_SMASH_ATTACK, target.getOnPos(), 750);
		if (target.onGround()) {
			SoundEvent soundevent = golem.fallDistance > 5.0F ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
			sl.playSound(null, golem.getX(), golem.getY(), golem.getZ(), soundevent, golem.getSoundSource(), 1.0F, 1.0F);
		} else {
			sl.playSound(null, golem.getX(), golem.getY(), golem.getZ(), SoundEvents.MACE_SMASH_AIR, golem.getSoundSource(), 1.0F, 1.0F);
		}
		knockback(sl, golem, target);
	}

	private static void knockback(Level level, AbstractGolemEntity<?, ?> user, Entity target) {
		var list = level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(3.5F), user::predicateTarget);
		for (var e : list) {
			Vec3 vec3 = e.position().subtract(target.position());
			double d0 = getKnockbackPower(user, e, vec3);
			Vec3 vec31 = vec3.normalize().scale(d0);
			if (d0 > 0.0F) {
				e.push(vec31.x, 0.7F, vec31.z);
				if (e instanceof ServerPlayer sp) {
					sp.connection.send(new ClientboundSetEntityMotionPacket(sp));
				}
			}
		}
	}

	private static double getKnockbackPower(Mob user, LivingEntity target, Vec3 diff) {
		var kbres = target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
		return (3.5F - diff.length()) * 0.7F * (user.fallDistance > 5.0F ? 2 : 1) * (1.0F - kbres);
	}

	public static void doMaceAirMove(AbstractGolemEntity<?, ?> golem, LivingEntity target) {
		var speed = golem.getAttributeValue(Attributes.MOVEMENT_SPEED);
		var acc = Math.max(0.25, speed) * 0.1;
		var max = Math.max(0.3, speed);
		var v = golem.getDeltaMovement();
		var diff = target.position().subtract(golem.position()).multiply(1, 0, 1);
		if (v.multiply(1, 0, 1).length() < max && diff.length() > 1) {
			golem.addDeltaMovement(diff.normalize().scale(acc));
		}
	}

}
