package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

public record WeaponGoalEntry(
		IWeaponStatusPredicate item,
		IWeaponGoalFactory<?> goal

) {
}
