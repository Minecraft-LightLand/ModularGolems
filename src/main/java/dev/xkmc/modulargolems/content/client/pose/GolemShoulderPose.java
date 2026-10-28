package dev.xkmc.modulargolems.content.client.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.IGolemModel;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;

public interface GolemShoulderPose {

	LinkedHashMap<ResourceLocation, GolemShoulderPose> MAP = new LinkedHashMap<>();

	static void register(ResourceLocation id, GolemShoulderPose pose) {
		synchronized (MAP) {
			MAP.put(id, pose);
		}
	}

	default void setup(AbstractGolemEntity<?, ?> entity, IGolemModel<?, ?, ?> model, ItemStack stack, int slot, float pTick) {

	}

	default void render(AbstractGolemEntity<?, ?> entity, IGolemModel<?, ?, ?> model, ItemStack stack, int slot, PoseStack pose, MultiBufferSource source, int light, float pTick) {

	}

	default void setup(AbstractGolemEntity<?, ?> entity, IGolemModel<?, ?, ?> model, ItemStack stack, InteractionHand hand, float pTick) {
		setup(entity, model, stack, hand == InteractionHand.MAIN_HAND ? 1 : -1, pTick);
	}

	default void render(AbstractGolemEntity<?, ?> entity, IGolemModel<?, ?, ?> model, ItemStack stack, InteractionHand hand, PoseStack pose, MultiBufferSource source, int light, float pTick) {
		render(entity, model, stack, hand == InteractionHand.MAIN_HAND ? 1 : -1, pose, source, light, pTick);
	}

	@Deprecated
	default void setup(MetalGolemEntity entity, MetalGolemModel model, ItemStack stack, InteractionHand hand, float pTick) {
		setup((AbstractGolemEntity<?, ?>) entity, model, stack, hand, pTick);
	}

	@Deprecated
	default void render(MetalGolemEntity entity, MetalGolemModel model, ItemStack stack, InteractionHand hand, PoseStack pose, MultiBufferSource source, int light, float pTick) {
		render((AbstractGolemEntity<?, ?>) entity, model, stack, hand, pose, source, light, pTick);
	}

}
