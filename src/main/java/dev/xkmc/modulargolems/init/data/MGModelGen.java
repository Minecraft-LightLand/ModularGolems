package dev.xkmc.modulargolems.init.data;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.generators.RegistrateBlockModelGenerator;
import com.tterrag.registrate.providers.generators.RegistrateItemModelGenerator;
import dev.xkmc.modulargolems.content.block.TableBlock;
import dev.xkmc.modulargolems.content.item.render.IsInTag;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.material.IGolemWeaponMaterial;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

public class MGModelGen {

	private static final TextureSlot MID = TextureSlot.create("middle");

	public static void genTable(DataGenContext<Block, TableBlock> ctx, RegistrateBlockModelGenerator pvd) {
		pvd.generate(ctx.get(), TexturedModel.createDefault(block -> new TextureMapping()
						.put(TextureSlot.TOP, new Material(pvd.modLoc("block/table_top")))
						.put(MID, new Material(pvd.modLoc("block/table_middle")))
						.put(TextureSlot.BOTTOM, new Material(pvd.modLoc("block/table_bottom")))
						.put(TextureSlot.PARTICLE, new Material(pvd.modLoc("block/table_particle"))),
				ModelTemplates.create(pvd.modLoc("table").toString(),
						TextureSlot.TOP, MID, TextureSlot.BOTTOM, TextureSlot.PARTICLE
				)));
	}

	public static void genUpgrade(DataGenContext<Item, SimpleUpgradeItem> ctx, RegistrateItemModelGenerator pvd, String modid, String id) {
		pvd.itemModelOutput.accept(ctx.get(), ItemModelUtils.conditional(new IsInTag(MGTagGen.BLUE_UPGRADES),
				ItemModelUtils.plainModel(ModelTemplates.TWO_LAYERED_ITEM.create(
						ModelLocationUtils.getModelLocation(ctx.get(), "_blue"),
						TextureMapping.layered(
								new Material(Identifier.fromNamespaceAndPath(modid, "item/upgrades/" + id)),
								new Material(ModularGolems.loc("item/blue_arrow"))),
						pvd.modelOutput)),
				ItemModelUtils.conditional(new IsInTag(MGTagGen.POTION_UPGRADES),
						ItemModelUtils.plainModel(ModelTemplates.TWO_LAYERED_ITEM.create(
								ModelLocationUtils.getModelLocation(ctx.get(), "_purple"),
								TextureMapping.layered(
										new Material(Identifier.fromNamespaceAndPath(modid, "item/upgrades/" + id)),
										new Material(ModularGolems.loc("item/purple_arrow"))),
								pvd.modelOutput)),
						ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(ctx.get(), TextureMapping.layer0(
										new Material(Identifier.fromNamespaceAndPath(modid, "item/upgrades/" + id))),
								pvd.modelOutput))
				)));
	}


	public static <T extends Item> void genWeapon(DataGenContext<Item, T> ctx, RegistrateItemModelGenerator pvd, String model, boolean hasIcon, @Nullable IGolemWeaponMaterial material) {
		var id = "item/equipments/" + ctx.getName();
		var tex = material == null ? pvd.modLoc(id) : material.modLoc(id);
		if (hasIcon) {
			ModelTemplate template = ModelTemplates.createItem(model, TextureSlot.LAYER0);
			Material large = new Material(tex);
			Material small = new Material(tex.withSuffix("_icon"));
			Identifier main = template.create(ctx.get(), TextureMapping.layer0(large), pvd.modelOutput);
			Identifier icon = ModelTemplates.FLAT_ITEM.create(pvd.modLoc("item/" + ctx.getName() + "_icon"), TextureMapping.layer0(small), pvd.modelOutput);
			pvd.itemModelOutput.accept(ctx.get(), ItemModelUtils.select(new DisplayContext(), ItemModelUtils.plainModel(main), ItemModelUtils.when(ItemDisplayContext.GUI, ItemModelUtils.plainModel(icon))));
		} else {
			pvd.generateFlatItem(ctx.get(), ModelTemplates.createItem(model, TextureSlot.LAYER0), new Material(tex));
		}

	}
}
