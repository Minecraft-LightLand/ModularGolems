package dev.xkmc.modulargolems.content.modifier.base;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;
import java.util.function.Predicate;

public class TargetBonusModifier extends GolemModifier {

	private final Predicate<LivingEntity> pred;

	public TargetBonusModifier(Predicate<LivingEntity> pred) {
		super(StatFilterType.ATTACK, 3);
		this.pred = pred;
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (pred.test(event.getEntity())) {
			event.setAmount((float) (event.getAmount() * (1 + level * ModConfig.COMMON.targetDamageBonus.get())));
		}
	}

	public List<MutableComponent> getDetail(int v) {
		int perc = (int) (ModConfig.COMMON.targetDamageBonus.get() * 100 * v);
		return List.of(Component.translatable(getDescriptionId() + ".desc", perc).withStyle(ChatFormatting.GREEN));
	}
}
