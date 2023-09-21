package dev.xkmc.modulargolems.content.menu.equipment;

import dev.xkmc.l2library.base.menu.base.BaseContainerMenu;
import dev.xkmc.l2library.base.menu.base.PredSlot;
import dev.xkmc.l2library.base.menu.base.SpriteManager;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemSpearItem;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.events.event.GolemEquipEvent;
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

public class EquipmentsMenu extends BaseContainerMenu<EquipmentsMenu> {

	public static EquipmentsMenu fromNetwork(MenuType<EquipmentsMenu> type, int wid, Inventory plInv, FriendlyByteBuf buf) {
		assert Proxy.getClientWorld() != null;
		Entity entity = Proxy.getClientWorld().getEntity(buf.readInt());
		return new EquipmentsMenu(type, wid, plInv, entity instanceof AbstractGolemEntity<?, ?> golem ? golem : null);
	}

	public static EquipmentSlot[] SLOTS = {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

	public static final SpriteManager MANAGER = new SpriteManager(ModularGolems.MODID, "equipments");

	public final AbstractGolemEntity<?, ?> golem;

	protected EquipmentsMenu(MenuType<?> type, int wid, Inventory plInv, @Nullable AbstractGolemEntity<?, ?> golem) {
		super(type, wid, plInv, MANAGER, EquipmentsContainer::new, false);
		this.golem = golem;
		addSlot("hand", (i, e) -> isValid(SLOTS[i], e));
		addSlot("armor", (i, e) -> isValid(SLOTS[i + 2], e));
	}

	private boolean isValid(EquipmentSlot slot, ItemStack stack) {
		return getSlotForItem(stack) == slot;
	}

	@Override
	public boolean stillValid(Player player) {
		return golem != null && !golem.isRemoved();
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
				EquipmentSlot es = getSlotForItem(stack);
				for (int i = 0; i < 6; i++) {
					if (SLOTS[i] == es) {
						this.moveItemStackTo(stack, 36 + i, 37 + i, false);
					}
				}
			}
			this.container.setChanged();
		}
		return ItemStack.EMPTY;
	}

	@Nullable
	public EquipmentSlot getSlotForItem(ItemStack stack) {
		if (!stillValid(inventory.player) || golem == null) {
			return null;
		}
		if (!stack.getItem().canFitInsideContainerItems()) return null;
		if (stack.getItem() instanceof GolemHolder) return null;
		if (golem instanceof HumanoidGolemEntity humanoidGolem) {
			GolemEquipEvent event = new GolemEquipEvent(humanoidGolem, stack);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.canEquip()) {
				return event.getSlot();
			} else {
				return null;
			}
		}
		if (golem instanceof MetalGolemEntity) {
			if (stack.getItem() instanceof MetalGolemArmorItem mgai) {
				return mgai.getSlot();
			} else if (stack.getItem() instanceof MetalGolemSpearItem) {
				return EquipmentSlot.MAINHAND;
			}
		}
		return null;
	}

}
