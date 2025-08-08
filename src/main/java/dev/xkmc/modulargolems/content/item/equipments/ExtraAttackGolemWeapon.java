package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.world.entity.Entity;

public interface ExtraAttackGolemWeapon {

	boolean repeatAttack(MetalGolemEntity self, Entity target, float damage, boolean prev);

}
