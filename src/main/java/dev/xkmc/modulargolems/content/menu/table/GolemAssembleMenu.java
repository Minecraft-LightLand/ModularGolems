package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2library.base.menu.base.BaseContainerMenu;
import dev.xkmc.l2library.base.menu.base.PredSlot;
import dev.xkmc.l2library.base.menu.base.SpriteManager;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.GolemSlot;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GolemAssembleMenu extends BaseContainerMenu<GolemAssembleMenu> implements ITableMenu {

	public static GolemAssembleMenu fromNetwork(MenuType<GolemAssembleMenu> type, int wid, Inventory plInv, FriendlyByteBuf buf) {
		return new GolemAssembleMenu(type, wid, plInv);
	}

	public static final SpriteManager MANAGER = new SpriteManager(ModularGolems.MODID, "assemble");

	public static final int MAX_BATCH = 64;

	protected MainSlot main;
	protected final PartMatSlot[] partMatSlots = new PartMatSlot[GolemSlot.values().length];
	protected ResultSlot result;

	public final List<GolemType<?, ?>> typeList = new ArrayList<>();

	private final int[] data = new int[2];
	public final DataSlot selectedType, batchMode;

	private boolean changing = false;

	public GolemAssembleMenu(MenuType<?> type, int wid, Inventory plInv) {
		super(type, wid, plInv, MANAGER, e -> new BaseContainer<>(6, e), true);
		for (var t : GolemTypes.TYPES.get())
			typeList.add(t);
		data[0] = -1;
		data[1] = 0;
		selectedType = addDataSlot(DataSlot.shared(data, 0));
		batchMode = addDataSlot(DataSlot.shared(data, 1));
		sprite.get().getSlot("golem", (x, y) -> new MainSlot(container, added++, x, y), this::addSlot);
		for (var e : GolemSlot.values())
			addPartMatSlot(e);
		sprite.get().getSlot("result", ResultSlot::new, this::addSlot);
		added++;
	}

	private void addPartMatSlot(GolemSlot slot) {
		sprite.get().getSlot(slot.slotName(), (x, y) -> new PartMatSlot(slot, container, added++, x, y), this::addSlot);
	}

	@Override
	public Slot getMainSlot() {
		return main;
	}

	@Nullable
	public GolemType<?, ?> getSelectedType() {
		int index = selectedType.get();
		if (index < 0 || index >= typeList.size()) return null;
		return typeList.get(index);
	}

	@Override
	public void slotsChanged(Container cont) {
		if (!changing) {
			main.update();
			result.update();
		}
		super.slotsChanged(cont);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (id >= 0 && id < typeList.size()) {
			if (!player.level().isClientSide()) {
				selectedType.set(selectedType.get() == id ? -1 : id);
				if (selectedType.get() >= 0)
					returnExtraItems();
				result.update();
			} else {
				result.output = ItemStack.EMPTY;
			}
			return true;
		}
		if (id == typeList.size()) {
			if (!player.level().isClientSide()) {
				batchMode.set(batchMode.get() == 1 ? 0 : 1);
			}
			return true;
		}
		return super.clickMenuButton(player, id);
	}

	private static boolean isArmPart(IGolemPart<?> part) {
		return part instanceof MetalGolemPartType mt && mt != MetalGolemPartType.BODY && mt != MetalGolemPartType.LEG;
	}

	private void returnExtraItems() {
		var type = getSelectedType();
		if (type == null) return;
		for (var e : partMatSlots) {
			if (e == null || e.getItem().isEmpty()) continue;
			if (e.mayPlace(e.getItem())) continue;
			var stack = e.getItem();
			e.set(ItemStack.EMPTY);
			returnToPlayer(stack);
		}
	}

	private void returnToPlayer(ItemStack stack) {
		var player = inventory.player;
		if (player instanceof ServerPlayer sp && player.isAlive() && !sp.hasDisconnected()) {
			inventory.placeItemBackInInventory(stack);
		} else {
			player.drop(stack, false);
		}
	}

	@Override
	public ItemStack quickMoveStack(Player pl, int id) {
		var slot = this.slots.get(id);
		ItemStack stack = slot.getItem();
		if (slot instanceof ResultSlot result) {
			if (batchMode.get() == 1) {
				int count = 0;
				while (count++ < MAX_BATCH) {
					result.update();
					var cur = result.getItem();
					if (cur.isEmpty()) break;
					if (!moveItemStackTo(cur, 0, 36, true)) break;
					result.onTake(pl, cur);
				}
			} else {
				if (moveItemStackTo(stack, 0, 36, true)) {
					result.onTake(pl, stack);
				}
			}
			return ItemStack.EMPTY;
		}
		return super.quickMoveStack(pl, id);
	}

	public class MainSlot extends PredSlot {

		public MainSlot(Container container, int index, int x, int y) {
			super(container, index, x, y, e -> e.is(GolemItems.GOLEM_TEMPLATE.get()));
			main = this;
		}

		public void update() {
		}

	}

	public class PartMatSlot extends PredSlot {

		public final GolemSlot slot;

		private final Set<GolemType<?, ?>> types = new HashSet<>();

		public PartMatSlot(GolemSlot slot, Container container, int index, int x, int y) {
			super(container, index, x, y, e -> true);
			this.slot = slot;
			partMatSlots[slot.ordinal()] = this;
			for (var type : typeList) {
				for (var part : type.values()) {
					if (part.getSlot() == slot)
						types.add(type);
				}
			}
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			var type = getSelectedType();
			if (type == null || !types.contains(type)) return false;
			if (stack.getItem() instanceof GolemPart<?, ?> gp) {
				if (GolemPart.getMaterial(stack).isEmpty())
					return false;
				var part = gp.getPart();
				boolean belongs = false;
				for (var p : type.values())
					if (p == part) {
						belongs = true;
						break;
					}
				if (!belongs) return false;
				if (part instanceof MetalGolemPartType mt)
					return mt == MetalGolemPartType.BODY ? slot == GolemSlot.MIDDLE
							: mt == MetalGolemPartType.LEG ? slot == GolemSlot.DOWN
							: slot == GolemSlot.LEFT || slot == GolemSlot.RIGHT;
				return part.getSlot() == slot;
			}
			if (stack.is(MGTagGen.SPECIAL_CRAFT))
				return false;
			return GolemMaterial.getMaterial(stack).isPresent();
		}

		@Override
		public boolean isActive() {
			if (!getItem().isEmpty()) return true;
			var type = getSelectedType();
			return type != null && types.contains(type);
		}

		public boolean isCore() {
			var type = getSelectedType();
			if (type == null) return false;
			return type.getBodyPart().getSlot() == slot;
		}

	}

	public class ResultSlot extends PredSlot {

		protected ItemStack output = ItemStack.EMPTY;
		protected @Nullable GolemType<?, ?> currentType;
		protected @Nullable IGolemPart<?> currentPart;
		protected @Nullable Component error;
		protected int templateCount = 0;

		public ResultSlot(int x, int y) {
			super(new SimpleContainer(1), 0, x, y, e -> false);
			result = this;
		}

		@Override
		public void onTake(Player player, ItemStack stack) {
			update();
			if (output.isEmpty()) {
				set(ItemStack.EMPTY);
				return;
			}
			var input = main.getItem();
			if (currentType != null) {
				changing = true;
				if (currentPart != null) {
					var s = partMatSlots[currentPart.getSlot().ordinal()];
					if (s != null && !s.getItem().isEmpty())
						s.getItem().shrink(currentPart.toItem().count);
				} else {
					for (var part : currentType.values()) {
						var s = partMatSlots[part.getSlot().ordinal()];
						if (s != null && !s.getItem().isEmpty()) {
							if (s.getItem().getItem() instanceof GolemPart<?, ?>) {
								s.getItem().shrink(1);
							} else {
								s.getItem().shrink(part.toItem().count);
							}
						}
					}
				}
				if (templateCount > 0)
					input.shrink(templateCount);
				changing = false;
			}
			super.onTake(player, stack);
			main.update();
			result.update();
		}

		@Override
		public boolean isActive() {
			return !getItem().isEmpty() || !output.isEmpty();
		}

		public void update() {
			output = ItemStack.EMPTY;
			currentType = null;
			currentPart = null;
			error = null;
			templateCount = 0;
			var input = main.getItem();
			boolean hasTemplate = !input.isEmpty() && input.is(GolemItems.GOLEM_TEMPLATE.get());
			var filled = new HashSet<GolemSlot>();
			for (var e : partMatSlots) {
				if (e != null && !e.getItem().isEmpty())
					filled.add(e.slot);
			}
			if (filled.isEmpty()) {
				set(ItemStack.EMPTY);
				return;
			}
			var selected = getSelectedType();
			List<GolemType<?, ?>> candidates;
			if (selected != null) {
				candidates = new ArrayList<>();
				candidates.add(selected);
			} else {
				candidates = typeList;
			}
for (var type : candidates) {
				if (filled.size() == 1) {
					var slot = filled.iterator().next();
					IGolemPart<?> targetPart = null;
					for (var part : type.values())
						if (part.getSlot() == slot) {
							targetPart = part;
							break;
						}
					if (targetPart == null) continue;
					var stack = partMatSlots[slot.ordinal()].getItem();
					if (stack.isEmpty()) continue;
					ResourceLocation mat = null;
					if (stack.getItem() instanceof GolemPart<?, ?>) {
						continue;
					} else {
						var opt = GolemMaterial.getMaterial(stack);
						if (opt.isPresent() && !stack.is(MGTagGen.SPECIAL_CRAFT)
								&& GolemMaterialConfig.mayApply(targetPart.toItem(), opt.get())) {
							mat = opt.get();
						}
					}
					if (mat == null) continue;
					boolean valid = true;
					int needed = targetPart.toItem().count;
					if (stack.getCount() < needed) {
						error = MGLangData.UI_SUB_MAT.get();
						valid = false;
					}
					if (!hasTemplate || input.getCount() < 1) {
						error = MGLangData.UI_SUB_TEMPLATE.get();
						valid = false;
					}
					var ans = new ItemStack(targetPart.toItem());
					GolemPart.setMaterial(ans, mat);
					currentType = type;
					currentPart = targetPart;
					templateCount = 1;
					output = ans;
					set(valid ? ans : ItemStack.EMPTY);
					return;
				}
				var typeSlots = new HashSet<GolemSlot>();
				var parts = type.values();
				for (var part : parts)
					typeSlots.add(part.getSlot());
				if (!typeSlots.equals(filled)) continue;
				var ans = new ItemStack(GolemType.getGolemHolder(type));
				boolean valid = true;
				int ingotCount = 0;
				for (var part : parts) {
					var stack = partMatSlots[part.getSlot().ordinal()].getItem();
					ResourceLocation mat = null;
					if (stack.getItem() instanceof GolemPart<?, ?> gp) {
						if (gp.getPart() == part || (isArmPart(part) && isArmPart(gp.getPart()))) {
							var opt = GolemPart.getMaterial(stack);
							if (opt.isPresent() && GolemMaterialConfig.mayApply(part.toItem(), opt.get()))
								mat = opt.get();
						}
					} else {
						var opt = GolemMaterial.getMaterial(stack);
						if (opt.isPresent() && !stack.is(MGTagGen.SPECIAL_CRAFT)
								&& GolemMaterialConfig.mayApply(part.toItem(), opt.get())) {
							mat = opt.get();
							ingotCount++;
							if (stack.getCount() < part.toItem().count) {
								error = MGLangData.UI_SUB_MAT.get();
								valid = false;
							}
						}
					}
					if (mat == null)
						valid = false;
					GolemHolder.addMaterial(ans, part.toItem(), mat == null ? type.defaultMaterial() : mat);
				}
				if (ingotCount > 0 && (!hasTemplate || input.getCount() < ingotCount))
					error = MGLangData.UI_SUB_TEMPLATE.get();
				currentType = type;
				templateCount = ingotCount;
				output = ans;
				set(valid && error == null ? ans : ItemStack.EMPTY);
				return;
			}
			set(ItemStack.EMPTY);
		}

	}

}