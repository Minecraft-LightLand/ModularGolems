package dev.xkmc.modulargolems.content.entity.humanoid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels;
import dev.xkmc.modulargolems.content.entity.render.IGolemModel;
import dev.xkmc.modulargolems.content.entity.render.IHeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public class HumanoidGolemModel extends HumanoidModel<HumanoidGolemRenderState> implements
		IGolemModel<HumanoidGolemEntity, HumanoidGolemRenderState, HumanoidGolemPartType, HumanoidGolemModel>, IHeadedModel {

	public HumanoidGolemModel(EntityModelSet set) {
		this(set.bakeLayer(GolemEquipmentModels.HUMANOID));
	}

	public HumanoidGolemModel(ModelPart modelPart) {
		super(modelPart);
	}

	@Override
	public void renderToBufferInternal(HumanoidGolemPartType type, Consumer<ModelPart> col) {
		if (type == HumanoidGolemPartType.BODY) {
			col.accept(body);
			col.accept(head);
		} else if (type == HumanoidGolemPartType.ARMS) {
			col.accept(leftArm);
			col.accept(rightArm);
		} else if (type == HumanoidGolemPartType.LEGS) {
			col.accept(leftLeg);
			col.accept(rightLeg);
		}
	}

	@Override
	public Identifier getTextureLocationInternal(Identifier rl) {
		return rl.withPath(e -> "textures/entity/humanoid_golem/" + e + ".png");
	}

	@Override
	public void setupAnim(HumanoidGolemRenderState state) {
		super.setupAnim(state);
		if (state.common().aggressive() && state.attackTime == 0.0F) {
			if (state.leftArmPose == ArmPose.ITEM) {
				this.leftArm.xRot = -1.8F;
			} else if (state.rightArmPose == ArmPose.ITEM) {
				this.rightArm.xRot = -1.8F;
			}
		}
	}

	@Override
	public void translateToHead(PoseStack pose) {
		pose.translate(0.0F, -0.25F, 0.0F);
		pose.mulPose(Axis.YP.rotationDegrees(180.0F));
		pose.scale(0.625F, -0.625F, -0.625F);
	}

}
