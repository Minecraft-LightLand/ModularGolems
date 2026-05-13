package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.mob_weapon_api.api.projectile.ProjectileWeaponUser;
import dev.xkmc.mob_weapon_api.api.simple.IHoldWeaponBehavior;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemAimState;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGDamageTypes;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class SonicCannonBehavior implements IHoldWeaponBehavior {

	public static void setCharge(ItemStack stack, int charge) {
		stack.set(GolemItems.DC_CHARGE, charge);
	}

	public static int getCharge(ItemStack stack) {
		return stack.getOrDefault(GolemItems.DC_CHARGE, 0);
	}

	@Override
	public boolean isValid(ProjectileWeaponUser user, ItemStack stack) {
		return user.user() instanceof MetalGolemEntity;
	}

	@Override
	public double range(LivingEntity le, ItemStack stack) {
		return 15;
	}

	@Override
	public int holdTime(LivingEntity le, ItemStack stack) {
		return getCharge(stack) > 0 ? 0 : 34;
	}

	@Override
	public void tickUsing(ProjectileWeaponUser user, ItemStack stack, int time) {
		if (getCharge(stack) <= 0 && time == 1) {
			user.user().playSound(SoundEvents.WARDEN_SONIC_CHARGE, 3.0F, 1.0F);
		}
	}

	@Override
	public int trigger(ProjectileWeaponUser user, ItemStack stack, LivingEntity target, int time) {
		if (!(user.user() instanceof MetalGolemEntity golem)) return 20;
		int charge = getCharge(stack);
		if (charge <= 0) setCharge(stack, 2);
		else setCharge(stack, charge - 1);
		float factor = MGConfig.COMMON.sonicCannonDamageFactor.get().floatValue();
		int cd = 20;
		for (var e : golem.getMaterials()) {
			if (e.id().equals(ModularGolems.loc("sculk"))) {
				factor += MGConfig.COMMON.sonicCannonResonanceBonus.get().floatValue();
				if (e.getPart().getPart() == MetalGolemPartType.BODY) {
					cd = 10;
				}
			}
		}
		if (user.user().level() instanceof ServerLevel sl) {
			float dmg = Math.max(10, (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE) * factor);
			Vec3 dst = target.getEyePosition();
			var offset = 7 / 16f * golem.getScale();
			Vec3 src = BowPoseUtil.getOrigin(golem).add(0, offset, 0);
			Vec3 dir = dst.subtract(src).normalize();
			shoot(sl, user.user(), src, dir, dmg);
		}
		return cd;
	}


	public static void shoot(ServerLevel level, LivingEntity user, Vec3 src, Vec3 dir, float damage) {
		for (int i = 1; i < 17; ++i) {
			Vec3 vec33 = src.add(dir.scale(i));
			level.sendParticles(ParticleTypes.SONIC_BOOM, vec33.x, vec33.y, vec33.z, 1, 0.0F, 0.0F, 0.0F, 0.0F);
		}

		List<LivingEntity> target = new ArrayList<>();
		AABB aabb = new AABB(src, src.add(dir.scale(17.0F)));

		for (Entity e : level.getEntities(user, aabb)) {
			if (e instanceof LivingEntity x) {
				AABB box = x.getBoundingBox().inflate(1.0F);
				for (int i = 0; i <= 17; ++i) {
					if (box.contains(src.add(dir.scale(i)))) {
						target.add(x);
						break;
					}
				}
			}
		}

		for (LivingEntity e : target) {
			var source = new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(MGDamageTypes.ECHO), user);
			e.hurt(source, damage);
			double d1 = (double) 0.5F * ((double) 1.0F - e.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
			double d0 = (double) 2.5F * ((double) 1.0F - e.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
			e.push(dir.x() * d0, dir.y() * d1, dir.z() * d0);
		}
		level.playSound(null, user.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 3.0F, 1.0F);

	}

}
