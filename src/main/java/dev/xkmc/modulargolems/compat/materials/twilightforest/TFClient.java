package dev.xkmc.modulargolems.compat.materials.twilightforest;

import dev.xkmc.modulargolems.compat.materials.common.ClientModDispatch;
import dev.xkmc.modulargolems.compat.materials.twilightforest.client.*;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;

import static dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels.regAndAdd;

public class TFClient extends ClientModDispatch {

	@Override
	public void dispatchClientSetup() {
		MinecraftForge.EVENT_BUS.register(TFClientEventHandler.class);
	}

	@Override
	public void dispatchEntityLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
		regAndAdd(event, IronwoodArmors.HELMET_LAYER, IronwoodArmors::createHelmet);
		regAndAdd(event, IronwoodArmors.CHESTPLATE_LAYER, IronwoodArmors::createChestplate);
		regAndAdd(event, IronwoodArmors.SHINGUARD_LAYER, IronwoodArmors::createLeggings);
		regAndAdd(event, IronwoodArmors.BOOTS_LAYER, IronwoodArmors::createBoots);

		regAndAdd(event, NagaArmors.HELMET_LAYER, NagaArmors::createHelmet);
		regAndAdd(event, NagaArmors.CHESTPLATE_LAYER, NagaArmors::createChestplate);
		regAndAdd(event, NagaArmors.SHINGUARD_LAYER, NagaArmors::createLeggings);
		regAndAdd(event, NagaArmors.BOOTS_LAYER, NagaArmors::createBoots);

		regAndAdd(event, KnightmetalArmors.HELMET_LAYER, KnightmetalArmors::createHelmet);
		regAndAdd(event, KnightmetalArmors.CHESTPLATE_LAYER, KnightmetalArmors::createChestplate);
		regAndAdd(event, KnightmetalArmors.SHINGUARD_LAYER, KnightmetalArmors::createLeggings);
		regAndAdd(event, KnightmetalArmors.BOOTS_LAYER, KnightmetalArmors::createBoots);

		regAndAdd(event, FieryArmors.HELMET_LAYER, FieryArmors::createHelmet);
		regAndAdd(event, FieryArmors.CHESTPLATE_LAYER, FieryArmors::createChestplate);
		regAndAdd(event, FieryArmors.SHINGUARD_LAYER, FieryArmors::createLeggings);
		regAndAdd(event, FieryArmors.BOOTS_LAYER, FieryArmors::createBoots);
	}
}
