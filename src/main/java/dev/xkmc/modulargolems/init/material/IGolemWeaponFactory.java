package dev.xkmc.modulargolems.init.material;

import dev.xkmc.modulargolems.content.item.equipments.MetalGolemWeaponItem;
import net.minecraft.world.item.Item;

public interface IGolemWeaponFactory {

	MetalGolemWeaponItem create(Item.Properties properties, int attackDamage, double percentAttack, float range, float sweep);

}
