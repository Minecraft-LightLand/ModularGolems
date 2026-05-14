package dev.xkmc.modulargolems.content.client.pose;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemAimState;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModel;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;

public interface GolemShoulderPose {

	LinkedHashMap<Identifier, GolemShoulderPose> MAP = new LinkedHashMap<>();

	static void register(Identifier id, GolemShoulderPose pose) {
		synchronized (MAP) {
			MAP.put(id, pose);
		}
	}

	void setup(MetalGolemAimState entity, MetalGolemModel model, ItemStack stack, HumanoidArm hand);

	void submit(MetalGolemRenderState entity, ItemStack stack, HumanoidArm hand, PoseStack pose, SubmitNodeCollector source, int light);

}
