package dev.xkmc.modulargolems.util;

import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.events.event.GolemCollectItemEvent;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GolemUtils {

	public static float adjustedDamage(float base, float bonus) {
		if (bonus > base) {
			return (float) Math.sqrt(bonus / base) * base * 2;
		} else return base + bonus;
	}

	public static List<ItemStack> collectFromGolem(Level level, ItemStack golem, boolean getUpgrades) {
		List<ItemStack> ans = new ArrayList<>();
		if (!(golem.getItem() instanceof GolemHolder<?, ?> holder))
			return ans;
		var root = golem.getTag();
		if (root == null) return ans;
		if (root.contains(GolemHolder.KEY_EQUIPMENTS, Tag.TAG_COMPOUND)) {
			var equipMap = root.getCompound(GolemHolder.KEY_EQUIPMENTS);
			for (var ent : equipMap.getAllKeys()) {
				if (!equipMap.contains(ent, Tag.TAG_COMPOUND)) continue;
				var item = ItemStack.of(equipMap.getCompound(ent));
				if (!item.isEmpty()) {
					ans.add(item);
				}
			}
		}
		if (root.contains(GolemHolder.KEY_ENTITY, Tag.TAG_COMPOUND)) {
			AbstractGolemEntity<?, ?> entity = holder.createDummy(golem, level);
			if (entity != null) {
				entity.addItemsToList(ans);
				if (ModList.get().isLoaded(CuriosApi.MODID)) {
					CurioCompatRegistry.getAllItems(entity, ans);
				}
				MinecraftForge.EVENT_BUS.post(new GolemCollectItemEvent(entity, ans));
			}
		}
		if (getUpgrades) {
			Map<Item, ItemStack> upgrades = new LinkedHashMap<>();
			for (var e : GolemHolder.getUpgrades(golem)) {
				if (!upgrades.containsKey(e.asItem())) {
					upgrades.put(e.asItem(), e.asItem().getDefaultInstance());
				} else {
					upgrades.get(e.asItem()).grow(1);
				}
			}
			ans.addAll(upgrades.values());
		}
		return ans;
	}

}
