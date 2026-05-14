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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

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
	public Identifier getModelTexture(MetalGolemEntity entity, ItemStack stack, HumanoidArm hand) {
		return ModularGolems.loc("textures/equipments/sonic_cannon.png");
	}

	@Override
	public Identifier getEmissiveTexture(MetalGolemEntity entity, ItemStack stack, HumanoidArm hand) {
		return ModularGolems.loc("textures/equipments/sonic_cannon_emissive.png");
	}

	@Override
	public @Nullable Identifier getPoseId() {
		return GolemModelPaths.BOW_MAINHAND;
	}

	@Override
	public @Nullable Identifier getModelForHand(HumanoidArm hand) {
		return hand == HumanoidArm.RIGHT ? GolemModelPaths.SONIC_MAINHAND : GolemModelPaths.SONIC_OFFHAND;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, TooltipDisplay disp, Consumer<Component> list, TooltipFlag flag) {
		list.accept(MGLangData.SONIC_CANNON.get());
		list.accept(MGLangData.SONIC_CANNON_RESONANCE.get());
		super.appendHoverText(stack, level, disp, list, flag);
	}
}
