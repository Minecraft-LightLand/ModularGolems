package dev.xkmc.modulargolems.init.registrate;

import dev.xkmc.l2library.repack.registrate.util.entry.ItemEntry;
import dev.xkmc.modulargolems.content.item.GolemHolder;
import dev.xkmc.modulargolems.content.item.GolemPart;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class GolemItemRegistry {

	public static final Tab GOLEMS = new Tab("golems");

	static {
		REGISTRATE.creativeModeTab(() -> GOLEMS);
	}

	public static final ItemEntry<Item> GOLEM_TEMPLATE = REGISTRATE.item("golem_template", Item::new)
			.model((ctx, pvd) -> pvd.withExistingParent(ctx.getName(), "item/conduit")).defaultLang().register();

	public static final ItemEntry<GolemPart> GOLEM_BODY = REGISTRATE.item("golem_body", p ->
					new GolemPart(p, GolemTypeRegistry.TYPE_GOLEM::get, 9))
			.model((ctx, pvd) -> pvd.withExistingParent(ctx.getName(), "item/iron_block")).defaultLang().register();

	public static final ItemEntry<GolemPart> GOLEM_LEFT_HAND = REGISTRATE.item("golem_left_hand", p ->
					new GolemPart(p, GolemTypeRegistry.TYPE_GOLEM::get, 9))
			.model((ctx, pvd) -> pvd.withExistingParent(ctx.getName(), "item/iron_block")).defaultLang().register();

	public static final ItemEntry<GolemPart> GOLEM_RIGHT_HAND = REGISTRATE.item("golem_right_hand", p ->
					new GolemPart(p, GolemTypeRegistry.TYPE_GOLEM::get, 9))
			.model((ctx, pvd) -> pvd.withExistingParent(ctx.getName(), "item/iron_block")).defaultLang().register();

	public static final ItemEntry<GolemPart> GOLEM_LEGS = REGISTRATE.item("golem_legs", p ->
					new GolemPart(p, GolemTypeRegistry.TYPE_GOLEM::get, 9))
			.model((ctx, pvd) -> pvd.withExistingParent(ctx.getName(), "item/iron_block")).defaultLang().register();

	public static final ItemEntry<GolemHolder> HOLDER_GOLEM = REGISTRATE.item("golem-holder", p ->
					new GolemHolder(p, GolemTypeRegistry.TYPE_GOLEM::get))
			.model((ctx, pvd) -> pvd.withExistingParent(ctx.getName(), "item/iron_block")).defaultLang().register();

	public static void register() {
	}

	public static class Tab extends CreativeModeTab {

		public Tab(String label) {
			super(ModularGolems.MODID + "." + label);
		}

		@Override
		public ItemStack makeIcon() {
			return HOLDER_GOLEM.asStack();
		}
	}

}
