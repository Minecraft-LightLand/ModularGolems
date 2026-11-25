package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import dev.xkmc.modulargolems.compat.materials.common.ClientModDispatch;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.client.CloudArmors;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.client.ObliteratorArmors;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.client.PaladinArmors;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.client.override.ModelOverride;
import dev.xkmc.modulargolems.content.entity.common.GolemFormAppearance;
import dev.xkmc.modulargolems.content.client.override.ModelOverrides;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import static dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels.regAndAdd;

public class LMClient extends ClientModDispatch {

	@Override
	public void dispatchClientSetup() {
		// Boss 变 angry 的阈值是 Crackiness.MEDIUM（低于 65%），贴图切换跟着走，不再用半血
		ModelOverrides.registerOverride(ResourceLocation.fromNamespaceAndPath(LMDispatch.MODID, "cloud"),
				ModelOverride.texturePredicate((e) -> GolemFormAppearance.visualFinal(e, LMArmorHelper.cloudFinalForm(e), LMArmorHelper.PHASE_TWO) ? "_dark" : ""));
		ModelOverrides.registerOverride(ResourceLocation.fromNamespaceAndPath(LMDispatch.MODID, "paladin"),
				ModelOverride.texturePredicate((e) -> GolemFormAppearance.visualFinal(e, LMArmorHelper.paladinFinalForm(e), LMArmorHelper.PHASE_TWO) ? "_posessed" : ""));
	}

	@Override
	public void dispatchEntityLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
		regAndAdd(event, PaladinArmors.HELMET_LAYER, PaladinArmors::createHelmet);
		regAndAdd(event, PaladinArmors.CHESTPLATE_LAYER, PaladinArmors::createChestplate);
		regAndAdd(event, PaladinArmors.SHINGUARD_LAYER, PaladinArmors::createLeggings);

		regAndAdd(event, CloudArmors.HELMET_LAYER, CloudArmors::createHelmet);
		regAndAdd(event, CloudArmors.CHESTPLATE_LAYER, CloudArmors::createChestplate);
		regAndAdd(event, CloudArmors.SHINGUARD_LAYER, CloudArmors::createLeggings);

		regAndAdd(event, ObliteratorArmors.HELMET_LAYER, ObliteratorArmors::createHelmet);
		regAndAdd(event, ObliteratorArmors.CHESTPLATE_LAYER, ObliteratorArmors::createChestplate);
		regAndAdd(event, ObliteratorArmors.SHINGUARD_LAYER, ObliteratorArmors::createLeggings);
	}

}