package dev.xkmc.modulargolems.compat.materials.goety.revelation;

import dev.xkmc.modulargolems.compat.materials.common.ClientModDispatch;
import dev.xkmc.modulargolems.compat.materials.twilightforest.client.FieryArmors;
import net.minecraftforge.client.event.EntityRenderersEvent;

import static dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels.regAndAdd;

public class GRClient extends ClientModDispatch {

	@Override
	public void dispatchEntityLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
		regAndAdd(event, ApollyonArmors.HELMET_LAYER, ApollyonArmors::createHelmet);
		regAndAdd(event, ApollyonArmors.CHESTPLATE_LAYER, ApollyonArmors::createChestplate);
		regAndAdd(event, ApollyonArmors.SHINGUARD_LAYER, ApollyonArmors::createLeggings);
		regAndAdd(event, ApollyonArmors.BOOTS_LAYER, ApollyonArmors::createBoots);
	}

}
