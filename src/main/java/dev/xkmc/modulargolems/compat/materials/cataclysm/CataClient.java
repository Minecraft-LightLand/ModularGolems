package dev.xkmc.modulargolems.compat.materials.cataclysm;

import dev.xkmc.modulargolems.compat.materials.cataclysm.client.HarbingerArmors;
import dev.xkmc.modulargolems.compat.materials.cataclysm.client.IgnisArmors;
import dev.xkmc.modulargolems.compat.materials.cataclysm.client.MaledictusArmors;
import dev.xkmc.modulargolems.compat.materials.cataclysm.client.MonstrosityArmors;
import dev.xkmc.modulargolems.compat.materials.common.ClientModDispatch;
import dev.xkmc.modulargolems.content.client.override.ModelOverride;
import dev.xkmc.modulargolems.content.client.override.ModelOverrides;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;

import static dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels.regAndAdd;

public class CataClient extends ClientModDispatch {

	@Override
	public void dispatchClientSetup() {
		ModelOverrides.registerOverride(new ResourceLocation(CataDispatch.MODID, "ignitium"),
				ModelOverride.texturePredicate((e) -> CataDispatch.ignisBlue(e) ? "_soul" : ""));
	}

	@Override
	public void dispatchEntityLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {

		regAndAdd(event, HarbingerArmors.HELMET_LAYER, HarbingerArmors::createHelmet);
		regAndAdd(event, HarbingerArmors.CHESTPLATE_LAYER, HarbingerArmors::createChestplate);
		regAndAdd(event, HarbingerArmors.SHINGUARD_LAYER, HarbingerArmors::createLeggings);

		regAndAdd(event, MonstrosityArmors.HELMET_LAYER, MonstrosityArmors::createHelmet);
		regAndAdd(event, MonstrosityArmors.CHESTPLATE_LAYER, MonstrosityArmors::createChestplate);
		regAndAdd(event, MonstrosityArmors.SHINGUARD_LAYER, MonstrosityArmors::createLeggings);

		regAndAdd(event, IgnisArmors.HELMET_LAYER, IgnisArmors::createHelmet);
		regAndAdd(event, IgnisArmors.CHESTPLATE_LAYER, IgnisArmors::createChestplate);
		regAndAdd(event, IgnisArmors.SHINGUARD_LAYER, IgnisArmors::createLeggings);

		regAndAdd(event, MaledictusArmors.HELMET_LAYER, MaledictusArmors::createHelmet);
		regAndAdd(event, MaledictusArmors.CHESTPLATE_LAYER, MaledictusArmors::createChestplate);
		regAndAdd(event, MaledictusArmors.SHINGUARD_LAYER, MaledictusArmors::createLeggings);
	}

}
