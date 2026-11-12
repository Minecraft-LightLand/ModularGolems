package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.cataclysm_mux.MWCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.VortexField;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public class EnderGuardianTeleportSlamModifier extends GolemModifier implements EarthquakeHelper.Modifier {
	public static final int COOLDOWN = 300;
	/** 起跳可及的最大水平距离（格）。 */
	public static final int MAX_RANGE = 25;

	public EnderGuardianTeleportSlamModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (EarthquakeHelper.isLeaping(entity)) event.setAmount(event.getAmount() * EarthquakeHelper.LEAP_DAMAGE_MULT);
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return (double) MAX_RANGE * MAX_RANGE;
	}

	/**
	 * 跃迁猛击只在完整形态（半血以下，或被末地守卫胸甲钉住）可用。
	 */
	@Override
	public boolean canUse(AbstractGolemEntity<?, ?> golem) {
		return CataArmorHelper.guardianFinalForm(golem);
	}

	@Override
	public boolean useServerJump() {
		return true;
	}

	@Override
	public void performJump(AbstractGolemEntity<?, ?> golem, int lv) {
		var target = golem.getTarget();
		if (target != null && target.isAlive() && !(golem.getControllingPassenger() instanceof Player)) {

			if (!golem.level().isClientSide()) {
				MWCataProxy.spawnVortex(golem, target.position());
				VortexField.add(golem, target.position());
			}
			golem.teleportTo(target.getX(), target.getY() + 10, target.getZ());
			golem.setDeltaMovement(0, 0, 0);
			golem.playSound(SoundEvents.SHULKER_TELEPORT, 1.0F, 1.0F);
			return;
		}
		if (golem.getControllingPassenger() == null) {
			golem.addDeltaMovement(new Vec3(0, 1.3, 0));
			return;
		}

		if (target != null && target.isAlive() && golem.predicateTarget(target)) {
			if (!golem.level().isClientSide()) {
				MWCataProxy.spawnVortex(golem, target.position());
				VortexField.add(golem, target.position());
			}
			golem.teleportTo(target.getX(), target.getY() + 10, target.getZ());
		} else {
			Vec3 look = golem.getLookAngle();
			Vec3 flat = new Vec3(look.x, 0, look.z);
			if (flat.lengthSqr() < 1e-8) flat = new Vec3(0, 0, 1);
			flat = flat.normalize();
			golem.teleportTo(golem.getX() + flat.x * 8, golem.getY() + 8, golem.getZ() + flat.z * 8);
		}
		golem.setDeltaMovement(0, 0, 0);
		golem.playSound(SoundEvents.SHULKER_TELEPORT, 1.0F, 1.0F);
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int lv) {
		GolemCataProxy.massDestruction(golem);
		GolemCataProxy.guardianSlamLanding(golem);
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		// 末地守卫胸甲：半血以下（或被胸甲钉在最终形态）时冷却减半
		return CataArmorHelper.guardianFastSlam(golem) ? COOLDOWN / 2 : COOLDOWN;
	}


		@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(
				Component.translatable(getDescriptionId() + ".desc",
						EarthquakeHelper.leapReduction(),
						CataArmorHelper.healthPct(CataArmorHelper.HALF_HEALTH))
						.withStyle(ChatFormatting.GREEN),
				MGLangData.SKILL_HEALTH_GATE.get(
						CataArmorHelper.healthPct(CataArmorHelper.HALF_HEALTH))
						.withStyle(ChatFormatting.GREEN));
	}

}
