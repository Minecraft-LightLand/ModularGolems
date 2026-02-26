package dev.xkmc.modulargolems.compat.materials.twilightforest.modifiers;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class FieryModifier extends GolemModifier {

	private static float getPercent() {
		return (float) (double) MGConfig.COMMON.fieryDamageFactor.get();
	}

	public FieryModifier() {
		super(StatFilterType.ATTACK, MAX_LEVEL);
	}

	public List<MutableComponent> getDetail(int v) {
		int reflect = Math.round(getPercent() * v * 100);
		return List.of(Component.translatable(getDescriptionId() + ".desc", reflect).withStyle(ChatFormatting.GREEN));
	}

	@Override
	public void modifyDamage(AttackCache cache, AbstractGolemEntity<?, ?> entity, int level) {
		super.modifyDamage(cache, entity, level);
		if (!cache.getAttackTarget().fireImmune()) {
			cache.getAttackTarget().setSecondsOnFire(10);
			cache.addHurtModifier(DamageModifier.multTotal(1 + getPercent() * level));
		}
	}

}
