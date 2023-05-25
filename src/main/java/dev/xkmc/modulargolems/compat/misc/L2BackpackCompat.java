package dev.xkmc.modulargolems.compat.misc;

import dev.xkmc.l2backpack.content.remote.common.WorldStorage;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestInvWrapper;
import dev.xkmc.l2backpack.content.remote.worldchest.WorldChestItem;
import dev.xkmc.l2backpack.init.data.ItemTags;
import dev.xkmc.modulargolems.events.event.GolemEquipEvent;
import dev.xkmc.modulargolems.events.event.GolemHandleItemEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.UUID;

public class L2BackpackCompat {

	public static void register() {
		MinecraftForge.EVENT_BUS.register(L2BackpackCompat.class);
		// TODO add block tag
	}

	@SubscribeEvent
	public static void onEquip(GolemEquipEvent event) {
		if (event.getStack().is(ItemTags.DIMENSIONAL_STORAGES.tag)) {
			event.setSlot(EquipmentSlot.CHEST, 1);
		}
	}

	@SubscribeEvent
	public static void onHandleItem(GolemHandleItemEvent event) {
		//TODO move to l2backpack
		if (event.getItem().isRemoved()) return;
		ItemStack stack = event.getItem().getItem();
		if (stack.isEmpty()) return;
		ItemStack backpack = event.getEntity().getItemBySlot(EquipmentSlot.CHEST);
		if (!(backpack.getItem() instanceof WorldChestItem item)) return;
		CompoundTag tag = backpack.getOrCreateTag();
		if (!tag.contains("owner_id")) return;
		UUID id = tag.getUUID("owner_id");
		long pwd = tag.getLong("password");
		var cont = WorldStorage.get((ServerLevel) event.getEntity().level)
				.getOrCreateStorage(id, item.color.getId(), pwd);
		if (cont.isEmpty()) return;
		var storage = cont.get();
		var handler = new WorldChestInvWrapper(storage.container, id);
		ItemStack remain = ItemHandlerHelper.insertItem(handler, stack, false);
		event.getItem().setItem(remain);
	}

}
