package dev.xkmc.modulargolems.content.client.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.IGolemModel;
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

	void setup(AbstractGolemEntity<?, ?> entity, IGolemModel<?, ?, ?> model, ItemStack stack, InteractionHand hand, float pTick);

	void render(AbstractGolemEntity<?, ?> entity, IGolemModel<?, ?, ?> model, ItemStack stack, InteractionHand hand, PoseStack pose, MultiBufferSource source, int light, float pTick);

}
