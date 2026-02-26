package dev.xkmc.modulargolems.init.material;

import dev.xkmc.modulargolems.content.item.equipments.MetalGolemWeaponItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public interface IGolemWeaponMaterial {

	int getDamage();

	String getName();

	Item getIngot();

	default Item.Properties modify(Item.Properties prop) {
		if (fireResistant()) {
			prop = prop.fireResistant();
		}
		return prop;
	}

	boolean fireResistant();

	default ResourceLocation modLoc(String s) {
		return ModularGolems.loc(s);
	}

	default IGolemWeaponFactory factory() {
		return MetalGolemWeaponItem::new;
	}

	default <T> T model(T model) {
		return model;
	}

}
