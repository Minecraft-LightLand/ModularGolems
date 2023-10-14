package dev.xkmc.modulargolems.content.client.pose;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModel;
import net.minecraft.util.Mth;

public class WeaponPose extends MetalGolemPose {

	public static final MetalGolemPose WEAPON = new WeaponPose();

	private static float amplitude() {
		return (float) (Math.PI / 2);
	}

	@Override
	public void attackModel(MetalGolemEntity entity, MetalGolemModel model, float atkTick) {
		model.rightArm.xRot = 0;
		model.leftArm.xRot = 0;
		model.rightForeArm.xRot = -amplitude() * 0.5f * (1 + Mth.triangleWave(atkTick, 10));
		model.leftForeArm.xRot = 0;
	}

	@Override
	public void aggressive(MetalGolemEntity entity, MetalGolemModel model, float walkTick, float speed, float pTick) {
		model.rightArm.xRot = 0;
		model.leftArm.xRot = 0;
		model.rightForeArm.xRot = -amplitude();
		model.leftForeArm.xRot = 0;
	}

	public void walking(MetalGolemEntity entity, MetalGolemModel model, float walkTick, float speed, float pTick) {
		super.walking(entity, model, walkTick, speed, pTick);

		//FIXME for debug
		model.rightForeArm.xRot = -amplitude() * 0.5f * (1 + Mth.triangleWave((entity.tickCount + pTick) * 0.1f, 10));
	}

}
