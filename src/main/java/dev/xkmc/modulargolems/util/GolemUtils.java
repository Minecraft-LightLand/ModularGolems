package dev.xkmc.modulargolems.util;

import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.events.event.GolemCollectItemEvent;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
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
		var equipMap = golem.get(GolemItems.EQUIPMENTS);
		var entityData = golem.get(GolemItems.ENTITY);
		if (equipMap != null) {
			for (var ent : equipMap.equipments().entrySet()) {
				if (!ent.getValue().isEmpty()) {
					ans.add(ent.getValue().copy());
				}
			}
		}
		if (entityData != null) {
			AbstractGolemEntity<?, ?> entity = holder.createDummy(golem, level);
			if (entity != null) {
				entity.addItemsToList(ans);
				if (ModList.get().isLoaded(CuriosApi.MODID)) {
					CurioCompatRegistry.getAllItems(entity, ans);
				}
				NeoForge.EVENT_BUS.post(new GolemCollectItemEvent(entity, ans));
			}
		}
		if (getUpgrades) {
			Map<Item, ItemStack> upgrades = new LinkedHashMap<>();
			for (var e : GolemHolder.getUpgrades(golem).upgrades()) {
				if (!upgrades.containsKey(e)) {
					upgrades.put(e, e.getDefaultInstance());
				} else {
					upgrades.get(e).grow(1);
				}
			}
			ans.addAll(upgrades.values());
		}
		return ans;
	}

}
