package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.data.GolemUpgrade;
import dev.xkmc.modulargolems.content.item.golem.GolemFacade;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.item.upgrade.AddSlotItem;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.GrindstoneEvent;
import net.neoforged.neoforge.event.entity.player.AnvilCraftEvent;

import java.util.ArrayList;

@EventBusSubscriber(modid = ModularGolems.MODID)
public class CraftEventListeners {

	@SubscribeEvent
	public static void onAnvilCraft(AnvilUpdateEvent event) {
		ItemStack stack = event.getLeft();
		ItemStack block = event.getRight();
		if (stack.getItem() instanceof GolemPart<?, ?> part && part.count <= block.getCount()) {
			if (block.is(MGTagGen.SPECIAL_CRAFT) || !stack.is(MGTagGen.ANVIL_CRAFT)) return;
			var mat = GolemMaterial.getMaterial(block);
			if (mat.isPresent()) {
				if (!GolemMaterialConfig.mayApply(part, mat.get())) return;
				ItemStack new_stack = stack.copy();
				GolemPart.setMaterial(new_stack, mat.get());
				event.setOutput(new_stack);
				event.setMaterialCost(part.count);
				event.setXpCost(1);
			}
		}
		if (stack.getItem() instanceof GolemHolder<?, ?> holder) {
			if (block.getItem() instanceof AddSlotItem item) {
				addSlot(event, holder, item.slot);
			} else if (block.getItem() instanceof UpgradeItem upgrade) {
				appendUpgrade(event, holder, upgrade);
			} else {
				fixGolem(event, holder, stack);
			}
		}
		if (stack.is(GolemItems.EMPTY_UPGRADE)) {
			var mat = GolemMaterial.getRepairMaterial(block);
			if (mat.isEmpty()) return;
			if (!GolemMaterialConfig.mayApply(GolemItems.GOLEM_BODY.get(), mat.get())) return;
			var ans = GolemFacade.setMaterial(GolemItems.FACADE.asStack(), mat.get());
			ans.setCount(stack.getCount());
			int consume = Math.min(stack.getCount(), block.getCount());
			event.setOutput(ans);
			event.setMaterialCost(consume);
			event.setXpCost(stack.getCount() - consume + 1);
		}
	}

	@SubscribeEvent
	public static void onAnvilFinish(AnvilCraftEvent.Post event) {
		if (event.getEntity().level().isClientSide())
			return;
		ItemStack stack = event.getLeft();
		ItemStack block = event.getRight();
		if (stack.getItem() instanceof GolemPart<?, ?> part && part.count <= block.getCount()) {
			var mat = GolemMaterial.getMaterial(block);
			mat.ifPresent(rl -> GolemTriggers.PART_CRAFT.get().trigger((ServerPlayer) event.getEntity(), rl));
		}
		if (stack.getItem() instanceof GolemHolder<?, ?> holder) {
			if (block.getItem() instanceof UpgradeItem) {
				ItemStack result = event.getOutput();
				var mats = GolemHolder.getMaterial(result);
				var upgrades = GolemHolder.getUpgrades(result);
				int remaining = holder.getRemaining(mats, upgrades);
				int total = upgrades.size();
				GolemTriggers.UPGRADE_APPLY.get().trigger((ServerPlayer) event.getEntity(), block, remaining, total);
			} else {
				var mats = GolemHolder.getMaterial(stack);
				var type = holder.getEntityType();
				IGolemPart<?> part = type.getBodyPart();
				if (mats.size() <= part.ordinal()) return;
				var mat = mats.get(part.ordinal());
				var ing = GolemMaterialConfig.get().getRepairIngredient(mat.id());
				if (!ing.test(block)) return;
				GolemTriggers.ANVIL_FIX.get().trigger((ServerPlayer) event.getEntity(), mat.id());
			}
		}
	}

	@SubscribeEvent
	public static void onGrindStone(GrindstoneEvent.OnPlaceItem event) {
		if (event.getTopItem().getItem() instanceof GolemHolder) {
			ItemStack copy = event.getTopItem().copy();
			if (GolemHolder.getUpgrades(copy).size() > 0) {
				GolemUpgrade.removeAll(copy);
				event.setOutput(copy);
				event.setXp(0);
			}
		}
	}

	private static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	void fixGolem(AnvilUpdateEvent event, GolemHolder<T, P> holder, ItemStack stack) {
		var data = GolemItems.ENTITY.get(stack);
		if (data == null) return;

		var ing = GolemHolder.getHealingMaterial(stack);
		if (ing == null || ing.isEmpty()) return;
		ItemStack repairStack = event.getRight();
		if (!ing.test(repairStack)) return;
		int reforge = GolemHolder.getReforge(stack);
		if (reforge > 0) {
			if (repairStack.getCount() <= reforge) {
				var result = stack.copy();
				GolemHolder.setReforge(result, reforge - repairStack.getCount());
				event.setMaterialCost(repairStack.getCount());
				event.setXpCost(1);
				event.setOutput(result);
			} else {
				var result = stack.copy();
				GolemHolder.setReforge(result, 0);
				float max = GolemHolder.getMaxHealth(result);
				float health = GolemHolder.getHealth(stack);
				int maxFix = Math.min(repairStack.getCount() - reforge, (int) Math.ceil((max - health) / max * 4));
				event.setMaterialCost(maxFix + reforge);
				event.setXpCost(reforge + maxFix);
				GolemHolder.setHealth(result, Math.min(max, health + max / 4 * maxFix));
				event.setOutput(result);
			}
		} else {
			float max = GolemHolder.getMaxHealth(stack);
			float health = GolemHolder.getHealth(stack);
			if (health >= max) return;
			int maxFix = Math.min(repairStack.getCount(), (int) Math.ceil((max - health) / max * 4));
			event.setMaterialCost(maxFix);
			event.setXpCost(maxFix);
			ItemStack result = stack.copy();
			GolemHolder.setHealth(result, Math.min(max, health + max / 4 * maxFix));
			event.setOutput(result);
		}
	}


	private static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	void addSlot(AnvilUpdateEvent event, GolemHolder<T, P> holder, int slot) {
		ItemStack stack = event.getLeft();
		ItemStack result = stack.copy();
		GolemUpgrade.addSlot(result, slot);
		event.setOutput(result);
		event.setXpCost(1);
		event.setMaterialCost(1);
	}

	private static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	void appendUpgrade(AnvilUpdateEvent event, GolemHolder<T, P> holder, UpgradeItem upgrade) {
		ItemStack stack = event.getLeft();
		var upgrades = GolemHolder.getUpgrades(stack);
		ItemStack result = appendUpgrade(stack, holder, upgrade);
		if (result.isEmpty()) return;
		event.setOutput(result);
		event.setXpCost(Math.min(39, 4 * (1 + upgrades.size())));
		event.setMaterialCost(1);
	}

	public static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	ItemStack appendUpgrade(ItemStack stack, GolemHolder<T, P> holder, UpgradeItem upgrade) {
		if (!upgrade.fitsOn(holder.getEntityType())) return ItemStack.EMPTY;
		var mats = GolemHolder.getMaterial(stack);
		var upgrades = GolemHolder.getUpgrades(stack);
		var copy = new ArrayList<>(upgrades.upgrades());
		copy.add(upgrade);
		int remaining = holder.getRemaining(mats, new GolemUpgrade(upgrades.extraSlot(), copy));
		if (remaining < 0) return ItemStack.EMPTY; // check if it overflows when adding the new upgrade
		var map = GolemMaterial.collectModifiers(GolemHolder.getMaterial(stack), upgrades);
		for (var e : upgrade.get()) {
			if (map.getOrDefault(e.mod(), 0) >= e.mod().maxLevel) return ItemStack.EMPTY;
		}
		ItemStack result = stack.copy();
		GolemUpgrade.add(result, upgrade);
		return result;
	}

}
