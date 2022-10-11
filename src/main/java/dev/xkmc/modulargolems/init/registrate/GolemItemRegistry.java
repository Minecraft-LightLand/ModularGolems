package dev.xkmc.modulargolems.init.registrate;

import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.repack.registrate.builders.ItemBuilder;
import dev.xkmc.l2library.repack.registrate.util.entry.ItemEntry;
import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemPartType;
import dev.xkmc.modulargolems.content.entity.humanoid.HumaniodGolemPartType;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.item.GolemHolder;
import dev.xkmc.modulargolems.content.item.GolemPart;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import dev.xkmc.modulargolems.content.upgrades.UpgradeItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.function.Supplier;

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

	public static final ItemEntry<GolemPart<DogGolemEntity, DogGolemPartType>> DOG_BODY, DOG_LEGS;
	public static final ItemEntry<GolemHolder<DogGolemEntity, DogGolemPartType>> HOLDER_DOG;

	public static final ItemEntry<UpgradeItem> FIRE_IMMUNE, THUNDER_IMMUNE, RECYCLE, DIAMOND, NETHERITE, QUARTZ, GOLD, ENCHANTED_GOLD;

	static {
		// metal golem
		{
			GOLEM_BODY = REGISTRATE.item("metal_golem_body", p ->
							new GolemPart<>(p.fireResistant(), GolemTypeRegistry.TYPE_GOLEM, MetalGolemPartType.BODY, 9))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
			GOLEM_ARM = REGISTRATE.item("metal_golem_arm", p ->
							new GolemPart<>(p.fireResistant(), GolemTypeRegistry.TYPE_GOLEM, MetalGolemPartType.LEFT, 9))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
			GOLEM_LEGS = REGISTRATE.item("metal_golem_legs", p ->
							new GolemPart<>(p.fireResistant(), GolemTypeRegistry.TYPE_GOLEM, MetalGolemPartType.LEG, 9))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
			HOLDER_GOLEM = REGISTRATE.item("metal_golem_holder", p ->
							new GolemHolder<>(p.fireResistant(), GolemTypeRegistry.TYPE_GOLEM))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
		}

		// humanoid golem
		{
			HUMANOID_BODY = REGISTRATE.item("humanoid_golem_body", p ->
							new GolemPart<>(p.fireResistant(), GolemTypeRegistry.TYPE_HUMANOID, HumaniodGolemPartType.BODY, 6))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
			HUMANOID_ARMS = REGISTRATE.item("humanoid_golem_arms", p ->
							new GolemPart<>(p.fireResistant(), GolemTypeRegistry.TYPE_HUMANOID, HumaniodGolemPartType.ARMS, 6))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
			HUMANOID_LEGS = REGISTRATE.item("humanoid_golem_legs", p ->
							new GolemPart<>(p.fireResistant(), GolemTypeRegistry.TYPE_HUMANOID, HumaniodGolemPartType.LEGS, 6))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
			HOLDER_HUMANOID = REGISTRATE.item("humanoid_golem_holder", p ->
							new GolemHolder<>(p.fireResistant(), GolemTypeRegistry.TYPE_HUMANOID))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
		}

		// dog golem
		{
			DOG_BODY = REGISTRATE.item("dog_golem_body", p ->
							new GolemPart<>(p.fireResistant(), GolemTypeRegistry.TYPE_DOG, DogGolemPartType.BODY, 4))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
			DOG_LEGS = REGISTRATE.item("dog_golem_legs", p ->
							new GolemPart<>(p.fireResistant(), GolemTypeRegistry.TYPE_DOG, DogGolemPartType.LEGS, 4))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
			HOLDER_DOG = REGISTRATE.item("dog_golem_holder", p ->
							new GolemHolder<>(p.fireResistant(), GolemTypeRegistry.TYPE_DOG))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.defaultLang().register();
		}

		// upgrades
		{
			FIRE_IMMUNE = regUpgrade("fire_immune", () -> GolemModifierRegistry.FIRE_IMMUNE).lang("Fire Immune Upgrade").register();
			THUNDER_IMMUNE = regUpgrade("thunder_immune", () -> GolemModifierRegistry.THUNDER_IMMUNE).lang("Thunder Immune Upgrade").register();
			RECYCLE = regUpgrade("recycle", () -> GolemModifierRegistry.RECYCLE).lang("Recycle Ugpgrade").register();
			DIAMOND = regUpgrade("diamond", () -> GolemModifierRegistry.ARMOR).lang("Diamond Upgrade").register();
			NETHERITE = regUpgrade("netherite", () -> GolemModifierRegistry.TOUGH).lang("Netherite Upgrade").register();
			QUARTZ = regUpgrade("quartz", () -> GolemModifierRegistry.DAMAGE).lang("Quartz Upgrade").register();
			GOLD = regUpgrade("gold", () -> GolemModifierRegistry.REGEN).lang("Golden Apple Upgrade").register();
			ENCHANTED_GOLD = regUpgrade("enchanted_gold", () -> GolemModifierRegistry.REGEN, 2, true).lang("Enchanted Golden Apple Upgrade").register();
		}
	}

	public static ItemBuilder<UpgradeItem, L2Registrate> regUpgrade(String id, Supplier<RegistryEntry<? extends GolemModifier>> mod) {
		return regUpgrade(id, mod, 1, false);
	}

	public static ItemBuilder<UpgradeItem, L2Registrate> regUpgrade(String id, Supplier<RegistryEntry<? extends GolemModifier>> mod, int level, boolean foil) {
		return REGISTRATE.item(id, p -> new UpgradeItem(p, mod.get()::get, level, foil))
				.model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("item/upgrades/" + id)));
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
