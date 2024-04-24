package dev.xkmc.modulargolems.compat.materials.create;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.xkmc.l2complements.init.L2Complements;
import dev.xkmc.l2complements.init.data.TagGen;
import dev.xkmc.modulargolems.compat.materials.create.automation.DummyFurnace;
import dev.xkmc.modulargolems.compat.materials.create.modifier.*;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.content.modifier.base.AttributeGolemModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.fml.ModList;

import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class CreateCompatRegistry {

	public static final RegistryEntry<CoatingModifier> COATING;
	public static final RegistryEntry<AttributeGolemModifier> PUSH;
	public static final RegistryEntry<MechBodyModifier> BODY;
	public static final RegistryEntry<MechMobileModifier> MOBILE;
	public static final RegistryEntry<MechForceModifier> FORCE;

	public static final RegistryEntry<MechMobileEffect> EFF_MOBILE;
	public static final RegistryEntry<MechForceEffect> EFF_FORCE;

	public static final ItemEntry<SimpleUpgradeItem> UP_COATING, UP_PUSH;
	public static final ItemEntry<DummyFurnace> DUMMY;

	static {
		COATING = reg("coating", CoatingModifier::new, "Reduce damage taken by %s");
		PUSH = reg("push", () -> new AttributeGolemModifier(1,
				new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_ATKKB, () -> 1)
		)).register();
		BODY = reg("mechanical_engine", MechBodyModifier::new, "Consumes fuels to power the golem up.");
		MOBILE = reg("mechanical_mobility", MechMobileModifier::new, "When burning fuels, increase speed by %s%%");
		FORCE = reg("mechanical_force", MechForceModifier::new, "When burning fuels, increase attack damage by %s%%");
		UP_COATING = regModUpgrade("coating", () -> COATING, CreateDispatch.MODID).lang("Zinc Upgrade").register();
		UP_PUSH = regModUpgrade("push", () -> PUSH, CreateDispatch.MODID).lang("Extendo Upgrade").register();

		EFF_MOBILE = genEffect("mechanical_mobility", () -> new MechMobileEffect(MobEffectCategory.BENEFICIAL, 0xffffffff),
				"Increase golem movement speed");
		EFF_FORCE = genEffect("mechanical_force", () -> new MechForceEffect(MobEffectCategory.BENEFICIAL, 0xffffffff),
				"Increase golem attack damage");

		DUMMY = ModularGolems.REGISTRATE.item("dummy_furnace", p -> new DummyFurnace())
				.model((ctx, pvd) -> pvd.withExistingParent("item/" + ctx.getName(), "block/air"))
				.removeTab(GolemItems.TAB.getKey())
				.register();
	}

	private static <T extends MobEffect> RegistryEntry<T> genEffect(String name, NonNullSupplier<T> sup, String desc) {
		return ModularGolems.REGISTRATE.effect(name, sup, desc).lang(MobEffect::getDescriptionId).register();
	}

	public static void register() {
		MGTagGen.OPTIONAL_ITEM.add(e -> e.addTag(MGTagGen.SPECIAL_CRAFT)
				.addOptional(AllItems.ANDESITE_ALLOY.getId())
				.addOptionalTag(new ResourceLocation("forge", "ingots/brass"))
				.addOptional(AllBlocks.RAILWAY_CASING.getId()));
		if (ModList.get().isLoaded(L2Complements.MODID)) {
			MGTagGen.OPTIONAL_EFF.add(e -> e.addTag(TagGen.SKILL_EFFECT)
					.addOptional(EFF_MOBILE.getId()).addOptional(EFF_FORCE.getId()));
		}
	}

}
