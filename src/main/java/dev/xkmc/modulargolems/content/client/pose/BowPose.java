package dev.xkmc.modulargolems.content.client.pose;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModel;
import dev.xkmc.modulargolems.content.item.ranged.BowPoseUtil;
import net.minecraft.client.model.AnimationUtils;

public class BowPose extends MetalGolemPose {

	public static final MetalGolemPose BOW = new BowPose();

	@Override
	public void attackModel(MetalGolemEntity entity, MetalGolemModel model, float atkTick) {
		AnimationUtils.swingWeaponDown(model.rightArm, model.leftArm, entity, model.attackTime, atkTick);
		model.leftArm.xRot = 0;
		model.rightForeArm.xRot = 0;
		model.leftForeArm.xRot = 0;
	}

	@Override
	public void aggressive(MetalGolemEntity entity, MetalGolemModel model, float walkTick, float speed, float pTick) {
		model.rightArm.xRot = BowPoseUtil.getAngle(entity);
		model.leftArm.xRot = 0;
		model.rightForeArm.xRot = 0;
		model.leftForeArm.xRot = 0;
	}

	public void walking(MetalGolemEntity entity, MetalGolemModel model, float walkTick, float speed, float pTick) {
		super.walking(entity, model, walkTick, speed, pTick);
	}

}
