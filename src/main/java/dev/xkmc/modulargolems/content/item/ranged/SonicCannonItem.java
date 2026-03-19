package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.client.weapon.IEntityModelWeapon;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.GolemEquipmentItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SonicCannonItem extends GolemEquipmentItem implements IEntityModelWeapon {

	public SonicCannonItem(Properties properties) {
		super(properties, EquipmentSlot.MAINHAND, GolemTypes.ENTITY_GOLEM::get, e -> {
		});
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public boolean emissive() {
		return true;
	}

	@Override
	public ResourceLocation getModelTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		return ModularGolems.loc("textures/equipments/sonic_cannon.png");
	}

	@Override
	public ResourceLocation getEmissiveTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		return ModularGolems.loc("textures/equipments/sonic_cannon_emissive.png");
	}

	@Override
	public @Nullable ResourceLocation getPoseId() {
		return GolemModelPaths.BOW_MAINHAND;
	}

	@Override
	public @Nullable ResourceLocation getModelForHand(InteractionHand hand) {
		return hand == InteractionHand.MAIN_HAND ? GolemModelPaths.SONIC_MAINHAND : GolemModelPaths.SONIC_OFFHAND;
	}

}
