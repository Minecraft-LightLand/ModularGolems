package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface IWeaponStatusPredicate {

	Optional<WeaponStatus> getProperties(HumanoidGolemEntity golem, ItemStack weapon, @Nullable InteractionHand hand);

}
