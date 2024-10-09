package dev.xkmc.modulargolems.compat.musket;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GolemMusketCompat {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(GolemMusketCompat.class);
	}

	@SubscribeEvent
	public static void onAddEntity(EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof HumanoidGolemEntity e) {
			e.goalSelector.addGoal(2, new GolemMusketGoal(e));
		}
	}

}
