package dev.xkmc.modulargolems.content.entity.common.goals;

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
			d1 /= d3;
			float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
			this.golem.setYRot(this.rotlerp(this.golem.getYRot(), f, 90.0F));
			this.golem.yBodyRot = this.golem.getYRot();
			float f1 = (float) (this.speedModifier * this.golem.getAttributeValue(Attributes.MOVEMENT_SPEED));
			float f2 = Mth.lerp(0.125F, this.golem.getSpeed(), f1);
			this.golem.setSpeed(f2);
			this.golem.setDeltaMovement(this.golem.getDeltaMovement().add((double) f2 * d0 * 0.005D, (double) f2 * d1 * 0.1D, (double) f2 * d2 * 0.005D));
		} else {
			if (!this.golem.isOnGround()) {
				this.golem.setDeltaMovement(this.golem.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
			}
			super.tick();
		}

	}
}