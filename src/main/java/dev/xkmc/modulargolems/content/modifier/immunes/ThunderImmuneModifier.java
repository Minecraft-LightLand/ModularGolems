package dev.xkmc.modulargolems.content.modifier.immunes;

import dev.xkmc.l2core.base.effects.EffectUtil;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.Consumer;

public class ThunderImmuneModifier extends GolemModifier {

	public ThunderImmuneModifier() {
		super(StatFilterType.HEALTH, 2);
	}

	@Override
	public boolean onAttacked(AbstractGolemEntity<?, ?> entity, DamageData.Attack event, int level) {
		if (level > 0 && event.getSource().is(DamageTypeTags.IS_LIGHTNING)) {
			EffectUtil.addEffect(entity, new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200), entity);
			entity.heal(MGConfig.COMMON.thunderHeal.get() * level);
			Player player = entity.getOwner();
			if (player instanceof ServerPlayer pl) {
				GolemTriggers.THUNDER.get().trigger(pl);
			}
			return true;
		}
		return false;
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.THUNDER_IMMUNE);
	}

	public List<MutableComponent> getDetail(int v) {
		int heal = MGConfig.COMMON.thunderHeal.get() * v;
		return List.of(Component.translatable(getDescriptionId() + ".desc", heal).withStyle(ChatFormatting.GREEN));
	}

}
