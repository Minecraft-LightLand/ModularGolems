package dev.xkmc.modulargolems.compat.materials.tinker.modifier;

import dev.xkmc.l2library.base.effects.EffectUtil;
import dev.xkmc.modulargolems.compat.materials.tinker.TCCompatRegistry;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class HepatizonDefenseModifier extends GolemModifier {

	public HepatizonDefenseModifier() {
		super(StatFilterType.HEALTH, 5);
	}

	@Override
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (event.getSource().is(DamageTypeTags.BYPASSES_EFFECTS) ||
				event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return;
		}
		var eff = TCCompatRegistry.EFF_HEPATIZON.get();
		var ins = entity.getEffect(eff);
		if (ins == null) return;
		int reduction = (ins.getAmplifier() + 1) * 2;
		event.setAmount(Math.max(0, event.getAmount() - reduction));
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		var eff = TCCompatRegistry.EFF_HEPATIZON.get();
		var dur = 100;
		int amp = Math.min(level * 2, (int) (event.getAmount() / 4)) - 1;
		if (amp < 0) return;
		MobEffectInstance ins = new MobEffectInstance(eff, dur, amp - 1);
		EffectUtil.addEffect(entity, ins, EffectUtil.AddReason.SELF, entity);
	}

}
