package dev.xkmc.modulargolems.content.menu;

import dev.xkmc.l2library.base.menu.BaseContainerMenu;
import dev.xkmc.l2library.base.menu.PredSlot;
import dev.xkmc.l2library.base.menu.SpriteManager;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
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
		Entity entity = Proxy.getClientWorld().getEntity(buf.readInt());
		return new EquipmentsMenu(type, wid, plInv, entity instanceof HumanoidGolemEntity golem ? golem : null);
	}

	public static EquipmentSlot[] SLOTS = {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

	public static final SpriteManager MANAGER = new SpriteManager(ModularGolems.MODID, "equipments");

	@Nullable
	protected final HumanoidGolemEntity golem;

	protected EquipmentsMenu(MenuType<?> type, int wid, Inventory plInv, @Nullable HumanoidGolemEntity golem) {
		super(type, wid, plInv, MANAGER, EquipmentsContainer::new, false);
		this.golem = golem;
		addSlot("hand", (i, e) -> isValid(SLOTS[i], e), (i, e) -> {
		});
		addSlot("armor", (i, e) -> isValid(SLOTS[i + 2], e), (i, e) -> {
		});//TODO remove in 1.19.4
	}

	private boolean isValid(EquipmentSlot slot, ItemStack stack) {
		if (golem == null || !stillValid(inventory.player)) {
			return false;
		}
		if (!stack.getItem().canFitInsideContainerItems()) return false;
		if (stack.getItem() instanceof GolemHolder) return false;
		GolemEquipEvent event = new GolemEquipEvent(golem, stack);
		MinecraftForge.EVENT_BUS.post(event);
		return event.canEquip() && event.getSlot() == slot;
	}

	@Override
	public boolean stillValid(Player player) {
		return golem != null && !golem.isRemoved();
	}

	@Override
	protected PredSlot getAsPredSlot(String name, int i, int j) {
		return super.getAsPredSlot(name, i, j);
	}

	@Override
	public ItemStack quickMoveStack(Player pl, int id) {
		if (golem != null) {
			ItemStack stack = this.slots.get(id).getItem();
			if (id >= 36) {
				this.moveItemStackTo(stack, 0, 36, true);
			} else {
				GolemEquipEvent event = new GolemEquipEvent(golem, stack);
				MinecraftForge.EVENT_BUS.post(event);
				if (event.canEquip()) {
					for (int i = 0; i < 6; i++) {
						if (SLOTS[i] == event.getSlot()) {
							this.moveItemStackTo(stack, 36 + i, 37 + i, false);
						}
					}
				}
			}
			this.container.setChanged();
		}
		return ItemStack.EMPTY;
	}
}
