package dev.xkmc.modulargolems.compat.materials.cataclysm;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CataEventHandler {

	@SubscribeEvent
	public static void onExplosion(ExplosionEvent.Detonate event) {
		var direct = event.getExplosion().getDirectSourceEntity();
		var owner = event.getExplosion().getIndirectSourceEntity();
		if (direct == null || !(owner instanceof AbstractGolemEntity<?, ?> golem)) return;
		boolean fireball = CataclysmProxy.isIgnisExplosive(direct);
		boolean strike = CataclysmProxy.isIgnisStrike(direct);
		if (fireball || strike) {
			event.getAffectedBlocks().clear();
			event.getAffectedEntities().removeIf(e -> {
				if (e instanceof LivingEntity le) {
					if (!golem.canAttack(le)) return true;
					if (CataclysmProxy.isAbyssFireball(direct)) {
						CataclysmProxy.stun(le, 60);
						le.invulnerableTime = 0;
					} else if (CataclysmProxy.isSoul(direct)) {
						CataclysmProxy.stun(le, 40);
						le.invulnerableTime = 0;
					}
				}
				if (e instanceof TraceableEntity proj) {
					if (proj.getOwner() == golem) {
						return true;
					}
				}
				return false;
			});
		}
	}

}
