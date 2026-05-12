package dev.xkmc.modulargolems.compat.materials.cataclysm;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;

public class CataEventHandler {

	@SubscribeEvent
	public static void onExplosion(ExplosionEvent.Detonate event) {
		var direct = event.getExplosion().getDirectSourceEntity();
		var owner = event.getExplosion().getIndirectSourceEntity();
		if (direct == null || !(owner instanceof AbstractGolemEntity<?, ?> golem)) return;
		boolean fireball = GolemCataProxy.isIgnisExplosive(direct);
		boolean strike = GolemCataProxy.isIgnisStrike(direct);
		if (fireball || strike) {
			if (!golem.isHostile()) event.getAffectedBlocks().clear();
			event.getAffectedEntities().removeIf(e -> {
				if (e instanceof ItemEntity) return true;
				if (e instanceof LivingEntity le) {
					if (!golem.canAttack(le)) return true;
					if (GolemCataProxy.isAbyssFireball(direct)) {
						GolemCataProxy.inflictStun(golem, le, 60);
						le.invulnerableTime = 0;
					} else if (GolemCataProxy.isSoul(direct)) {
						GolemCataProxy.inflictStun(golem, le, 40);
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

	@SubscribeEvent
	public static void onMobGrief(EntityMobGriefingEvent event) {
		var owner = GolemCataProxy.getOwner(event.getEntity());
		if (owner instanceof AbstractGolemEntity<?, ?> golem) {
			if (!golem.isHostile()) {
				event.setCanGrief(false);
			}
		}
	}

}
