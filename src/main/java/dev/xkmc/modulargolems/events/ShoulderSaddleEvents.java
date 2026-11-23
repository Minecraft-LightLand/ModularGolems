package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModularGolems.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShoulderSaddleEvents {

	/**
	 * 敌人想锁肩鞍上的乘客时，优先锁坐骑；坐骑不可攻击时才保留乘客目标。
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onTargetChange(LivingChangeTargetEvent event) {
		LivingEntity target = event.getNewTarget();
		if (target == null) return;
		Entity vehicle = target.getVehicle();
		if (!(vehicle instanceof MetalGolemEntity golem)) return;
		if (!golem.hasShoulderSaddle() || !golem.isAlive()) return;
		if (event.getEntity() instanceof Mob mob && mob.canAttack(golem)) {
			event.setNewTarget(golem);
		}
	}

}
