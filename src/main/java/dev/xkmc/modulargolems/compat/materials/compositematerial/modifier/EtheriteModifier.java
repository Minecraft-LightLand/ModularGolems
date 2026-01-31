package dev.xkmc.modulargolems.compat.materials.compositematerial.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class EtheriteModifier extends GolemModifier {

	public EtheriteModifier() {
		super(StatFilterType.MASS, 4);
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		var source = event.getSource();
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
		if (source.is(DamageTypeTags.BYPASSES_EFFECTS)) return;
		if (level >= 1 && source.is(DamageTypeTags.IS_PROJECTILE)) {
			event.setCanceled(true);
			return;
		}
		if (level >= 3 && source.getEntity() == null) {
			event.setCanceled(true);
			return;
		}
		super.onAttacked(entity, event, level);
	}

	@Override
	public boolean isImmuneTo(AbstractGolemEntity<?, ?> golem, MobEffectInstance ins, int level) {
		return level >= 2 && !ins.getEffect().isBeneficial() && ins.isCurativeItem(Items.MILK_BUCKET.getDefaultInstance());
	}

	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		if (golem.tickCount % 600 == 123) {//TODO config
			golem.repairWithItem();
		}
	}

}
