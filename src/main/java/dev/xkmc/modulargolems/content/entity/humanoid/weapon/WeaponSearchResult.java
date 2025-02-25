package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import net.minecraft.resources.ResourceLocation;

public record WeaponSearchResult(ResourceLocation id, WeaponStatus status, WeaponGoalEntry entry) {
}
