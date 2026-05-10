package dev.xkmc.modulargolems.compat.materials.cataclysm;

import dev.xkmc.modulargolems.compat.materials.cataclysm.client.HarbingerArmors;
import dev.xkmc.modulargolems.compat.materials.cataclysm.client.IgnisArmors;
import dev.xkmc.modulargolems.compat.materials.cataclysm.client.MonstrosityArmors;
import dev.xkmc.modulargolems.compat.materials.common.ClientModDispatch;
import dev.xkmc.modulargolems.content.client.override.ModelOverride;
import dev.xkmc.modulargolems.content.client.override.ModelOverrides;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import static dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels.regAndAdd;

public class CataClient extends ClientModDispatch {

	@Override
	public void dispatchClientSetup() {

		ModelOverrides.registerOverride(Identifier.fromNamespaceAndPath(CataDispatch.MODID, "ignitium"),
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
	}

}
