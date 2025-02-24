package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.goal.Goal;

public record WeaponGoalHolder<T extends Goal & IWeaponGoal>(ResourceLocation id, T goal, boolean supportMelee) {
}
