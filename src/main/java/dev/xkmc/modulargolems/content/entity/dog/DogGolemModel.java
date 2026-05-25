package dev.xkmc.modulargolems.content.entity.dog;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels;
import dev.xkmc.modulargolems.content.entity.render.IGolemModel;
import dev.xkmc.modulargolems.content.entity.render.IHeadedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class DogGolemModel extends EntityModel<DogGolemRenderState> implements IGolemModel<
		DogGolemEntity, DogGolemRenderState, DogGolemPartType, DogGolemModel>, IHeadedModel {

	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart tail;
	private final ModelPart upperBody;

	public DogGolemModel(EntityModelSet set) {
		this(set.bakeLayer(GolemEquipmentModels.DOGGOLEM));
	}

	public DogGolemModel(ModelPart part) {
		super(part);
		this.root = part;
		this.head = part.getChild("head");
		this.body = part.getChild("body");
		this.upperBody = part.getChild("upper_body");
		this.rightHindLeg = part.getChild("right_hind_leg");
		this.leftHindLeg = part.getChild("left_hind_leg");
		this.rightFrontLeg = part.getChild("right_front_leg");
		this.leftFrontLeg = part.getChild("left_front_leg");
		this.tail = part.getChild("tail");
	}

	public void setupAnim(DogGolemRenderState state) {
		super.setupAnim(state);
		float animationPos = state.walkAnimationPos;
		float animationSpeed = state.walkAnimationSpeed;
		if (state.common.aggressive()) {
			this.tail.yRot = 0.0F;
		} else {
			this.tail.yRot = Mth.cos(animationPos * 0.6662F) * 1.4F * animationSpeed;
		}

		if (state.isSitting) {
			this.setSittingPose(state);
		} else {
			this.rightHindLeg.xRot = Mth.cos(animationPos * 0.6662F) * 1.4F * animationSpeed;
			this.leftHindLeg.xRot = Mth.cos(animationPos * 0.6662F + (float) Math.PI) * 1.4F * animationSpeed;
			this.rightFrontLeg.xRot = Mth.cos(animationPos * 0.6662F + (float) Math.PI) * 1.4F * animationSpeed;
			this.leftFrontLeg.xRot = Mth.cos(animationPos * 0.6662F) * 1.4F * animationSpeed;
		}
		this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
		this.head.yRot = state.yRot * (float) (Math.PI / 180.0);
		this.tail.xRot = state.tailAngle;
	}

	protected void setSittingPose(DogGolemRenderState state) {
		float ageScale = state.ageScale;
		this.body.y += 4.0F * ageScale;
		this.body.z -= 2.0F * ageScale;
		this.body.xRot = (float) (Math.PI / 4);
		this.tail.y += 9.0F * ageScale;
		this.tail.z -= 2.0F * ageScale;
		this.rightHindLeg.y += 6.7F * ageScale;
		this.rightHindLeg.z -= 5.0F * ageScale;
		this.rightHindLeg.xRot = (float) (Math.PI * 3.0 / 2.0);
		this.leftHindLeg.y += 6.7F * ageScale;
		this.leftHindLeg.z -= 5.0F * ageScale;
		this.leftHindLeg.xRot = (float) (Math.PI * 3.0 / 2.0);
		this.rightFrontLeg.xRot = 5.811947F;
		this.rightFrontLeg.x += 0.01F * ageScale;
		this.rightFrontLeg.y += 1.0F * ageScale;
		this.leftFrontLeg.xRot = 5.811947F;
		this.leftFrontLeg.x -= 0.01F * ageScale;
		this.leftFrontLeg.y += 1.0F * ageScale;
		this.upperBody.y += 2.0F;
		this.upperBody.xRot = (float) (Math.PI * 2.0 / 5.0);
		this.upperBody.yRot = 0.0F;
	}

	@Override
	public void iterateParts(DogGolemPartType type, Consumer<ModelPart> col) {
		if (type == DogGolemPartType.BODY) {
			col.accept(body);
			col.accept(head);
			col.accept(upperBody);
			col.accept(tail);
		} else if (type == DogGolemPartType.LEGS) {
			col.accept(leftHindLeg);
			col.accept(rightHindLeg);
			col.accept(leftFrontLeg);
			col.accept(rightFrontLeg);
		}
	}

	public Identifier getTextureLocationInternal(Identifier rl) {
		return rl.withPath(e -> "textures/entity/dog_golem/" + e + ".png");
	}

	@Override
	public void translateToHead(PoseStack pose) {
		//head.translateAndRotate(pose);
		pose.mulPose(Axis.YP.rotationDegrees(180.0F));
		pose.scale(0.625F, -0.625F, -0.625F);
	}

	@Override
	public ModelPart getHead() {
		return head;
	}

}