package dev.xkmc.modulargolems.content.entity.humanoid.ranged;

import dev.xkmc.modulargolems.content.entity.humanoid.weapon.WeaponStatus;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface RangedStatusPredicate {

	Optional<WeaponStatus> getProperties(ItemStack stack);

}
