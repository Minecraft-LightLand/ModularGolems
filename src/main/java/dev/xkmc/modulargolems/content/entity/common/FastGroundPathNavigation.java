package dev.xkmc.modulargolems.content.entity.common;

import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FastGroundPathNavigation extends GroundPathNavigation {

	public FastGroundPathNavigation(Mob p_26448_, Level p_26449_) {
		super(p_26448_, p_26449_);
	}

	protected void followThePath() {
		Vec3 vec3 = this.getTempMobPos();
		this.maxDistanceToWaypoint = this.mob.getBbWidth() > 0.75F ? this.mob.getBbWidth() / 2.0F : 0.75F - this.mob.getBbWidth() / 2.0F;
		Vec3i vec3i = this.path.getNextNodePos();
		double d0 = Math.abs(this.mob.getX() - ((double) vec3i.getX() + (this.mob.getBbWidth() + 1) / 2D)); //Forge: Fix MC-94054
		double d1 = Math.abs(this.mob.getY() - (double) vec3i.getY());
		double d2 = Math.abs(this.mob.getZ() - ((double) vec3i.getZ() + (this.mob.getBbWidth() + 1) / 2D)); //Forge: Fix MC-94054
		boolean flag = d0 <= (double) this.maxDistanceToWaypoint && d2 <= (double) this.maxDistanceToWaypoint && d1 < 1.0D; //Forge: Fix MC-94054
		if (flag || this.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(vec3)) {
			this.path.advance();
		}

		this.doStuckDetection(vec3);
	}

	private boolean shouldTargetNextNodeInDirection(Vec3 current) {
		double s0 = mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
		double s1 = Math.max(2, s0 * 2 + 1);
		double s2 = s0 * 2 + 0.5;
		if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
			return false;
		} else {
			Vec3 next = Vec3.atBottomCenterOf(this.path.getNextNodePos());
			if (!current.closerThan(next, s1)) {
				return false;
			} else if (this.canMoveDirectly(current, this.path.getNextEntityPos(this.mob))) {
				return true;
			} else {
				Vec3 far = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
				Vec3 step1 = next.subtract(current);
				Vec3 step2 = far.subtract(current);
				double dsq1 = step1.lengthSqr();
				double dsq2 = step2.lengthSqr();
				boolean near = dsq2 < dsq1;
				if (dsq1 < s2 * s2) return true;
				boolean close = dsq1 < 0.5;
				if (!near && !close) {
					return false;
				} else {
					Vec3 dir1 = step1.normalize();
					Vec3 dir2 = step2.normalize();
					return dir2.dot(dir1) < 0.0D;
				}
			}
		}
	}


}
