package dev.xkmc.modulargolems.init.data;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.generators.RegistrateBlockModelGenerator;
import dev.xkmc.modulargolems.content.block.TableBlock;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.resources.model.sprite.Material;
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

}
