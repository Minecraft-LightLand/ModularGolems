package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.UpgradeItem;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CraftEventListeners {

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
		if (stack.getItem() instanceof GolemHolder<?, ?> holder) {
			if (block.getItem() instanceof UpgradeItem upgrade) {
				appendUpgrade(event, holder, upgrade);
			} else {
				fixGolem(event, holder, stack);
			}
		}
	}

	@SubscribeEvent
	public static void onAnvilFinish(AnvilRepairEvent event) {
		if (event.getEntity().getLevel().isClientSide())
			return;
		ItemStack stack = event.getLeft();
		ItemStack block = event.getRight();
		if (stack.getItem() instanceof GolemPart part && part.count <= block.getCount()) {
			var mat = GolemMaterial.getMaterial(block);
			mat.ifPresent(rl -> GolemTriggers.PART_CRAFT.trigger((ServerPlayer) event.getEntity(), rl));
		}
		if (stack.getItem() instanceof GolemHolder<?, ?> holder) {
			if (block.getItem() instanceof UpgradeItem) {
				ItemStack result = event.getOutput();
				var mats = GolemHolder.getMaterial(result);
				var upgrades = GolemHolder.getUpgrades(result);
				int remaining = holder.getRemaining(mats, upgrades);
				GolemTriggers.UPGRADE_APPLY.trigger((ServerPlayer) event.getEntity(), block, remaining);
			} else {
				var mats = GolemHolder.getMaterial(stack);
				var type = holder.getEntityType();
				IGolemPart<?> part = type.getBodyPart();
				if (mats.size() <= part.ordinal()) return;
				var mat = mats.get(part.ordinal());
				var ing = GolemMaterialConfig.get().ingredients.get(mat.id());
				if (ing == null || !ing.test(block)) return;
				GolemTriggers.ANVIL_FIX.trigger((ServerPlayer) event.getEntity(), mat.id());
			}
		}
	}

	private static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	void fixGolem(AnvilUpdateEvent event, GolemHolder<T, P> holder, ItemStack stack) {
		if (stack.getTag() == null || !stack.getTag().contains(GolemHolder.KEY_ENTITY)) return;
		float max = GolemHolder.getMaxHealth(stack);
		float health = GolemHolder.getHealth(stack);
		if (health >= max) return;
		var mats = GolemHolder.getMaterial(stack);
		var type = holder.getEntityType();
		P part = type.getBodyPart();
		if (mats.size() <= part.ordinal()) return;
		var mat = mats.get(part.ordinal());
		var ing = GolemMaterialConfig.get().ingredients.get(mat.id());
		ItemStack repairStack = event.getRight();
		if (ing == null || !ing.test(repairStack)) return;
		int maxFix = Math.min(repairStack.getCount(), (int) Math.ceil((max - health) / max * 4));
		event.setMaterialCost(maxFix);
		event.setCost(maxFix);
		ItemStack result = stack.copy();
		GolemHolder.setHealth(result, Math.min(max, health + max / 4 * maxFix));
		event.setOutput(result);
	}

	private static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	void appendUpgrade(AnvilUpdateEvent event, GolemHolder<T, P> holder, UpgradeItem upgrade) {
		ItemStack stack = event.getLeft();
		var mats = GolemHolder.getMaterial(stack);
		var upgrades = GolemHolder.getUpgrades(stack);
		int remaining = holder.getRemaining(mats, upgrades);
		if (remaining <= 0) return;
		var map = GolemMaterial.collectModifiers(GolemHolder.getMaterial(stack), upgrades);
		if (map.getOrDefault(upgrade.get(), 0) >= upgrade.get().maxLevel) return;
		ItemStack result = stack.copy();
		GolemHolder.addUpgrade(result, upgrade);
		event.setOutput(result);
		event.setCost(4 << upgrades.size());
		event.setMaterialCost(1);
	}

}
