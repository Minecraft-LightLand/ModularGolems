package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

public record WeaponGoalEntry(
		boolean supportMelee,
		IWeaponGoalPredicate item,
		IWeaponGoalFactory<?> goal

) {
}
