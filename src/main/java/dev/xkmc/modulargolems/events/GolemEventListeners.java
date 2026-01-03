package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemBeaconItem;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemWeaponItem;
import dev.xkmc.modulargolems.events.event.GolemEquipItemEvent;
import dev.xkmc.modulargolems.events.event.GolemThrowableEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.TridentItem;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.world.entity.LivingEntity.getEquipmentSlotForItem;


@Mod.EventBusSubscriber(modid = ModularGolems.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GolemEventListeners {

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onEquip(GolemEquipItemEvent event) {
		var golem = event.getEntity();
		var stack = event.getStack();
		if (golem instanceof HumanoidGolemEntity) {
			if (stack.getItem() instanceof ArrowItem) {
				event.setSlot(stack.getCount(), EquipmentSlot.OFFHAND);
			}
			if (stack.getItem() instanceof BowItem) {
				event.setSlot(1, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
			}
			if (stack.getItem() instanceof BannerItem) {
				event.setSlot(1, EquipmentSlot.HEAD);
			}

			var slot = getEquipmentSlotForItem(stack);
			if (stack.canEquip(slot, golem)) {
				if (slot == EquipmentSlot.MAINHAND) {
					event.setSlot(1, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
				} else event.setSlot(1, slot);
			}
		}


		if (golem instanceof MetalGolemEntity) {
			if (stack.getItem() instanceof MetalGolemArmorItem mgai) {
				event.setSlot(1, mgai.getSlot());
			} else if (stack.getItem() instanceof MetalGolemWeaponItem || stack.is(MGTagGen.LARGE_GOLEM_WEAPONS)) {
				event.setSlot(1, EquipmentSlot.MAINHAND);
			} else if (stack.getItem() instanceof BannerItem) {
				event.setSlot(1, EquipmentSlot.HEAD, EquipmentSlot.FEET);
			}
		}
		if (golem instanceof DogGolemEntity) {
			if (stack.getItem() instanceof BannerItem) {
				event.setSlot(1, EquipmentSlot.HEAD);
			}
		}
	}

}
