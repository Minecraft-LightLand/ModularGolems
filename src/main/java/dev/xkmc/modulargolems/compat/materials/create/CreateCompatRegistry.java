package dev.xkmc.modulargolems.compat.materials.create;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.content.modifier.base.AttributeGolemModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.List;

import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class CreateCompatRegistry {

	public static final RegistryEntry<CoatingModifier> COATING;
	public static final RegistryEntry<AttributeGolemModifier> PUSH;

	public static final RegistryEntry<SimpleUpgradeItem> UP_COATING, UP_PUSH;

	static {
		COATING = reg("coating", CoatingModifier::new, "Reduce damage taken by %s");
		PUSH = reg("push", () -> new AttributeGolemModifier(1,
				new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_ATKKB, () -> 1)
		)).register();
		UP_COATING = regModUpgrade("coating", () -> COATING, CreateDispatch.MODID).lang("Zinc Upgrade").register();
		UP_PUSH = regModUpgrade("push", () -> PUSH, CreateDispatch.MODID).lang("Extendo Upgrade").register();

	}

	public static void register() {
		MGTagGen.OPTIONAL_ITEM.add(e -> e.addTag(MGTagGen.SPECIAL_CRAFT)
				.addOptional(AllItems.ANDESITE_ALLOY.getId())
				.addOptionalTag(new ResourceLocation("forge", "ingots/brass"))
				.addOptional(AllBlocks.RAILWAY_CASING.getId()));
	}

}
