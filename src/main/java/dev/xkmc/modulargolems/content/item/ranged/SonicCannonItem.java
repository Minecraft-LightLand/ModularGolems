package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.client.weapon.IEntityModelWeapon;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.GolemEquipmentItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SonicCannonItem extends GolemEquipmentItem implements IEntityModelWeapon {

	public SonicCannonItem(Properties properties) {
		super(properties, EquipmentSlot.MAINHAND, GolemTypes.ENTITY_GOLEM::get, e -> {
		});
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity user) {
		return 72000;
	}

	@Override
	public boolean emissive() {
		return true;
	}

	@Override
	public Identifier getModelTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		return ModularGolems.loc("textures/equipments/sonic_cannon.png");
	}

	@Override
	public Identifier getEmissiveTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		return ModularGolems.loc("textures/equipments/sonic_cannon_emissive.png");
	}

	@Override
	public @Nullable Identifier getPoseId() {
		return GolemModelPaths.BOW_MAINHAND;
	}

	@Override
	public @Nullable Identifier getModelForHand(InteractionHand hand) {
		return hand == InteractionHand.MAIN_HAND ? GolemModelPaths.SONIC_MAINHAND : GolemModelPaths.SONIC_OFFHAND;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
		list.add(MGLangData.SONIC_CANNON.get());
		list.add(MGLangData.SONIC_CANNON_RESONANCE.get());
		super.appendHoverText(stack, level, list, flag);
	}
}
