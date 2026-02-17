package dev.xkmc.modulargolems.content.menu.table;

import dev.xkmc.l2library.base.menu.base.BaseContainerMenu;
import dev.xkmc.l2library.base.menu.base.PredSlot;
import dev.xkmc.l2library.base.menu.base.SpriteManager;
import dev.xkmc.modulargolems.content.capability.TargetFilterEditor;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemPartType;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemPartType;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.recipe.GolemReplaceRecipe;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.util.GolemUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.awt.SystemColor.menu;

public class GolemDisintegrateMenu extends BaseContainerMenu<GolemDisintegrateMenu> {

	private record GolemSlots<P extends IGolemPart<P>>(String name, IGolemPart<P> part) {

	}

	public static GolemDisintegrateMenu fromNetwork(MenuType<GolemDisintegrateMenu> type, int wid, Inventory plInv, FriendlyByteBuf buf) {
		return new GolemDisintegrateMenu(type, wid, plInv);
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
	protected ResultSlot result;
	protected final List<PartSlot> partSlots = new ArrayList<>();

	private boolean changing = false;

	public GolemDisintegrateMenu(MenuType<?> type, int wid, Inventory plInv) {
		super(type, wid, plInv, MANAGER, e -> new BaseContainer<>(6, e), true);
		sprite.get().getSlot("golem", (x, y) -> new MainSlot(container, added++, x, y), this::addSlot);
		addPartSlot(UP);
		addPartSlot(LEFT);
		addPartSlot(MIDDLE);
		addPartSlot(RIGHT);
		addPartSlot(DOWN);
		sprite.get().getSlot("result", ResultSlot::new, this::addSlot);
		added++;
	}

	private void addPartSlot(String slot) {
		sprite.get().getSlot(slot, (x, y) -> new PartSlot(slot, container, added++, x, y), this::addSlot);
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
			if (!inventory.player.level().isClientSide())
				result.update();
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
					if (health < max || reforge > 0)
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
					if (player.isAlive() && !sp.hasDisconnected()) {
						inventory.placeItemBackInInventory(e);
					} else {
						player.drop(e, false);
					}
				}
				main.set(ItemStack.EMPTY);
				return true;
			}
			return false;
		}
		return super.clickMenuButton(player, id);
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
			dropList = GolemUtils.collectFromGolem(inventory.player.level(), getItem());
		}
	}

	public class ResultSlot extends PredSlot {

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
			changing = false;
			main.set(ItemStack.EMPTY);
			super.onTake(player, stack);
		}

		@Override
		public boolean isActive() {
			return !getItem().isEmpty();
		}

		public void update() {
			var input = main.getItem();
			if (input.getItem() instanceof GolemHolder<?, ?> holder) {
				var list = GolemHolder.getMaterial(input);
				boolean success = false;
				var ans = input.copy();
				if (list.size() == holder.getEntityType().values().length) {
					for (var slot : partSlots) {
						var item = slot.getItem();
						var part = slot.of(holder);
						if (part == null) continue;
						if (!(item.getItem() instanceof GolemPart<?, ?>)) continue;
						var opt = GolemPart.getMaterial(item);
						if (opt.isEmpty()) continue;
						GolemHolder.setMaterial(ans, part.ordinal(), opt.get());
						success = true;
					}
				}
				if (success) {
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
		}

	}


}
