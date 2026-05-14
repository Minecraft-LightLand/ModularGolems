package dev.xkmc.modulargolems.content.entity.render;

import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.hostile.HostileGolemRegistry;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;

import java.util.List;
import java.util.UUID;

public record CommonGolemRenderState(
		Component name,
		ItemStack skin,
		ItemStackRenderState banner,
		List<GolemMaterial> materials,
		int id,
		boolean aggressive,
		int getVehicleId,
		float time,
		int configColor
) {

	public static CommonGolemRenderState of(AbstractGolemEntity<?, ?> e, ItemModelResolver imr, float pt) {
		var skin = ItemStack.EMPTY;
		if (CurioCompatRegistry.get() != null)
			skin = CurioCompatRegistry.get().getSkin(e);
		int vid = -1;
		var veh = e.getVehicle();
		if (veh != null) vid = veh.getId();
		var banner = getBanner(e);
		var bannerState = new ItemStackRenderState();
		if (isBanner(banner)) {
			imr.updateForLiving(bannerState, banner, ItemDisplayContext.HEAD, e);
		}
		return new CommonGolemRenderState(e.getDisplayName(), skin, bannerState, e.getMaterials(), e.getId(), e.isAggressive(), vid,
				e.tickCount + pt, e.getConfigColor());
	}

	public static ItemStack getBanner(AbstractGolemEntity<?, ?> entity) {
		ItemStack stack = entity.getItemBySlot(EquipmentSlot.HEAD);
		if (entity instanceof HumanoidGolemEntity && isBanner(stack)) {
			return ItemStack.EMPTY;
		}
		var selfFact = HostileGolemRegistry.tryGetFaction(entity);
		if (selfFact.isPresent()) {
			if (entity.hasPassenger(e -> e instanceof AbstractGolemEntity<?, ?>))
				return ItemStack.EMPTY;
			return selfFact.get().getBanner(entity, entity.getConfigColor());
		}
		if (entity instanceof MetalGolemEntity && !isBanner(stack)) {
			stack = entity.getItemBySlot(EquipmentSlot.FEET);
		}
		if (isBanner(stack)) return stack;
		var entry = entity.getConfigEntry(MGLangData.LOADING.get());
		if (entry != null) {
			entry.clientTick(entity.level(), false);
			UUID captainId = entry.squadConfig.getCaptainId();
			boolean showFlag = entity.getUUID().equals(captainId);
			if (showFlag) {
				String color = DyeColor.values()[entry.getColor()].getName();
				Item item = BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(color + "_banner"));
				return item.getDefaultInstance();
			}
		}
		return ItemStack.EMPTY;
	}

	public static boolean isBanner(ItemStack stack) {
		return stack.getItem() instanceof BannerItem;
	}

}
