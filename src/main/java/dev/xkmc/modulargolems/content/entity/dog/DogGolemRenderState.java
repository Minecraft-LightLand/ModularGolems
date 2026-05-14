package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.modulargolems.content.entity.render.AbstractGolemRenderState;
import dev.xkmc.modulargolems.content.entity.render.CommonGolemRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.item.ItemStack;

public class DogGolemRenderState extends LivingEntityRenderState implements AbstractGolemRenderState<
		DogGolemEntity, DogGolemRenderState, DogGolemPartType> {

	public CommonGolemRenderState common;
	public ItemStack bodyArmorItem;

	@Override
	public CommonGolemRenderState common() {
		return common;
	}

	public void update(DogGolemEntity entity, float pt, ItemModelResolver imr) {
		common = CommonGolemRenderState.of(entity, imr, pt);
		bodyArmorItem = entity.getBodyArmorItem();
	}

}
