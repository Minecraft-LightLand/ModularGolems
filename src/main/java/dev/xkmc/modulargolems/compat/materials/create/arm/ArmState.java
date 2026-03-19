package dev.xkmc.modulargolems.compat.materials.create.arm;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ArmState {

	public ItemStack heldItem;
	public Level level;
	public float baseAngle, lowerArmAngle, upperArmAngle, headAngle;

	public ArmState(Level level, ItemStack stack, float progress, ArmAngleTarget src, ArmAngleTarget dst) {
		this.level = level;
		this.heldItem = stack;
		this.baseAngle = Mth.lerp(progress, src.baseAngle, dst.baseAngle);
		this.lowerArmAngle = Mth.lerp(progress, src.lowerArmAngle, dst.lowerArmAngle);
		this.upperArmAngle = Mth.lerp(progress, src.upperArmAngle, dst.upperArmAngle);
		this.headAngle = angleLerp(progress, (src.headAngle % 360.0F), (dst.headAngle % 360.0F));
	}

	public static float angleLerp(double pct, double current, double target) {
		return (float) (current + getShortestAngleDiff(current, target) * pct);
	}

	public static float getShortestAngleDiff(double current, double target) {
		current %= 360.0F;
		target %= 360.0F;
		return (float) (((target - current) % 360.0F + 540.0F) % 360.0F - 180.0F);
	}


}
