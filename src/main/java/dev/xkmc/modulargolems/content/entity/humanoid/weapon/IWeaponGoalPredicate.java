package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IWeaponGoalPredicate {

	boolean isValid(HumanoidGolemEntity golem, ItemStack weapon, @Nullable InteractionHand hand);

}
