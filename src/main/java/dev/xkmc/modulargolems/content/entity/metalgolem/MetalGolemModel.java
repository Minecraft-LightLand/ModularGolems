package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MetalGolemModel<T extends MetalGolemEntity> extends HierarchicalModel<T> {

	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;
	private final ModelPart body;

	public MetalGolemModel(ModelPart part) {
		this.root = part;
		this.body = part.getChild("body");
		this.head = part.getChild("head");
		this.rightArm = part.getChild("right_arm");
		this.leftArm = part.getChild("left_arm");
		this.rightLeg = part.getChild("right_leg");
		this.leftLeg = part.getChild("left_leg");
	}

	public ModelPart root() {
		return this.root;
	}

	public void setupAnim(T entity, float f1, float f2, float f3, float f4, float f5) {
		this.head.yRot = f4 * ((float) Math.PI / 180F);
		this.head.xRot = f5 * ((float) Math.PI / 180F);
		this.rightLeg.xRot = -1.5F * Mth.triangleWave(f1, 13.0F) * f2;
		this.leftLeg.xRot = 1.5F * Mth.triangleWave(f1, 13.0F) * f2;
		this.rightLeg.yRot = 0.0F;
		this.leftLeg.yRot = 0.0F;
	}

	public void prepareMobModel(T entity, float f1, float f2, float f3) {
		int i = entity.getAttackAnimationTick();
		if (i > 0) {
			this.rightArm.xRot = -2.0F + 1.5F * Mth.triangleWave((float) i - f3, 10.0F);
			this.leftArm.xRot = -2.0F + 1.5F * Mth.triangleWave((float) i - f3, 10.0F);
		} else {
			this.rightArm.xRot = (-0.2F + 1.5F * Mth.triangleWave(f1, 13.0F)) * f2;
			this.leftArm.xRot = (-0.2F - 1.5F * Mth.triangleWave(f1, 13.0F)) * f2;
		}
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int i, int j, float f1, float f2, float f3, float f4) {
	}

	void renderToBufferInternal(PartType type, PoseStack stack, VertexConsumer consumer, int i, int j, float f1, float f2, float f3, float f4) {
		if (type == PartType.BODY) {
			this.body.render(stack, consumer, i, j, f1, f2, f3, f4);
			this.head.render(stack, consumer, i, j, f1, f2, f3, f4);
		} else if (type == PartType.LEFT) {
			this.leftArm.render(stack, consumer, i, j, f1, f2, f3, f4);
		} else if (type == PartType.RIGHT) {
			this.leftArm.render(stack, consumer, i, j, f1, f2, f3, f4);
		} else if (type == PartType.LEG) {
			this.leftLeg.render(stack, consumer, i, j, f1, f2, f3, f4);
			this.rightLeg.render(stack, consumer, i, j, f1, f2, f3, f4);
		}
	}


}