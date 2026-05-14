package dev.xkmc.modulargolems.content.entity.metalgolem;

import dev.xkmc.modulargolems.content.entity.render.AbstractGolemRenderState;
import dev.xkmc.modulargolems.content.entity.render.CommonGolemRenderState;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class MetalGolemRenderState extends HumanoidRenderState implements AbstractGolemRenderState<
		MetalGolemEntity, MetalGolemRenderState, MetalGolemPartType> {

	public Crackiness.Level crackiness;

	public float attackTicksRemaining;

	public MetalGolemAimState aim;

	public CommonGolemRenderState common;

	public ItemStack rightShoulderItem, leftShoulderItem;

	@Nullable
	public ItemStackRenderState beacon;

	@Nullable
	public MetalGolemWeaponModelState rightWeaponState, leftWeaponState;

	@Nullable
	public MetalGolemShoulderModelState rightShoulderState, leftShoulderState;

	public MetalGolemModelState model;

	@Override
	public CommonGolemRenderState common() {
		return common;
	}

	public void update(MetalGolemEntity entity, float pt, ItemModelResolver imr) {
		ArmedEntityRenderState.extractArmedEntityRenderState(entity, this, imr, pt);
		headEquipment = entity.getItemBySlot(EquipmentSlot.HEAD);
		chestEquipment = entity.getItemBySlot(EquipmentSlot.CHEST);
		legsEquipment = entity.getItemBySlot(EquipmentSlot.LEGS);
		feetEquipment = entity.getItemBySlot(EquipmentSlot.FEET);
		common = CommonGolemRenderState.of(entity, imr, pt);
		crackiness = entity.getCrackiness();
		attackTicksRemaining = entity.getAttackAnimationTick() > 0.0F ? entity.getAttackAnimationTick() - pt : 0.0F;
		aim = MetalGolemAimState.of(entity, pt);
		if (entity.isAddedToLevel() && entity.getItemBySlot(EquipmentSlot.FEET).is(GolemItems.BEACON_BOOTS)) {
			beacon = new ItemStackRenderState();
			imr.updateForLiving(beacon, Items.BEACON.getDefaultInstance(), ItemDisplayContext.NONE, entity);
		}
		model = MetalGolemModelState.of(entity);
		rightWeaponState = MetalGolemWeaponModelState.of(entity, rightHandItemStack, HumanoidArm.RIGHT, pt);
		leftWeaponState = MetalGolemWeaponModelState.of(entity, leftHandItemStack, HumanoidArm.LEFT, pt);
		rightShoulderItem = entity.getRightShoulder().getItem();
		leftShoulderItem = entity.getLeftShoulder().getItem();
		rightShoulderState = MetalGolemShoulderModelState.of(entity, rightShoulderItem, HumanoidArm.RIGHT, pt);
		leftShoulderState = MetalGolemShoulderModelState.of(entity, leftShoulderItem, HumanoidArm.LEFT, pt);
	}

}
