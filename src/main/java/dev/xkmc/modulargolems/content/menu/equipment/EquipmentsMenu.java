package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.l2library.base.menu.base.BaseContainerMenu;
import dev.xkmc.l2library.base.menu.base.PredSlot;
import dev.xkmc.l2library.base.menu.base.SpriteManager;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.events.event.GolemEquipEvent;
import dev.xkmc.modulargolems.events.event.GolemEquipItemEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.Set;

public class EquipmentsMenu extends BaseContainerMenu<EquipmentsMenu> {

	public static EquipmentsMenu fromNetwork(MenuType<EquipmentsMenu> type, int wid, Inventory plInv, FriendlyByteBuf buf) {
		assert Proxy.getClientWorld() != null;
		Entity entity = Proxy.getClientWorld().getEntity(buf.readInt());
		return new EquipmentsMenu(type, wid, plInv, entity instanceof AbstractGolemEntity<?, ?> golem ? golem : null);
	}

	public static EquipmentSlot[] SLOTS = {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

	public static final SpriteManager MANAGER = new SpriteManager(ModularGolems.MODID, "equipments");
	public static final SpriteManager EXTRA = new SpriteManager(ModularGolems.MODID, "equipments_extra");

	public final AbstractGolemEntity<?, ?> golem;

	protected EquipmentsMenu(MenuType<?> type, int wid, Inventory plInv, @Nullable AbstractGolemEntity<?, ?> golem) {
		super(type, wid, plInv, golem instanceof HumanoidGolemEntity ? EXTRA : MANAGER, EquipmentsContainer::new, false);
		this.golem = golem;
		addSlot("hand", (i, e) -> isValid(SLOTS[i], e));
		addSlot("armor", (i, e) -> isValid(SLOTS[i + 2], e));
		if (golem instanceof HumanoidGolemEntity) {
			addSlot("backup", e -> isValid(EquipmentSlot.MAINHAND, e) || isValid(EquipmentSlot.OFFHAND, e));
			addSlot("arrow", e -> true);
		}
	}

	private boolean isValid(EquipmentSlot slot, ItemStack stack) {
		if (golem instanceof HumanoidGolemEntity) {
			if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)
				return true;
		}
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
		return golem.isAlive();
	}

	@Override
	public PredSlot getAsPredSlot(String name, int i, int j) {
		return super.getAsPredSlot(name, i, j);
	}

	@Override
	public ItemStack quickMoveStack(Player pl, int id) {
		if (golem != null) {
			ItemStack stack = this.slots.get(id).getItem();
			if (id >= 36) {
				this.moveItemStackTo(stack, 0, 36, true);
			} else {
				var es = getSlotForItem(stack);
				for (int i = 0; i < SLOTS.length; i++) {
					if (es.contains(SLOTS[i])) {
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
			MinecraftForge.EVENT_BUS.post(event);
			if (event.canEquip()) {
				return Set.of(event.getSlot());
			}
		}
		GolemEquipItemEvent event = new GolemEquipItemEvent(golem, stack);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.canEquip()) {
			return Set.of(event.getSlot());
		}
		return Set.of();
	}

}
