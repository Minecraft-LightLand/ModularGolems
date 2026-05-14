package dev.xkmc.modulargolems.content.entity.metalgolem;

import dev.xkmc.modulargolems.content.entity.render.AbstractGolemRenderState;
import dev.xkmc.modulargolems.content.entity.render.CommonGolemRenderState;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.entity.EquipmentSlot;

public class MetalGolemRenderState extends HumanoidRenderState implements AbstractGolemRenderState<
		MetalGolemEntity, MetalGolemRenderState, MetalGolemPartType> {

	public Crackiness.Level crackiness;

	public float attackTicksRemaining;

	public MetalGolemAimState aim;

	public CommonGolemRenderState common;

	public boolean renderBeacon;

	@Override
	public CommonGolemRenderState common() {
		return common;
	}

	public void update(MetalGolemEntity entity, float pt, ItemModelResolver imr) {
		common = CommonGolemRenderState.of(entity, imr);
		crackiness = entity.getCrackiness();
		attackTicksRemaining = entity.getAttackAnimationTick() > 0.0F ? entity.getAttackAnimationTick() - pt : 0.0F;
		aim = MetalGolemAimState.of(entity);
		renderBeacon = entity.isAddedToLevel() && entity.getItemBySlot(EquipmentSlot.FEET).is(GolemItems.BEACON_BOOTS);
	}

}
