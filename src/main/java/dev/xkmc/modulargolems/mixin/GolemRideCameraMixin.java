package dev.xkmc.modulargolems.mixin;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * 骑乘体型很大的傀儡时，把第三人称的相机距离按坐骑体型拉远，否则镜头会被巨大的傀儡糊满屏幕。
 * <p>
 * 原版 {@code Camera#setup} 里第三人称距离写死为 {@code getMaxZoom(4.0D)}；这里只改那个常量，
 * 拉远距离仍然走原版的 {@code getMaxZoom}，所以撞墙自动拉近、视角遮挡等行为全部保留。
 * 第一人称不受影响（不走这条分支）。
 */
@Pseudo
@Mixin(Camera.class)
public class GolemRideCameraMixin {

	/** 最多拉远到原版距离的多少倍，避免超大体型把镜头甩得太远。 */
	private static final double MAX_ZOOM_FACTOR = 2.5D;

	@ModifyConstant(method = "setup", constant = @Constant(doubleValue = 4.0D), require = 0)
	private double mg$rideZoom(double original) {
		Entity cam = Minecraft.getInstance().getCameraEntity();
		if (!(cam instanceof Player player)) return original;
		if (!(player.getVehicle() instanceof AbstractGolemEntity<?, ?> golem)) return original;
		double scale = golem.getScale();
		if (scale <= 1.0D) return original;
		return original * Math.min(scale, MAX_ZOOM_FACTOR);
	}

}
