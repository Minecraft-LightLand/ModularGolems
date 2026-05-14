package dev.xkmc.modulargolems.content.client.pose;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModel;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemRenderState;
import net.minecraft.client.model.AnimationUtils;

public class WeaponPose extends MetalGolemPose {

	public static final MetalGolemPose WEAPON = new WeaponPose();

	@Override
	public void attackModel(MetalGolemRenderState entity, MetalGolemModel model, float atkTick) {
		AnimationUtils.swingWeaponDown(model.rightArm, model.leftArm, entity.mainArm, entity.attackTime, entity.ageInTicks);
		model.leftArm.xRot = 0;
		model.rightForeArm.xRot = 0;
		model.leftForeArm.xRot = 0;
	}

	@Override
	public void aggressive(MetalGolemRenderState entity, MetalGolemModel model, float walkTick, float speed) {
		model.rightArm.xRot = -1.8f;
		model.leftArm.xRot = 0;
		model.rightForeArm.xRot = 0;
		model.leftForeArm.xRot = 0;
	}

	public void walking(MetalGolemRenderState entity, MetalGolemModel model, float walkTick, float speed) {
		super.walking(entity, model, walkTick, speed);
	}

}
