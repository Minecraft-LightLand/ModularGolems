package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.l2library.base.effects.EffectUtil;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;

public class MaledictusAttackModifier extends GolemModifier {

	public MaledictusAttackModifier() {
		super(StatFilterType.ATTACK, 2);
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		CataCompatRegistry.EFF_FORCE.get().addTo(entity, 100, level == 1 ? 0 : 4,
				EffectUtil.AddReason.SELF, entity);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", v == 1 ? 1 : 5).withStyle(ChatFormatting.GREEN));
	}

	@Override
	public void modifySource(AbstractGolemEntity<?, ?> golem, CreateSourceEvent event, int value) {
		if (event.getResult() == null) return;
		if (!event.getResult().validState(DefaultDamageState.BYPASS_ARMOR)) return;
		event.enable(DefaultDamageState.BYPASS_ARMOR);
	}

}
