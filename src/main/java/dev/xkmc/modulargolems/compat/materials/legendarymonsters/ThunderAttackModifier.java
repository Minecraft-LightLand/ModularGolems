package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.miauczel.legendary_monsters.entity.custom.ElectricityEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.function.BiConsumer;

public class ThunderAttackModifier extends GolemModifier {

	public ThunderAttackModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new ThunderAttackGoal(entity, lv));
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		var attacker = event.getSource().getDirectEntity();
		if (attacker == null) return;

		var dir = attacker.position().subtract(entity.position()).normalize();
		double val = (dir.x * dir.x + dir.z * dir.z);
		Vec3 ax0 = val < 1e-4 ? new Vec3(1, 0, 0) :
				new Vec3(-dir.x * dir.y, val, -dir.z * dir.y).normalize();
		Vec3 ax1 = dir.cross(ax0).normalize();

		float damage = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE) * (0.2f + level * 0.2f);
		int n = 4 + level;

		for (int i = 0; i < n; i++) {
			double rad = Math.PI * 2 / n * i;
			var vec = ax1.scale(Math.sin(rad)).add(dir.scale(Math.cos(rad)));
			float angle = (float) (Math.atan2(vec.z, vec.x) * Mth.RAD_TO_DEG);
			ElectricityEntity e = new ElectricityEntity(entity, vec.x, vec.y, vec.z, entity.level(), damage, angle, 20.0F);
			Vec3 pos = entity.position().add(vec);
			e.setPos(pos.x, pos.y, pos.z);
			entity.level().addFreshEntity(e);
		}
	}
}
