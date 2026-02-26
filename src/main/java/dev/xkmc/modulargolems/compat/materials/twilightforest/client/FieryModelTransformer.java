package dev.xkmc.modulargolems.compat.materials.twilightforest.client;

import dev.xkmc.l2serial.util.Wrappers;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.loaders.ItemLayerModelBuilder;

public class FieryModelTransformer {

	public static <T> T transform(T model) {
		return Wrappers.cast(((ItemModelBuilder) model)
				.customLoader(ItemLayerModelBuilder::begin)
				.emissive(15, 15, 0).end());
	}

}
