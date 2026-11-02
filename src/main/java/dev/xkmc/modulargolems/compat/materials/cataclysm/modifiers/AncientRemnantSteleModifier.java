package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;

public class AncientRemnantSteleModifier extends GolemModifier {

	public AncientRemnantSteleModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new AncientRemnantSteleAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	public static void addStele(LivingEntity user, LivingEntity target, int lv) {
		user.level().playLocalSound(user.getX(), user.getY(), user.getZ(), SoundEvents.RAVAGER_ROAR,
				user.getSoundSource(), 5.0F, 0.7F + user.getRandom().nextFloat() * 0.2F, false);
		float rot = user.getRandom().nextFloat() * Mth.TWO_PI;
		int branches = 8, perBranch = 16;
		double initialRadius = 2.0, radiusInc = 0.75, curve = 0.6;
		float angleInc = (float) (Math.PI * 2 / branches);
		double spawnY = user.getY() + 15;
		var center = user.position();
		for (int b = 0; b < branches; b++) {
			float baseAngle = angleInc * b + rot;
			for (int i = 0; i < perBranch; i++) {
				double radius = initialRadius + i * radiusInc;
				float angle = (float) (baseAngle + i * angleInc / initialRadius + i * curve);
				double x = center.x + radius * Math.cos(angle);
				double z = center.z + radius * Math.sin(angle);
				spawnAt(user, x, spawnY, z, 1 * (i + 1) + 10);
			}
		}
		float f = (float) Mth.atan2(target.getZ() - center.z, target.getX() - center.x);
		for (int l = 0; l < 16; l++) {
			double d2 = 1.25 * (l + 1);
			double x = center.x + Math.cos(f) * d2;
			double z = center.z + Math.sin(f) * d2;
			spawnAt(user, x, spawnY, z, (int) (5 + 1.5 * l) + 10);
		}
	}

	private static void spawnAt(LivingEntity user, double x, double y, double z, int delay) {
		GolemCataProxy.spawnDesertStele(user, x, y, z, delay);
	}

}
