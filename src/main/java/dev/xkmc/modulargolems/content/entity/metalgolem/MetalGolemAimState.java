package dev.xkmc.modulargolems.content.entity.metalgolem;

import net.minecraft.world.phys.Vec3;

public record MetalGolemAimState(float scale, Vec3 targetAimPos, Vec3 position, Vec3 viewVector) {

	public static MetalGolemAimState of(MetalGolemEntity e) {
		return new MetalGolemAimState(e.getAgeScale(), e.getTargetAimPos(), e.position(), e.getViewVector(1));
	}

}
