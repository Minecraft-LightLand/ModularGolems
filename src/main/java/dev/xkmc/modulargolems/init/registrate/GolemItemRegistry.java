package dev.xkmc.modulargolems.init.registrate;

import dev.xkmc.l2library.repack.registrate.util.entry.ItemEntry;
import dev.xkmc.modulargolems.content.entity.humanoid.HumaniodGolemPartType;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.item.GolemHolder;
import dev.xkmc.modulargolems.content.item.GolemPart;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.generators.ModelFile;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class GolemItemRegistry {

	public static final Tab GOLEMS = new Tab("golems");

	static {
		REGISTRATE.creativeModeTab(() -> GOLEMS);
	}

	public static final ItemEntry<Item> GOLEM_TEMPLATE = REGISTRATE.item("metal_golem_template", Item::new)
			.defaultModel().defaultLang().register();

	public static final ItemEntry<GolemPart<MetalGolemEntity, MetalGolemPartType>> GOLEM_BODY, GOLEM_ARM, GOLEM_LEGS;
	public static final ItemEntry<GolemHolder<MetalGolemEntity, MetalGolemPartType>> HOLDER_GOLEM;

	public static final ItemEntry<GolemPart<HumanoidGolemEntity, HumaniodGolemPartType>> HUMANOID_BODY, HUMANOID_ARMS, HUMANOID_LEGS;
	public static final ItemEntry<GolemHolder<HumanoidGolemEntity, HumaniodGolemPartType>> HOLDER_HUMANOID;

	static {
		// metal golem
		{
			GOLEM_BODY = REGISTRATE.item("metal_golem_body", p ->
							new GolemPart<>(p, GolemTypeRegistry.TYPE_GOLEM, MetalGolemPartType.BODY, 9))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
			GOLEM_ARM = REGISTRATE.item("metal_golem_arm", p ->
							new GolemPart<>(p, GolemTypeRegistry.TYPE_GOLEM, MetalGolemPartType.LEFT, 9))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
			GOLEM_LEGS = REGISTRATE.item("metal_golem_legs", p ->
							new GolemPart<>(p, GolemTypeRegistry.TYPE_GOLEM, MetalGolemPartType.LEG, 9))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
			HOLDER_GOLEM = REGISTRATE.item("metal_golem_holder", p -> new GolemHolder<>(p, GolemTypeRegistry.TYPE_GOLEM))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
		}

		// humanoid golem
		{
			HUMANOID_BODY = REGISTRATE.item("humanoid_golem_body", p ->
							new GolemPart<>(p, GolemTypeRegistry.TYPE_HUMANOID, HumaniodGolemPartType.BODY, 6))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
			HUMANOID_ARMS = REGISTRATE.item("humanoid_golem_arms", p ->
							new GolemPart<>(p, GolemTypeRegistry.TYPE_HUMANOID, HumaniodGolemPartType.ARMS, 6))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
			HUMANOID_LEGS = REGISTRATE.item("humanoid_golem_legs", p ->
							new GolemPart<>(p, GolemTypeRegistry.TYPE_HUMANOID, HumaniodGolemPartType.LEGS, 6))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
			HOLDER_HUMANOID = REGISTRATE.item("humanoid_golem_holder", p -> new GolemHolder<>(p, GolemTypeRegistry.TYPE_HUMANOID))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
		}
	}

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
