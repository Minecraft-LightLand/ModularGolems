package dev.xkmc.modulargolems.content.entity.humanoid;

import dev.xkmc.modulargolems.content.entity.render.AbstractGolemRenderState;
import dev.xkmc.modulargolems.content.entity.render.CommonGolemRenderState;
import dev.xkmc.modulargolems.content.entity.skin.ClientSkinDispatch;
import dev.xkmc.modulargolems.content.entity.skin.SpecialRenderSkin;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.EquipmentSlot;
import org.jspecify.annotations.Nullable;

public class HumanoidGolemRenderState extends HumanoidRenderState implements AbstractGolemRenderState<
		HumanoidGolemEntity, HumanoidGolemRenderState, HumanoidGolemPartType> {

	public @Nullable SpecialRenderSkin skinProfile;

	public CommonGolemRenderState common;

	@Override
	public CommonGolemRenderState common() {
		return common;
	}

	@Override
	public void update(HumanoidGolemEntity entity, float pt, ItemModelResolver imr) {
		common = CommonGolemRenderState.of(entity, imr, pt);
		skinProfile = ClientSkinDispatch.get(this);
	}

}
