package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.github.L_Ender.cataclysm.config.CMConfig;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.AttributeGolemModifier;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class NetheriteMonstrosityEarthquakeModifier extends AttributeGolemModifier {

	public static final double RANGE = 5;

	public NetheriteMonstrosityEarthquakeModifier() {
		super(1,
				new AttrEntry(GolemTypes.STAT_ATTACK, () -> 5),
				new AttrEntry(GolemTypes.STAT_ARMOR, () -> 5),
				new AttrEntry(GolemTypes.STAT_TOUGH, () -> 5)
		);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> cons) {
		cons.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void handleEvent(AbstractGolemEntity<?, ?> golem, int value, byte event) {
		if (event == 83) {
			makeParticles(golem, 0, 0);
		}
	}

	public static void performEarthQuake(AbstractGolemEntity<?, ?> golem) {
		earthQuake(golem);
		golem.level().broadcastEntityEvent(golem, (byte) 83);
	}

	public static void earthQuake(LivingEntity le) {
		le.playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 1.0F + le.getRandom().nextFloat() * 0.1F);
		for (LivingEntity entity : le.level().getEntitiesOfClass(LivingEntity.class, le.getBoundingBox().inflate(7.0))) {
			if (!le.isAlliedTo(entity) && entity != le) {
				float damage = (float) (le.getAttributeValue(Attributes.ATTACK_DAMAGE) +
						entity.getMaxHealth() * CMConfig.MonstrositysHpdamage);
				boolean flag = entity.hurt(le.damageSources().mobAttack(le), damage);
				if (flag) {
					launch(le, entity);
				}
			}
		}
	}

	public static void makeParticles(LivingEntity le, float vec, float math) {
		if (le.level().isClientSide) {
			for (int i1 = 0; i1 < 80 + le.getRandom().nextInt(12); ++i1) {
				double DeltaMovementX = le.getRandom().nextGaussian() * 0.07;
				double DeltaMovementY = le.getRandom().nextGaussian() * 0.07;
				double DeltaMovementZ = le.getRandom().nextGaussian() * 0.07;
				float f = Mth.cos(le.yBodyRot * 0.017453292F);
				float f1 = Mth.sin(le.yBodyRot * 0.017453292F);
				float angle = 0.017453292F * le.yBodyRot + i1;
				double extraX = 2.0F * Mth.sin((float) (Math.PI + angle));
				double extraY = 0.30000001192092896;
				double extraZ = 2.0F * Mth.cos(angle);
				double theta = le.yBodyRot * 0.017453292519943295;
				++theta;
				double vecX = Math.cos(theta);
				double vecZ = Math.sin(theta);
				int hitX = Mth.floor(le.getX() + vec * vecX + extraX);
				int hitY = Mth.floor(le.getY());
				int hitZ = Mth.floor(le.getZ() + vec * vecZ + extraZ);
				BlockPos hit = new BlockPos(hitX, hitY, hitZ);
				BlockState block = le.level().getBlockState(hit.below());
				le.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, block), le.getX() + (double) vec * vecX + extraX + (double) (f * math), le.getY() + extraY, le.getZ() + (double) vec * vecZ + extraZ + (double) (f1 * math), DeltaMovementX, DeltaMovementY, DeltaMovementZ);
			}
		}

	}

	private static void launch(LivingEntity le, Entity e) {
		double d0 = e.getX() - le.getX();
		double d1 = e.getZ() - le.getZ();
		double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
		float f = 2.0F;
		e.push(d0 / d2 * f, 0.75, d1 / d2 * f);
	}

}
