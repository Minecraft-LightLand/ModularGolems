package dev.xkmc.modulargolems.content.modifier;

import dev.xkmc.modulargolems.content.core.GolemModifier;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class ThornModifier extends GolemModifier {

	public static final double PERCENTAGE = 0.2; //TODO move to config

	public ThornModifier(int maxLevel) {
		super(maxLevel);
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (level == 0) {
			return;
		}
		DamageSource source = event.getSource();
		if (source.isMagic() || source instanceof EntityDamageSource eds && eds.isThorns()) {
			return;
		}
		if (source.getDirectEntity() instanceof LivingEntity living && living.isAlive()) {
			living.hurt(DamageSource.thorns(entity), (float) (event.getAmount() * PERCENTAGE * level));
		}
	}
}
