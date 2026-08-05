package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.l2core.base.menu.base.BaseContainerMenu;
import dev.xkmc.l2core.base.menu.base.PredSlot;
import dev.xkmc.l2core.base.menu.base.SpriteManager;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.GolemMenuControl;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class EquipmentsMenu extends BaseContainerMenu<EquipmentsMenu> {

	public static EquipmentsMenu fromNetwork(MenuType<EquipmentsMenu> type, int wid, Inventory plInv, RegistryFriendlyByteBuf buf) {
		Entity entity = plInv.player.level().getEntity(buf.readInt());
		return new EquipmentsMenu(type, wid, plInv, entity instanceof AbstractGolemEntity<?, ?> golem ? golem : null);
	}

	public static EquipmentSlot[] SLOTS = {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	public static EquipmentSlot[] DOG_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.BODY};

	public static final SpriteManager MANAGER = new SpriteManager(ModularGolems.MODID, "equipments");

	@Nullable
	public final AbstractGolemEntity<?, ?> golem;
	@Nullable
	public final GolemMenuControl<?> ctrl;
	protected final EquipmentSlot[] equipmentSlots;

	protected EquipmentsMenu(MenuType<?> type, int wid, Inventory plInv, @Nullable AbstractGolemEntity<?, ?> golem) {
		super(type, wid, plInv, MANAGER, EquipmentsContainer::new, false);
		this.golem = golem;
		if (golem == null) {
			equipmentSlots = SLOTS;
			ctrl = null;
			return;
		}
		ctrl = GolemType.getGolemType(golem.getType()).menuControl(this, Wrappers.cast(golem));
		equipmentSlots = ctrl.getSlotDefinition();
		ctrl.fillMenu();
	}

	@Override
	public boolean stillValid(Player player) {
		if (golem == null || !player.isAlive()) return false;
		golem.inventoryTick = 5;
		return !golem.isRemoved() && golem.getGuardedDataImpl() > 0;
	}

	@Override
	public PredSlot getAsPredSlot(String name, int i, int j) {
		return super.getAsPredSlot(name, i, j);
	}

	@Override
	public ItemStack quickMoveStack(Player pl, int id) {
		if (golem != null && ctrl != null) {
			ItemStack stack = this.slots.get(id).getItem();
			if (id >= 36) {
				this.moveItemStackTo(stack, 0, 36, true);
			} else {
				ctrl.handleQuickMove(stack);
				var es = ctrl.getSlotForItem(stack);
				for (int i = 0; i < equipmentSlots.length; i++) {
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

	@Override
	public void addSlot(String name, Predicate<ItemStack> pred) {
		super.addSlot(name, pred);
	}

	@Override
	public void addSlot(String name, BiPredicate<Integer, ItemStack> pred) {
		super.addSlot(name, pred);
	}

	@Override
	public boolean moveItemStackTo(ItemStack p_38904_, int p_38905_, int p_38906_, boolean p_38907_) {
		return super.moveItemStackTo(p_38904_, p_38905_, p_38906_, p_38907_);
	}

}
