package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public interface CustomSweepBoxWeapon {

	AABB getAttackBoundingBox(MetalGolemEntity self, Entity target, double range, ItemStack stack);

}
