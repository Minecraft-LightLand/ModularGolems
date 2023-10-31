package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xkmc.modulargolems.content.client.GolemEquipmentModels;
import dev.xkmc.modulargolems.content.client.pose.MetalGolemPose;
import dev.xkmc.modulargolems.content.client.pose.WeaponPose;
import dev.xkmc.modulargolems.content.entity.common.IGolemModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MetalGolemModel extends HierarchicalModel<MetalGolemEntity> implements IGolemModel<MetalGolemEntity, MetalGolemPartType, MetalGolemModel>, HeadedModel {

	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;
	private final ModelPart body;

	public final ModelPart rightArm;
	public final ModelPart leftArm;
	public final ModelPart leftForeArm;
	public final ModelPart rightForeArm;

	public MetalGolemModel(EntityModelSet set) {
		this(set.bakeLayer(GolemEquipmentModels.METALGOLEM));
	}

	public MetalGolemModel(ModelPart part) {
		this.root = part;
		this.body = part.getChild("body");
		this.head = part.getChild("head");
		this.rightArm = part.getChild("right_arm");
		this.leftArm = part.getChild("left_arm");
		this.rightLeg = part.getChild("right_leg");
		this.leftLeg = part.getChild("left_leg");
		this.leftForeArm = leftArm.getChild("left_forearm");
		this.rightForeArm = rightArm.getChild("right_forearm");
	}

	public ModelPart root() {
		return this.root;
	}

	public void copyFrom(MetalGolemModel other) {
		head.copyFrom(other.head);
		body.copyFrom(other.body);
		rightArm.copyFrom(other.rightArm);
		leftArm.copyFrom(other.leftArm);
		rightLeg.copyFrom(other.rightLeg);
		leftLeg.copyFrom(other.leftLeg);
		leftForeArm.copyFrom(other.leftForeArm);
		rightForeArm.copyFrom(other.rightForeArm);
	}

	public void setupAnim(MetalGolemEntity entity, float f1, float f2, float f3, float f4, float f5) {
		this.head.yRot = f4 * ((float) Math.PI / 180F);
		this.head.xRot = f5 * ((float) Math.PI / 180F);
		this.rightLeg.xRot = -1.5F * Mth.triangleWave(f1, 13.0F) * f2;
		this.leftLeg.xRot = 1.5F * Mth.triangleWave(f1, 13.0F) * f2;
		this.rightLeg.yRot = 0.0F;
		this.leftLeg.yRot = 0.0F;
	}

	public void prepareMobModel(MetalGolemEntity entity, float bob, float speed, float pTick) {
		MetalGolemPose pose = MetalGolemPose.DEFAULT;
		if (!entity.getMainHandItem().isEmpty()) {
			pose = WeaponPose.WEAPON;
		}
		int atkTick = entity.getAttackAnimationTick();
		if (atkTick > 0) {
			pose.attackModel(entity, this, atkTick - pTick);
		} else if (entity.isAggressive()) {
			pose.aggressive(entity, this, bob, speed, pTick);
		} else {
			pose.walking(entity, this, bob, speed, pTick);
		}
	}

	public void renderToBufferInternal(MetalGolemPartType type, PoseStack stack, VertexConsumer consumer, int i, int j, float f1, float f2, float f3, float f4) {
		if (type == MetalGolemPartType.BODY) {
			this.body.render(stack, consumer, i, j, f1, f2, f3, f4);
			this.head.render(stack, consumer, i, j, f1, f2, f3, f4);
		} else if (type == MetalGolemPartType.LEFT) {
			this.leftArm.render(stack, consumer, i, j, f1, f2, f3, f4);
		} else if (type == MetalGolemPartType.RIGHT) {
			this.rightArm.render(stack, consumer, i, j, f1, f2, f3, f4);
		} else if (type == MetalGolemPartType.LEG) {
			this.leftLeg.render(stack, consumer, i, j, f1, f2, f3, f4);
			this.rightLeg.render(stack, consumer, i, j, f1, f2, f3, f4);
		}
	}

	public ResourceLocation getTextureLocationInternal(ResourceLocation rl) {
		String id = rl.getNamespace();
		String mat = rl.getPath();
		return new ResourceLocation(id, "textures/entity/metal_golem/" + mat + ".png");
	}

	public void transformToHand(EquipmentSlot slot, PoseStack pose) {
		if (slot == EquipmentSlot.MAINHAND) {
			rightArm.translateAndRotate(pose);
			rightForeArm.translateAndRotate(pose);
		}
		if (slot == EquipmentSlot.OFFHAND) {
			leftArm.translateAndRotate(pose);
			leftForeArm.translateAndRotate(pose);
		}
	}

	@Override
	public ModelPart getHead() {
		return head;
	}
}