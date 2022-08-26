package dev.xkmc.modulargolems.content.entity.humanoid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xkmc.modulargolems.content.entity.common.IGolemModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;

public class HumanoidGolemModel extends HumanoidModel<HumanoidGolemEntity> implements IGolemModel<HumanoidGolemEntity, HumaniodGolemPartType, HumanoidGolemModel> {

	public HumanoidGolemModel(EntityModelSet set) {
		this(set.bakeLayer(ModelLayers.PLAYER));
	}

	public HumanoidGolemModel(ModelPart modelPart) {
		super(modelPart);
	}

	@Override
	public void renderToBufferInternal(HumaniodGolemPartType type, PoseStack stack, VertexConsumer consumer, int i, int j, float f1, float f2, float f3, float f4) {
		if (type == HumaniodGolemPartType.BODY) {
			this.body.render(stack, consumer, i, j, f1, f2, f3, f4);
			this.head.render(stack, consumer, i, j, f1, f2, f3, f4);
			this.hat.render(stack, consumer, i, j, f1, f2, f3, f4);
		} else if (type == HumaniodGolemPartType.ARMS) {
			this.leftArm.render(stack, consumer, i, j, f1, f2, f3, f4);
			this.rightArm.render(stack, consumer, i, j, f1, f2, f3, f4);
		} else if (type == HumaniodGolemPartType.LEGS) {
			this.leftLeg.render(stack, consumer, i, j, f1, f2, f3, f4);
			this.rightLeg.render(stack, consumer, i, j, f1, f2, f3, f4);
		}
	}

	@Override
	public ResourceLocation getTextureLocationInternal(ResourceLocation rl) {
		String id = rl.getNamespace();
		String mat = rl.getPath();
		return new ResourceLocation(id, "textures/entity/humanoid_golem/" + mat + ".png");
	}

}
