package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.l2core.base.menu.base.BaseContainerMenu;
import dev.xkmc.l2core.base.menu.base.PredSlot;
import dev.xkmc.l2core.base.menu.base.SpriteManager;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.events.event.GolemEquipEvent;
import dev.xkmc.modulargolems.events.event.GolemEquipItemEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;
import java.util.Set;

public class EquipmentsMenu extends BaseContainerMenu<EquipmentsMenu> {

	public static EquipmentsMenu fromNetwork(MenuType<EquipmentsMenu> type, int wid, Inventory plInv, RegistryFriendlyByteBuf buf) {
		Entity entity = plInv.player.level().getEntity(buf.readInt());
		return new EquipmentsMenu(type, wid, plInv, entity instanceof AbstractGolemEntity<?, ?> golem ? golem : null);
	}

	public static EquipmentSlot[] LARGE_SLOTS = {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	public static EquipmentSlot[] DOG_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.BODY};

	public static final SpriteManager MANAGER = new SpriteManager(ModularGolems.MODID, "equipments");

	public final AbstractGolemEntity<?, ?> golem;
	protected final EquipmentSlot[] equipmentSlots;

	protected EquipmentsMenu(MenuType<?> type, int wid, Inventory plInv, @Nullable AbstractGolemEntity<?, ?> golem) {
		super(type, wid, plInv, MANAGER, EquipmentsContainer::new, false);
		this.golem = golem;
		equipmentSlots = golem instanceof DogGolemEntity ? DOG_SLOTS : LARGE_SLOTS;
		if (golem instanceof DogGolemEntity) {
			addSlot("head", e -> isValid(EquipmentSlot.HEAD, e));
			addSlot("chest", e -> isValid(EquipmentSlot.BODY, e));
		} else {
			addSlot("main", e -> isValid(EquipmentSlot.MAINHAND, e));
			addSlot("off", e -> isValid(EquipmentSlot.OFFHAND, e));
			addSlot("head", e -> isValid(EquipmentSlot.HEAD, e));
			addSlot("chest", e -> isValid(EquipmentSlot.CHEST, e));
			addSlot("legs", e -> isValid(EquipmentSlot.LEGS, e));
			addSlot("feet", e -> isValid(EquipmentSlot.FEET, e));
			if (golem instanceof HumanoidGolemEntity) {
				addSlot("backup", e -> isValid(EquipmentSlot.MAINHAND, e) || isValid(EquipmentSlot.OFFHAND, e));
				addSlot("arrow", ItemStack::isStackable);
			}
		}
	}

	private boolean isValid(EquipmentSlot slot, ItemStack stack) {
		if (golem instanceof HumanoidGolemEntity)
			if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)
				return true;
		var valids = getSlotForItem(stack);
		if (golem instanceof MetalGolemEntity && valids.contains(EquipmentSlot.MAINHAND)) {
			if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)
				return true;
		}
		return valids.contains(slot);
	}

	@Override
	public boolean stillValid(Player player) {
		if (golem == null) return false;
		golem.inventoryTick = 5;
		return golem.isAlive() && !golem.isRemoved();
	}

	@Override
	public PredSlot getAsPredSlot(String name, int i, int j) {
		return super.getAsPredSlot(name, i, j);
	}

	@Override
	public ItemStack quickMoveStack(Player pl, int id) {
		if (golem != null) {
			ItemStack stack = slots.get(id).getItem();
			if (id >= 36) {
				this.moveItemStackTo(stack, 0, 36, true);
			} else {
				var es = getSlotForItem(stack);
				for (int i = 0; i < 6; i++) {
					if (es.contains(equipmentSlots[i])) {
						this.moveItemStackTo(stack, 36 + i, 37 + i, false);
						break;
					}
				}
			}
			this.container.setChanged();
		}
		return ItemStack.EMPTY;
	}


	public Set<EquipmentSlot> getSlotForItem(ItemStack stack) {
		if (!stillValid(inventory.player) || golem == null) {
			return Set.of();
		}
		if (!stack.getItem().canFitInsideContainerItems()) return Set.of();
		if (stack.getItem() instanceof GolemHolder) return Set.of();
		if (golem instanceof HumanoidGolemEntity humanoidGolem) {
			GolemEquipEvent event = new GolemEquipEvent(humanoidGolem, stack);
			NeoForge.EVENT_BUS.post(event);
			if (event.canEquip()) {
				return Set.of(event.getSlot());
			}
		}
		GolemEquipItemEvent event = new GolemEquipItemEvent(golem, stack);
		NeoForge.EVENT_BUS.post(event);
		if (event.canEquip()) {
			return Set.of(event.getSlot());
		}
		return Set.of();
	}

}
