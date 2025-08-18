package dev.xkmc.modulargolems.compat.materials.create.automation;

import com.simibubi.create.content.kinetics.deployer.DeployerRecipeSearchEvent;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.card.ConfigCard;
import dev.xkmc.modulargolems.content.item.equipments.GolemEquipmentItem;
import dev.xkmc.modulargolems.content.item.golem.GolemEquipUtil;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.upgrade.UpgradeItem;
import dev.xkmc.modulargolems.events.CraftEventListeners;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.Curios;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.LinkedHashSet;
import java.util.Optional;

public class CreateRecipeEvents {

	@SubscribeEvent
	public static void addRecipe(DeployerRecipeSearchEvent event) {
		ItemStack first = event.getInventory().getItem(0);
		ItemStack second = event.getInventory().getItem(1);
		if (!(first.getItem() instanceof GolemHolder<?, ?> holder)) return;
		ItemStack result;
		Level level = event.getBlockEntity().getLevel();
		if (!(level instanceof ServerLevel sl)) return;
		if (second.getItem() instanceof ConfigCard card) {
			var id = ConfigCard.getUUID(second);
			if (id == null) return;
			result = first.copy();
			GolemHolder.setGolemConfig(result, id, card.getColor());
		} else if (second.getItem() instanceof UpgradeItem upgrade) {
			result = CraftEventListeners.appendUpgrade(first, holder, upgrade);
		} else if (GolemEquipUtil.isGolemCurio(holder, second)) {
			result = GolemEquipUtil.equipCurio(holder, first, second, sl);
		} else if (holder.getEntityType() == GolemTypes.TYPE_GOLEM.get()) {
			if (!(second.getItem() instanceof GolemEquipmentItem equipment)) return;
			if (!equipment.isFor(GolemTypes.ENTITY_GOLEM.get())) return;
			EquipmentSlot slot = equipment.getSlot();
			result = GolemEquipUtil.equip(holder, first, second, slot, sl);
		} else if (holder.getEntityType() == GolemTypes.TYPE_HUMANOID.get()) {
			EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(second);
			result = GolemEquipUtil.equip(holder, first, second, slot, sl);
		} else return;
		if (result.isEmpty()) return;
		event.addRecipe(() -> Optional.of(new DeployerUpgradeRecipe(result)), 1000);
	}


}
