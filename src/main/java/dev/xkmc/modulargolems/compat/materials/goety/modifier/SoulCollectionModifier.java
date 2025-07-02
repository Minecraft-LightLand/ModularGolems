package dev.xkmc.modulargolems.compat.materials.goety.modifier;

import com.Polarice3.Goety.utils.SEHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class SoulCollectionModifier extends GolemModifier {

	public SoulCollectionModifier() {
		super(StatFilterType.ATTACK, 4);
	}

	@Override
	public void onKillTarget(AbstractGolemEntity<?, ?> golem, LivingEntity entity, LivingDeathEvent event, int level) {
		Player owner = golem.getOwner();
		if (owner == null || owner instanceof FakePlayer) return;
		for (int i = 0; i < level; i++) {
			SEHelper.handleKill(owner, event.getEntity(), event.getSource());
		}
	}

}
