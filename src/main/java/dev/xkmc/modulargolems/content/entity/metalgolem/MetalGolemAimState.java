package dev.xkmc.modulargolems.content.entity.metalgolem;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public record MetalGolemAimState(
		float scale, float yBodyRot,
		Optional<Vec3> targetAimPos, Vec3 position, Vec3 viewVector,
		TargetingAnimState animState
) {

	public static MetalGolemAimState of(MetalGolemEntity e, float pt) {
		return new MetalGolemAimState(
				e.getScale(), Mth.lerp(pt, e.yBodyRotO, e.yBodyRot),
				e.getTargetAimPos(), e.position(), e.getViewVector(1),
				e.animState
		);
	}

}
