package dev.xkmc.modulargolems.content.entity.metalgolem;

public record MetalGolemModelState(
		float php
) {

	public static MetalGolemModelState of(MetalGolemEntity e) {
		float php = e.getGuardedDataImpl() / e.getMaxHealth();
		return new MetalGolemModelState(php);
	}

}
