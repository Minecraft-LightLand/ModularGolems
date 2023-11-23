package dev.xkmc.modulargolems.content.client.override;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class ModelOverride {

	public static final ModelOverride DEFAULT = new ModelOverride();

	public static ModelOverride texturePredicate(Function<AbstractGolemEntity<?, ?>, String> modifier) {
		return new ModelOverride() {

			@Override
			public ResourceLocation getTexture(AbstractGolemEntity<?, ?> golem, ResourceLocation id) {
				return super.getTexture(golem, id).withSuffix(modifier.apply(golem));
			}

		};

	}

	public ResourceLocation getTexture(AbstractGolemEntity<?, ?> golem, ResourceLocation id) {
		return id;
	}

}
