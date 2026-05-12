package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2core.base.menu.base.BaseContainerMenu;
import dev.xkmc.l2core.base.menu.base.PredSlot;
import dev.xkmc.l2core.base.menu.base.SpriteManager;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemPartType;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemPartType;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.item.data.GolemHolderMaterial;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.item.upgrade.AddSlotTemplate;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import dev.xkmc.modulargolems.util.GolemUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GolemDisintegrateMenu extends BaseContainerMenu<GolemDisintegrateMenu> implements ITableMenu {

	private record GolemSlots<P extends IGolemPart<P>>(String name, IGolemPart<P> part) {

	}

	public static GolemDisintegrateMenu fromNetwork(MenuType<GolemDisintegrateMenu> type, int wid, Inventory plInv, FriendlyByteBuf buf) {
		return new GolemDisintegrateMenu(type, wid, plInv);
	}

	public static GolemDisintegrateMenu createFloating(int wid, Inventory plInv, ContainerLevelAccess access) {
		return new GolemDisintegrateMenu(GolemMiscs.DISINTEGRATE.get(), wid, plInv);
	}

	public static final SpriteManager MANAGER = new SpriteManager(ModularGolems.MODID, "disintegrate");

	private static final String UP = "golem_up", LEFT = "golem_left", MIDDLE = "golem_middle", RIGHT = "golem_right", DOWN = "golem_down";

	@SuppressWarnings("unchecked")
	private static final GolemSlots<MetalGolemPartType>[] LARGE = new GolemSlots[]{
			new GolemSlots<>(LEFT, MetalGolemPartType.RIGHT),
			new GolemSlots<>(MIDDLE, MetalGolemPartType.BODY),
			new GolemSlots<>(RIGHT, MetalGolemPartType.LEFT),
			new GolemSlots<>(DOWN, MetalGolemPartType.LEG)
	};

	@SuppressWarnings("unchecked")
	private static final GolemSlots<HumanoidGolemPartType>[] HUMANOID = new GolemSlots[]{
			new GolemSlots<>(UP, HumanoidGolemPartType.BODY),
			new GolemSlots<>(MIDDLE, HumanoidGolemPartType.ARMS),
			new GolemSlots<>(DOWN, HumanoidGolemPartType.LEGS)
	};

	@SuppressWarnings("unchecked")
	private static final GolemSlots<DogGolemPartType>[] DOG = new GolemSlots[]{
			new GolemSlots<>(MIDDLE, DogGolemPartType.BODY),
			new GolemSlots<>(DOWN, DogGolemPartType.LEGS)
	};

	protected MainSlot main;
	protected ExtraMatSlot extra;
	protected ResultSlot result;

	@Nullable
	protected PartSlot body;
	protected final List<PartSlot> partSlots = new ArrayList<>();

	private boolean changing = false;

	public GolemDisintegrateMenu(MenuType<?> type, int wid, Inventory plInv) {
		super(type, wid, plInv, MANAGER, e -> new BaseContainer<>(7, e), true);
		getLayout().getSlot("golem", (x, y) -> new MainSlot(container, added++, x, y), this::addSlot);
		addPartSlot(UP);
		addPartSlot(LEFT);
		addPartSlot(MIDDLE);
		addPartSlot(RIGHT);
		addPartSlot(DOWN);
		getLayout().getSlot("extra_mat", (x, y) -> new ExtraMatSlot(container, added++, x, y), this::addSlot);
		getLayout().getSlot("result", ResultSlot::new, this::addSlot);
		added++;
	}

	private void addPartSlot(String slot) {
		getLayout().getSlot(slot, (x, y) -> new PartSlot(slot, container, added++, x, y), this::addSlot);
	}

	@Override
	public Slot getMainSlot() {
		return main;
	}

	@Override
	public void slotsChanged(Container cont) {
		if (!changing) {
			main.update();
			boolean allEmpty = main.getItem().isEmpty();
			for (var e : partSlots)
				allEmpty &= e.getItem().isEmpty();
			for (var e : partSlots)
				e.updateVisibility(allEmpty);
			result.update();
			extra.update();
		}
		super.slotsChanged(cont);
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (id == 1) {
			var input = main.getItem();
			if (input.getItem() instanceof GolemHolder<?, ?>) {
				boolean mayBreak = !input.isEmpty();
				for (var e : partSlots)
					mayBreak &= e.getItem().isEmpty();
				if (mayBreak) {
					float max = GolemHolder.getMaxHealth(input);
					float health = GolemHolder.getHealth(input);
					int reforge = GolemHolder.getReforge(input);
					if (max > 0 && health < max || reforge > 0)
						mayBreak = false;
				}
				if (!mayBreak) return false;
				if (!(inventory.player instanceof ServerPlayer sp))
					return true;
				changing = true;
				for (var e : partSlots)
					e.set(e.partShadow);
				changing = false;
				for (var e : main.dropList) {
					returnToPlayer(e);
				}
				main.set(ItemStack.EMPTY);
				return true;
			}
			return false;
		}
		return super.clickMenuButton(player, id);
	}

	@Override
	public ItemStack quickMoveStack(Player pl, int id) {
		var slot = this.slots.get(id);
		ItemStack stack = slot.getItem();
		if (slot instanceof ResultSlot resultSlot) {
			if (moveItemStackTo(stack, 0, 36, true)) {
				resultSlot.onTake(pl, stack);
			}
			return ItemStack.EMPTY;
		}
		return super.quickMoveStack(pl, id);
	}

	public class MainSlot extends PredSlot {

		public List<ItemStack> dropList = new ArrayList<>();

		public MainSlot(Container container, int index, int x, int y) {
			super(container, index, x, y, e -> e.getItem() instanceof GolemHolder<?, ?>);
			main = this;
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			for (var e : partSlots)
				if (!e.isValid(stack, e.getItem()))
					return false;
			return super.mayPlace(stack);
		}

		public void update() {
			dropList = GolemUtils.collectFromGolem(inventory.player.level(), getItem(), true);
		}
	}

	public class ExtraMatSlot extends PredSlot {

		@Nullable
		public Ingredient ingot = null;
		public int count = 0;

		public ExtraMatSlot(Container container, int index, int x, int y) {
			super(container, index, x, y, e -> true);
			extra = this;
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return ingot != null && ingot.test(stack);
		}

		@Override
		public boolean isActive() {
			return !getItem().isEmpty() || ingot != null && count > 0;
		}

		public void update() {
		}

	}

	public class ResultSlot extends PredSlot {

		protected int rem;
		protected ItemStack output = ItemStack.EMPTY;
		protected @Nullable Component error;

		public ResultSlot(int x, int y) {
			super(new SimpleContainer(1), 0, x, y, e -> false);
			result = this;
		}

		@Override
		public void onTake(Player player, ItemStack stack) {
			changing = true;
			for (var e : partSlots) {
				if (!e.getItem().isEmpty())
					e.set(e.partShadow);
			}
			if (!extra.getItem().isEmpty()) {
				extra.getItem().shrink(extra.count);
			}
			changing = false;
			main.set(ItemStack.EMPTY);
			super.onTake(player, stack);
		}

		@Override
		public boolean isActive() {
			return !getItem().isEmpty() || !output.isEmpty();
		}

		public void update() {
			rem = 0;
			extra.ingot = null;
			extra.count = 0;
			output = ItemStack.EMPTY;
			error = null;
			var input = main.getItem();
			if (input.getItem() instanceof GolemHolder<?, ?> holder) {
				var list = GolemHolder.getMaterial(input);
				boolean success = false;
				if (list.size() == holder.getEntityType().values().length) {
					for (var slot : partSlots) {
						var item = slot.getItem();
						var part = slot.of(holder);
						if (part == null) continue;
						if (!(item.getItem() instanceof GolemPart<?, ?>)) continue;
						var opt = GolemPart.getMaterial(item);
						if (opt.isEmpty()) continue;
						list.set(part.ordinal(), part.toItem().parseMaterial(opt.get()));
						success = true;
					}
				}
				if (success) {
					var ans = input.copy();
					ans.set(GolemItems.HOLDER_MAT, GolemHolderMaterial.parse(list));
					var mhp = GolemHolder.getMaxHealth(input);
					if (mhp > 0 && GolemHolder.getHealth(input) >= mhp) {
						GolemHolder.setHealth(ans, GolemHolder.getMaxHealth(ans));
					}
					var upgrades = GolemHolder.getUpgrades(ans);
					rem = holder.getRemaining(GolemHolder.getMaterial(ans), upgrades);
					if (rem < 0) {
						error = MGLangData.UI_SUB_SLOT.get();
						output = ans;
						set(ItemStack.EMPTY);
						return;
					}
					if (body != null && !body.getItem().isEmpty()) {
						int count = 0;
						for (var e : upgrades.upgrades()) {
							if (e instanceof AddSlotTemplate) {
								count++;
							}
						}
						var mat = GolemHolder.getCraftingMaterial(ans);
						if (count > 0 && mat != null && !mat.isEmpty()) {
							extra.ingot = mat;
							extra.count = count;
							ItemStack ex = extra.getItem();
							if (!mat.test(ex) || ex.getCount() < count) {
								error = MGLangData.UI_SUB_INGOT.get();
								output = ans;
								set(ItemStack.EMPTY);
								return;
							}
						}
					}
					set(ans);
					return;
				}
			}
			set(ItemStack.EMPTY);
		}

	}

	public class PartSlot extends Slot {

		public final String name;

		@Nullable
		private IGolemPart<?> large, humanoid, dog;

		private boolean active = false;

		public ItemStack partShadow = ItemStack.EMPTY;

		public PartSlot(String name, Container container, int index, int x, int y) {
			super(container, index, x, y);
			this.name = name;
			partSlots.add(this);
			for (var e : LARGE) if (e.name.equals(name)) large = e.part();
			for (var e : HUMANOID) if (e.name.equals(name)) humanoid = e.part();
			for (var e : DOG) if (e.name.equals(name)) dog = e.part();
		}

		@Nullable
		private IGolemPart<?> of(GolemHolder<?, ?> holder) {
			return holder == GolemItems.HOLDER_GOLEM.get() ? large :
					holder == GolemItems.HOLDER_HUMANOID.get() ? humanoid :
					holder == GolemItems.HOLDER_DOG.get() ? dog : null;
		}

		public boolean isValid(ItemStack golem, ItemStack item) {
			if (item.isEmpty()) return true;
			if (!(golem.getItem() instanceof GolemHolder<?, ?> holder)) return false;
			if (!(item.getItem() instanceof GolemPart<?, ?> part)) return false;
			if (GolemPart.getMaterial(item).isEmpty()) return false;
			var type = of(holder);
			return type != null && type.toItem() == part;
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return super.mayPlace(stack) && isValid(main.getItem(), stack);
		}

		@Override
		public boolean isActive() {
			return !getItem().isEmpty() || active;
		}

		public void updateVisibility(boolean allEmpty) {
			partShadow = ItemStack.EMPTY;
			if (allEmpty) {
				active = !getItem().isEmpty();
				return;
			}
			if (!(main.getItem().getItem() instanceof GolemHolder<?, ?> holder))
				return;
			var part = of(holder);
			active = !getItem().isEmpty() || part != null;
			if (part == null) return;
			partShadow = part.toItem().getDefaultInstance();
			var list = GolemHolder.getMaterial(main.getItem());
			if (list.size() > part.ordinal()) {
				partShadow = GolemPart.setMaterial(partShadow, list.get(part.ordinal()).id());
			}
			if (part == holder.getEntityType().getBodyPart()) {
				body = this;
			}
		}

	}

	private void returnToPlayer(ItemStack e) {
		var player = inventory.player;
		if (!(player instanceof ServerPlayer sp)) return;
		if (player.isAlive() && !sp.hasDisconnected()) {
			inventory.placeItemBackInInventory(e);
		} else {
			player.drop(e, false);
		}
	}


}
