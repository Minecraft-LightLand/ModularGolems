package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.github.L_Ender.cataclysm.config.CMConfig;
import com.github.L_Ender.cataclysm.init.ModEffect;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class IgnisAttackModifier extends GolemModifier {

	public IgnisAttackModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> golem, LivingHurtEvent event, int level) {
		LivingEntity target = event.getEntity();
		var eff = ModEffect.EFFECTBLAZING_BRAND.get();
		var old = target.getEffect(eff);
		int i = old == null ? 0 : Math.min(4, old.getAmplifier() + 1);
		MobEffectInstance ins = new MobEffectInstance(eff, 240, i, false, true, true);
		target.addEffect(ins);
		golem.heal(level * (float) CMConfig.IgnisHealingMultiplier * (float) (i + 1));
	}

	@Override
	public void modifySource(AbstractGolemEntity<?, ?> golem, CreateSourceEvent event, int value) {
		if (golem.getHealth() < golem.getMaxHealth() / 2) {
			if (event.getResult() != null && event.getResult().toRoot() == L2DamageTypes.MOB_ATTACK) {
				event.enable(DefaultDamageState.BYPASS_ARMOR);
			}
		}
	}

}
