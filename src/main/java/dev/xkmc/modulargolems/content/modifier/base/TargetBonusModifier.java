package dev.xkmc.modulargolems.content.modifier.base;

import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.function.Predicate;

public class TargetBonusModifier extends GolemModifier {

	private final Predicate<LivingEntity> pred;

	public TargetBonusModifier(Predicate<LivingEntity> pred) {
		super(StatFilterType.ATTACK, 2);
		this.pred = pred;
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, DamageData.Offence event, int level) {
		if (pred.test(event.getTarget())) {
			float factor = (float) (1 + level * MGConfig.COMMON.targetDamageBonus.get());
			event.addHurtModifier(DamageModifier.multTotal(factor, getRegistryName()));
		}
	}

	public List<MutableComponent> getDetail(int v) {
		int perc = (int) (MGConfig.COMMON.targetDamageBonus.get() * 100 * v);
		return List.of(Component.translatable(getDescriptionId() + ".desc", perc).withStyle(ChatFormatting.GREEN));
	}
}
