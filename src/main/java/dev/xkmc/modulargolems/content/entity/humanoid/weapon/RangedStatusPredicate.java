package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface RangedStatusPredicate {

	Optional<WeaponStatus> getProperties(ItemStack stack);

}
