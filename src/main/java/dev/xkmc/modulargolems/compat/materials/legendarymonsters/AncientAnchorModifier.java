package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.entity.CameraShakeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.function.Consumer;

public class AncientAnchorModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public AncientAnchorModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int level) {
		Vec3 entityPosition = golem.position();
		CameraShakeEntity.cameraShake(golem.level(), entityPosition, 15.0F, 0.3F, 0, 15);
		if (golem.level() instanceof ServerLevel sl)
			execute(sl, golem.getX(), golem.getY(), golem.getZ(), golem, 5f, level);
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return 16;
	}

	private void execute(ServerLevel level, double x, double y, double z, AbstractGolemEntity<?, ?> golem, float reach, int lv) {
		int n = 128;
		for (double i = 0; i < n; ++i) {
			level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
					x + (double) 0.5F + Math.cos((Math.PI * 2D) / n * i) * reach,
					y,
					z + (double) 0.5F + Math.sin((Math.PI * 2D) / n * i) * reach,
					0, 0.0F, 0.05, 0.0F, 1);
		}

		level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.NEUTRAL, 1.0F, 1.0F);
		Vec3 cen = new Vec3(x, y, z);
		for (LivingEntity le : level.getEntitiesOfClass(LivingEntity.class, (new AABB(cen, cen)).inflate(reach), e -> e != golem)
				.stream().sorted(Comparator.comparingDouble(ec -> ec.distanceToSqr(cen))).toList()) {
			if (golem.isAlliedTo(le)) continue;
			le.addEffect(new MobEffectInstance(ModEffects.STUN.get(), 20 * lv, 255, false, false));
			le.hurt(golem.damageSources().mobAttack(golem), (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE) * lv * 0.4f);
			EarthquakeHelper.launch(golem, le, 1);
		}
	}

}
