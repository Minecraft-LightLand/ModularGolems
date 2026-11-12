package dev.xkmc.modulargolems.compat.materials.alexscaves;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.xkmc.l2complements.init.L2Complements;
import dev.xkmc.l2complements.init.data.TagGen;
import dev.xkmc.modulargolems.compat.materials.alexscaves.modifier.*;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.upgrade.CraftMaterialItem;
import dev.xkmc.modulargolems.content.item.upgrade.RepairMaterialItem;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.ModList;

import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class ACCompatRegistry {

	public static final RegistryEntry<CandyStickModifier> STICKY;
	public static final RegistryEntry<CandyShootModifier> SHOOT;
	public static final RegistryEntry<CandyDashModifier> DASH;
	public static final RegistryEntry<FreeMoveModifier> FREE;
	public static final RegistryEntry<PolarizeModifier> POLARIZE;
	public static final RegistryEntry<ReformationModifier> REFORMATION;
	public static final RegistryEntry<RadiationModifier> RADIATION;
	public static final RegistryEntry<AtomicFuelingModifier> ATOMIC;
	public static final RegistryEntry<AtomicBreathModifier> ATOMIC_BREATH;

	public static final ItemEntry<RepairMaterialItem> REPAIR_CANDY, REPAIR_MAGNETIC;
	public static final ItemEntry<CraftMaterialItem> CRAFT_CANDY, CRAFT_MAGNETIC;
	public static final ItemEntry<Item> CRAFT_NUCLEAR;
	public static final RegistryEntry<SimpleUpgradeItem> UPGRADE_ATOMIC_BREATH;

	public static final RegistryEntry<AtomicBoostedEffect> EFF_ATOMIC;
	public static final ItemEntry<DummyConsumer> DUMMY_IRON, DUMMY_URANIUM;

	static {
		STICKY = reg("sticky_caramel", () -> new CandyStickModifier(StatFilterType.ATTACK, 2),
				"Put molten caramel on attack targets");
		SHOOT = reg("gum_shooter", () -> new CandyShootModifier(StatFilterType.HEAD, 1),
				"Shoot gumballs at targets dealing %s damage. Each gumball may randomly gain Shotgum enchantment effects");
		DASH = reg("candy_dash", () -> new CandyDashModifier(StatFilterType.HEAD, 1),
				"Candy Dash", "Charge toward a distant target, damaging and knocking back all targets at the front of the path. Damage taken is reduced by %s while charging.");
		FREE = reg("free_movement", () -> new FreeMoveModifier(StatFilterType.MOVEMENT, 1),
				"Golem will not be stuck by blocks or caramel");
		POLARIZE = reg("polarize", () -> new PolarizeModifier(StatFilterType.MASS, 4),
				"Pull enemies in melee mode and push enemies away in ranged/standing mode, deal %s electrical damage constantly.");
		REFORMATION = reg("reformation", () -> new ReformationModifier(StatFilterType.HEALTH, 2),
				"Consume iron ingot to gain %s absorption and heal %s. Golem may hold at most %s absorption");
		RADIATION = reg("radiation", () -> new RadiationModifier(StatFilterType.ATTACK, 2),
				"Inflict %s to attack targets. Damage to radiated target increase by %s per radiation level.");
		ATOMIC = reg("atomic_fueling", () -> new AtomicFuelingModifier(StatFilterType.HEALTH, 2),
				"Consume [%s] to heal %s HP, and boost attack and speed by %s for %s seconds. Makes golem immune to irradiated effect.");
		ATOMIC_BREATH = reg("atomic_breath", () -> new AtomicBreathModifier(StatFilterType.HEAD, 1),
				"Atomic Breath Upgrade", "Continuously fire a beam hitting everything along it, and apply %s to hit targets.");

		REPAIR_CANDY = GolemItems.item(ACDispatch.MODID, "candy_mixture", RepairMaterialItem::new);
		CRAFT_CANDY = GolemItems.item(ACDispatch.MODID, "candy_construct", CraftMaterialItem::new);
		REPAIR_MAGNETIC = GolemItems.item(ACDispatch.MODID, "magnetic_alloy", RepairMaterialItem::new);
		CRAFT_MAGNETIC = GolemItems.item(ACDispatch.MODID, "magnetic_construct", CraftMaterialItem::new);
		CRAFT_NUCLEAR = GolemItems.item(ACDispatch.MODID, "nuclear_construct", Item::new);
		UPGRADE_ATOMIC_BREATH = regModUpgrade("atomic_breath", () -> ATOMIC_BREATH, ACDispatch.MODID)
				.lang("Tremorzilla Upgrade").register();

		EFF_ATOMIC = genEffect("atomic_boost", () -> new AtomicBoostedEffect(MobEffectCategory.BENEFICIAL, 0xffffffff),
				"Increase golem attack damage and movement speed");

		DUMMY_IRON = ModularGolems.REGISTRATE.item("dummy_iron_consumer", p -> new DummyConsumer(Tags.Items.INGOTS_IRON))
				.model((ctx, pvd) -> pvd.withExistingParent("item/" + ctx.getName(), "block/air"))
				.removeTab(GolemItems.ITEMS.getKey())
				.register();

		DUMMY_URANIUM = ModularGolems.REGISTRATE.item("dummy_uranium_consumer",
						p -> new DummyConsumer(ItemTags.create(new ResourceLocation("forge", "nuggets/uranium"))))
				.model((ctx, pvd) -> pvd.withExistingParent("item/" + ctx.getName(), "block/air"))
				.removeTab(GolemItems.ITEMS.getKey())
				.register();
	}

	private static <T extends MobEffect> RegistryEntry<T> genEffect(String name, NonNullSupplier<T> sup, String desc) {
		return ModularGolems.REGISTRATE.effect(name, sup, desc).lang(MobEffect::getDescriptionId).register();
	}

	public static void register() {
		if (ModList.get().isLoaded(L2Complements.MODID)) {
			MGTagGen.OPTIONAL_EFF.add(e -> e.addTag(TagGen.SKILL_EFFECT)
					.addOptional(EFF_ATOMIC.getId()));
			MGTagGen.OPTIONAL_ITEM.add(e -> e.addTag(MGTagGen.RED_UPGRADES)
					.addOptional(UPGRADE_ATOMIC_BREATH.getId()));
		}
	}

}
