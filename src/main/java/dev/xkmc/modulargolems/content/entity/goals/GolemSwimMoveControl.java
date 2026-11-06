package dev.xkmc.modulargolems.content.entity.goals;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

public class GolemSwimMoveControl extends MoveControl {
	private final AbstractGolemEntity<?, ?> golem;

	public GolemSwimMoveControl(AbstractGolemEntity<?, ?> pDrowned) {
		super(pDrowned);
		this.golem = pDrowned;
	}

	public void tick() {
		LivingEntity livingentity = this.golem.getTarget();
		if (this.golem.isInWater()) {
			if (livingentity != null && livingentity.getY() > this.golem.getY()) {
				this.golem.setDeltaMovement(this.golem.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
			}
			if (this.operation != MoveControl.Operation.MOVE_TO || this.golem.getNavigation().isDone()) {
				this.golem.setSpeed(0.0F);
				return;
			}

			double d0 = this.wantedX - this.golem.getX();
			double d1 = this.wantedY - this.golem.getY();
			double d2 = this.wantedZ - this.golem.getZ();
			double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
			// 已经到达目标点：停止推进，避免在接近 0 的位移上反复计算朝向导致原地打转（顺时针/逆时针绕圈）。
			if (d3 < 1.0E-4D) {
				this.golem.setSpeed(0.0F);
				return;
			}
			double dh = Math.sqrt(d0 * d0 + d2 * d2);
			// 水平方向几乎重合（目标近乎正上/正下方，例如玩家站在岸上、傀儡在脚下水里）时，
			// d0/d2 只剩浮点噪声，atan2 每 tick 乱跳，rotlerp 一路追着噪声转 → 原地顺时针/逆时针打转。
			// 这种情形只做竖直补偿，不改朝向。
			if (dh > 1.0E-4D) {
				float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
				this.golem.setYRot(this.rotlerp(this.golem.getYRot(), f, 90.0F));
				this.golem.yBodyRot = this.golem.getYRot();
			}
			double ny = d1 / d3;
			float f1 = (float) (this.speedModifier * this.golem.getAttributeValue(Attributes.MOVEMENT_SPEED));
			float f2 = Mth.lerp(0.125F, this.golem.getSpeed(), f1);
			this.golem.setSpeed(f2);
			this.golem.setDeltaMovement(this.golem.getDeltaMovement().add((double) f2 * d0 * 0.005D, (double) f2 * ny * 0.1D, (double) f2 * d2 * 0.005D));
		} else {
			if (!this.golem.onGround()) {
				this.golem.setDeltaMovement(this.golem.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
			}
			super.tick();
		}

	}
}