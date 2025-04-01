package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.common.StackableEffect;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class MaledictusAttackModifier extends GolemModifier {

	public MaledictusAttackModifier() {
		super(StatFilterType.ATTACK, 2);
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, DamageData.Offence cache, int level) {
		StackableEffect.addTo(CataCompatRegistry.EFF_FORCE, entity, 100, level == 1 ? 0 : 4, entity);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", v == 1 ? 1 : 5).withStyle(ChatFormatting.GREEN));
	}

	@Override
	public void modifySource(AbstractGolemEntity<?, ?> golem, CreateSourceEvent event, int value) {
		event.enable(DefaultDamageState.BYPASS_ARMOR);
	}

}
