package dev.xkmc.modulargolems.init.data;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.generators.RegistrateBlockModelGenerator;
import com.tterrag.registrate.providers.generators.RegistrateItemModelGenerator;
import dev.xkmc.modulargolems.content.block.TableBlock;
import dev.xkmc.modulargolems.content.item.render.IsInTag;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

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
								new Material(pvd.modLoc("item/blue_arrow"))),
						pvd.modelOutput)),
				ItemModelUtils.conditional(new IsInTag(MGTagGen.POTION_UPGRADES),
						ItemModelUtils.plainModel(ModelTemplates.TWO_LAYERED_ITEM.create(
								ModelLocationUtils.getModelLocation(ctx.get(), "_purple"),
								TextureMapping.layered(
										new Material(Identifier.fromNamespaceAndPath(modid, "item/upgrades/" + id)),
										new Material(pvd.modLoc("item/purple_arrow"))),
								pvd.modelOutput)),
						ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(ctx.get(), TextureMapping.layer0(
										new Material(Identifier.fromNamespaceAndPath(modid, "item/upgrades/" + id))),
								pvd.modelOutput))
				)));
	}


}
