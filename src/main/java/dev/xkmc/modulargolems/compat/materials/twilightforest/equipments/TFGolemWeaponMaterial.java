package dev.xkmc.modulargolems.compat.materials.twilightforest.equipments;

import dev.xkmc.modulargolems.compat.materials.twilightforest.TFCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.twilightforest.client.FieryModelTransformer;
import dev.xkmc.modulargolems.init.material.IGolemWeaponFactory;
import dev.xkmc.modulargolems.init.material.IGolemWeaponMaterial;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import twilightforest.init.TFItems;

import java.util.Locale;

public enum TFGolemWeaponMaterial implements IGolemWeaponMaterial {
	IRONWOOD(6, false, TFItems.IRONWOOD_INGOT::get),
	STEELEAF(8, false, TFItems.STEELEAF_INGOT::get),
	KNIGHTMETAL(8, false, TFItems.KNIGHTMETAL_INGOT::get),
	FIERY(10, true, TFItems.FIERY_INGOT::get);

	private final int damage;
	private final boolean fireResistant;
	private final ItemLike ingot;

	TFGolemWeaponMaterial(int damage, boolean fireResistant, ItemLike ingot) {
		this.damage = damage;
		this.fireResistant = fireResistant;
		this.ingot = ingot;
	}

	@Override
	public int getDamage() {
		return damage;
	}

	@Override
	public String getName() {
		return name().toLowerCase(Locale.ROOT);
	}

	@Override
	public boolean fireResistant() {
		return fireResistant;
	}

	public Item getIngot() {
		return ingot.asItem();
	}

	public ResourceLocation modLoc(String s) {
		return TFCompatRegistry.tfLoc(s);
	}

	@Override
	public IGolemWeaponFactory factory() {
		return switch (this) {
			case FIERY -> FieryMetalGolemWeaponItem::new;
			case KNIGHTMETAL -> KnightmetalMetalGolemWeaponItem::new;
			default -> TFMetalGolemWeaponItem::new;
		};
	}

	@Override
	public <T> T model(T model) {
		if (this != FIERY) return model;
		return FieryModelTransformer.transform(model);
	}

	public Item getHandle() {
		return switch (this) {
			case STEELEAF -> TFItems.LIVEROOT.get();
			case FIERY -> Items.BLAZE_ROD;
			default -> Items.STICK;
		};
	}


}
