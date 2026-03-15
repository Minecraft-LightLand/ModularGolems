package dev.xkmc.modulargolems.content.item.ranged;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IMultiShotBow {

	int getMaxShoot(@Nullable LivingEntity user, ItemStack stack);

}
