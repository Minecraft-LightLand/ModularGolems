package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.DogGolemArmorItem;
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
		// 人形
		if (golem instanceof HumanoidGolemEntity) {
			// 对箭,装到副手
			if (stack.getItem() instanceof ArrowItem) {
				event.setSlot(stack.getCount(), EquipmentSlot.OFFHAND);
			}
			// 对弓,装到主手,副手
			if (stack.getItem() instanceof BowItem) {
				event.setSlot(1, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
			}
			// 旗帜,头部
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

		// 大傀儡
		if (golem instanceof MetalGolemEntity) {
			if (stack.getItem() instanceof MetalGolemArmorItem mgai) {
				event.setSlot(1, mgai.getSlot());
			} else if (stack.getItem() instanceof MetalGolemWeaponItem || stack.is(MGTagGen.LARGE_GOLEM_WEAPONS)) {
				event.setSlot(1, EquipmentSlot.MAINHAND);
			} else if (stack.getItem() instanceof BannerItem) {
				event.setSlot(1, EquipmentSlot.HEAD, EquipmentSlot.FEET);
			}
		}

		// 狗傀儡
		if (golem instanceof DogGolemEntity) {
			if (stack.getItem() instanceof BannerItem) {
				event.setSlot(1, EquipmentSlot.HEAD);
			}
			if (stack.getItem() instanceof DogGolemArmorItem mgai) {
				event.setSlot(1, EquipmentSlot.CHEST);
			}
		}
	}

	@SubscribeEvent
	public static void isThrowable(GolemThrowableEvent event) {
		// 判断当前装备的物品是否属于投掷物品(仅含三叉戟)如果是，则将该物品设置为可投掷，并创建一个ThrownTrident对象
		if (event.getStack().getItem() instanceof TridentItem) {
			event.setThrowable(level -> {
				// 创建投掷物模拟投掷行为
				var ans = new ThrownTrident(level, event.getEntity(), event.getStack());
				// 该投掷物不可拾取
				ans.pickup = AbstractArrow.Pickup.DISALLOWED;
				return ans;
			});
		}
	}
}
