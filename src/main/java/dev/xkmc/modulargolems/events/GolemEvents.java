package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.GolemPart;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GolemEvents {

	@SubscribeEvent
	public static void onGolemSpawn(EntityJoinLevelEvent event) {
		if (event.getLevel().isClientSide()) return;
		if (event.getEntity() instanceof AbstractGolemEntity<?, ?> entity) {
			entity.getModifiers().forEach((k, v) -> k.onGolemSpawn(entity, v));
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onHurtPre(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		if (source.getEntity() instanceof AbstractGolemEntity<?, ?> entity) {
			entity.getModifiers().forEach((k, v) -> k.onHurtTarget(entity, event, v));
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onAttackPre(LivingAttackEvent event) {
		if (event.getSource().getEntity() instanceof AbstractGolemEntity<?, ?> entity) {
			entity.getModifiers().forEach((k, v) -> k.onAttackTarget(entity, event, v));
		}
	}

	@SubscribeEvent
	public static void onAttacked(LivingAttackEvent event) {
		if (event.getEntity() instanceof AbstractGolemEntity<?, ?> entity) {
			entity.getModifiers().forEach((k, v) -> k.onAttacked(entity, event, v));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onHurtPost(LivingHurtEvent event) {
		if (event.getEntity() instanceof AbstractGolemEntity<?, ?> entity) {
			entity.getModifiers().forEach((k, v) -> k.onHurt(entity, event, v));
		}
	}

	@SubscribeEvent
	public static void onDamaged(LivingDamageEvent event) {
		if (event.getEntity() instanceof AbstractGolemEntity<?, ?> entity) {
			entity.getModifiers().forEach((k, v) -> k.onDamaged(entity, event, v));
		}
	}

	@SubscribeEvent
	public static void onAnvilCraft(AnvilUpdateEvent event) {
		ItemStack stack = event.getLeft();
		ItemStack block = event.getRight();
		if (stack.getItem() instanceof GolemPart part && part.count <= block.getCount()) {
			var mat = GolemMaterial.getMaterial(block);
			if (mat.isPresent()) {
				ItemStack new_stack = stack.copy();
				GolemPart.setMaterial(new_stack, mat.get());
				event.setOutput(new_stack);
				event.setMaterialCost(part.count);
				event.setCost(1);
			}
		}
	}

}
