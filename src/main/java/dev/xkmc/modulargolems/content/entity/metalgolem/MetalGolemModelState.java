package dev.xkmc.modulargolems.content.entity.metalgolem;

public record MetalGolemModelState(
		long gameTime, float php
) {

	public static MetalGolemModelState of(MetalGolemEntity e) {
		float php = e.getGuardedDataImpl() / e.getMaxHealth();
		return new MetalGolemModelState(e.level().getGameTime(), php);
	}

}
