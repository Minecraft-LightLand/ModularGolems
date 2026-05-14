package dev.xkmc.modulargolems.content.client.pose;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModel;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.LinkedHashMap;

public class MetalGolemPose {

	public static final MetalGolemPose DEFAULT = new MetalGolemPose();

	public static final LinkedHashMap<Identifier, MetalGolemPose> MAP = new LinkedHashMap<>();

	public static synchronized void register(Identifier id, MetalGolemPose pose) {
		MAP.put(id, pose);
	}

	public void attackModel(MetalGolemRenderState entity, MetalGolemModel model, float atkTick) {
		model.rightArm.xRot = -2.0F + 1.5F * Mth.triangleWave(atkTick, 10.0F);
		model.leftArm.xRot = -2.0F + 1.5F * Mth.triangleWave(atkTick, 10.0F);
		model.rightForeArm.xRot = 0;
		model.leftForeArm.xRot = 0;
	}

	public void aggressive(MetalGolemRenderState entity, MetalGolemModel model, float walkTick, float speed) {
		walking(entity, model, walkTick, speed);
	}

	public void walking(MetalGolemRenderState entity, MetalGolemModel model, float walkTick, float speed) {
		model.rightArm.xRot = (-0.2F + 1.5F * Mth.triangleWave(walkTick, 13.0F)) * speed;
		model.leftArm.xRot = (-0.2F - 1.5F * Mth.triangleWave(walkTick, 13.0F)) * speed;
		model.rightForeArm.xRot = 0;
		model.leftForeArm.xRot = 0;
	}
}
