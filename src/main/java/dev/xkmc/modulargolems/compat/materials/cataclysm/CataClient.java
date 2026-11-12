package dev.xkmc.modulargolems.compat.materials.cataclysm;

import dev.xkmc.modulargolems.compat.materials.cataclysm.client.AncientMetalArmors;
import dev.xkmc.modulargolems.compat.materials.cataclysm.client.EnderGuardianArmors;
import dev.xkmc.modulargolems.compat.materials.cataclysm.client.HarbingerArmors;
import dev.xkmc.modulargolems.compat.materials.cataclysm.client.IgnisArmors;
import dev.xkmc.modulargolems.compat.materials.cataclysm.client.MaledictusArmors;
import dev.xkmc.modulargolems.compat.materials.cataclysm.client.MonstrosityArmors;
import dev.xkmc.modulargolems.compat.materials.cataclysm.client.StormArmors;
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

		regAndAdd(event, EnderGuardianArmors.HELMET_LAYER, EnderGuardianArmors::createHelmet);
		regAndAdd(event, EnderGuardianArmors.CHESTPLATE_LAYER, EnderGuardianArmors::createChestplate);
		regAndAdd(event, EnderGuardianArmors.SHINGUARD_LAYER, EnderGuardianArmors::createLeggings);

		regAndAdd(event, AncientMetalArmors.HELMET_LAYER, AncientMetalArmors::createHelmet);
		regAndAdd(event, AncientMetalArmors.CHESTPLATE_LAYER, AncientMetalArmors::createChestplate);
		regAndAdd(event, AncientMetalArmors.SHINGUARD_LAYER, AncientMetalArmors::createLeggings);

		regAndAdd(event, StormArmors.HELMET_LAYER, StormArmors::createHelmet);
		regAndAdd(event, StormArmors.CHESTPLATE_LAYER, StormArmors::createChestplate);
		regAndAdd(event, StormArmors.SHINGUARD_LAYER, StormArmors::createLeggings);
	}

}
