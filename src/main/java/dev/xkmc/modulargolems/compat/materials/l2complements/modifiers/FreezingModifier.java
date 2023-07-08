package dev.xkmc.modulargolems.compat.materials.l2complements.modifiers;

import dev.xkmc.l2complements.init.registrate.LCEnchantments;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.function.Consumer;

public class FreezingModifier extends GolemModifier {

	public FreezingModifier() {
		super(StatFilterType.MASS, 3);
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		LCEnchantments.ICE_BLADE.get().doPostAttack(entity, event.getEntity(), level);
	}

	@Override
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (event.getSource().getDirectEntity() instanceof LivingEntity attacker)
			LCEnchantments.ICE_THORN.get().doPostHurt(entity, attacker, level);
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (event.getSource().is(DamageTypeTags.IS_FREEZING)) {
			event.setCanceled(true);
		}
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.FREEZE_IMMUNE);
	}

}
