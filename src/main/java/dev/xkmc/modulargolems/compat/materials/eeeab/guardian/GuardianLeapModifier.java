package dev.xkmc.modulargolems.compat.materials.eeeab.guardian;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import dev.xkmc.modulargolems.util.GolemUtils;
import dev.xkmc.modulargolems.util.SkillSound;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import java.util.function.Consumer;

public class GuardianLeapModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public GuardianLeapModifier() {
		super(StatFilterType.ATTACK, 3);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> cons) {
		cons.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (EarthquakeHelper.isLeaping(entity)) event.setAmount(event.getAmount() * EarthquakeHelper.LEAP_DAMAGE_MULT);
	}

	@Override
	public void handleEvent(AbstractGolemEntity<?, ?> golem, int value, byte event) {
		if (event == EarthquakeHelper.FLAG) {
			EarthquakeHelper.makeParticles(golem, 0, 0);
		}
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {

		return 25 * 25;
	}

	@Override
	public void performJump(AbstractGolemEntity<?, ?> golem, int lv) {
		LivingEntity target = golem.getTarget();
		if (target == null) {

			float radians = (float) Math.toRadians(golem.getYRot() + 90.0F);
			golem.setDeltaMovement(3.0 * Math.cos(radians), 1.0, 3.0 * Math.sin(radians));
			golem.hasImpulse = true;
			return;
		}

		Vec3 vec = findTargetPoint(golem, target);
		double yMotion = 1.0 + Mth.clamp(vec.y * 0.055, 0.0, 12.0);
		golem.setDeltaMovement(vec.x * 0.155, yMotion, vec.z * 0.155);
		golem.hasImpulse = true;
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int lv) {
		SkillSound.playQuakeOnly(golem, SoundEvents.GENERIC_EXPLODE, 1.5F, 1.0F + golem.getRandom().nextFloat() * 0.1F);

		float baseDamage = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);

		float damage = baseDamage * (0.8F + 0.2F * lv);

		for (LivingEntity entity : golem.level().getEntitiesOfClass(LivingEntity.class, golem.getBoundingBox().inflate(7.0, 3.0, 7.0))) {
			if (!GolemTargetUtil.canTarget(golem, entity)) continue;
			float finalDamage = GolemUtils.adjustedDamage(damage, entity.getMaxHealth() * 0.02F);
			boolean flag = entity.hurt(golem.damageSources().mobAttack(golem), finalDamage);
			if (flag) {
				EarthquakeHelper.launch(golem, entity, 0.8F);

				if (entity.getY() > golem.getY() + 3.0) {
					entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.06, 0));
				}
			}
		}

		golem.level().broadcastEntityEvent(golem, EarthquakeHelper.FLAG);
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {

		int base = 300 - 40 * (lv - 1);
		return Math.max(120, base);
	}

	public static Vec3 findTargetPoint(LivingEntity attacker, LivingEntity target) {
		Vec3 vec3 = target.position();
		float width = Math.min(target.getBbWidth(), 1.5F);
		RandomSource random = attacker.getRandom();
		double radians = Math.toRadians(attacker.getYRot() + 90.0F);
		double randomXOffset = -(1.5 + width) * Math.cos(radians) + (random.nextFloat() - 0.5) * width * 2.0;
		double randomZOffset = -(1.5 + width) * Math.sin(radians) + (random.nextFloat() - 0.5) * width * 2.0;
		return new Vec3(vec3.x - attacker.getX() + randomXOffset, vec3.y - attacker.getY(), vec3.z - attacker.getZ() + randomZOffset);
	}


	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", EarthquakeHelper.leapReduction()).withStyle(ChatFormatting.GREEN));
	}

}
