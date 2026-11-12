package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class IgnisAttackModifier extends GolemModifier {

	/**
	 * Health fraction at or below which the blade damage bypasses armor.
	 */
	public static final float ARMOR_BYPASS_HEALTH = 2f / 3f;

	public IgnisAttackModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		var threshold = Component.literal(Math.round(ARMOR_BYPASS_HEALTH * 100) + "%")
				.withStyle(ChatFormatting.AQUA);
		return List.of(Component.translatable(getDescriptionId() + ".desc", threshold).withStyle(ChatFormatting.GREEN));
	}

	@Override
	public void finalizeHurtTarget(AttackCache cache, AbstractGolemEntity<?, ?> golem, int level) {
		LivingEntity target = cache.getAttackTarget();
		float rate = MGConfig.COMMON.ignitiumHealRate.get().floatValue() * level;
		CataDispatch.stackBlazingBrand(golem, target, rate * cache.getDamageDealt(), 1);
	}

	@Override
	public void modifySource(AbstractGolemEntity<?, ?> golem, CreateSourceEvent event, int value) {
		if (CataDispatch.ignisBlue(golem)) {
			if (event.getResult() != null && event.getResult().toRoot() == L2DamageTypes.MOB_ATTACK) {
				event.enable(DefaultDamageState.BYPASS_ARMOR);
			}
		}
	}

}
