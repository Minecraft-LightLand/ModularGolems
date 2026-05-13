package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels;
import dev.xkmc.modulargolems.content.client.pose.MetalGolemPose;
import dev.xkmc.modulargolems.content.client.pose.WeaponPose;
import dev.xkmc.modulargolems.content.client.weapon.IEntityModelWeapon;
import dev.xkmc.modulargolems.content.entity.render.IGolemModel;
import dev.xkmc.modulargolems.content.entity.render.IHeadedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class MetalGolemModel extends EntityModel<MetalGolemRenderState> implements IGolemModel<
		MetalGolemEntity, MetalGolemRenderState, MetalGolemPartType, MetalGolemModel>, IHeadedModel {

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
		super(part);
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

	@Override
	public void setupAnim(MetalGolemRenderState state) {
		root.resetPose();

		MetalGolemPose pose = MetalGolemPose.DEFAULT;
		ItemStack stack = state.getMainHandItemStack();
		if (!stack.isEmpty()) {
			pose = WeaponPose.WEAPON;
			if (stack.getItem() instanceof IEntityModelWeapon weapon) {
				var id = weapon.getPoseId();
				if (id != null && MetalGolemPose.MAP.containsKey(id))
					pose = MetalGolemPose.MAP.get(id);
			}
		}
		float atkTick = state.attackTicksRemaining;
		float animationSpeed = state.walkAnimationSpeed;
		float animationPos = state.walkAnimationPos;
		if (atkTick > 0) {
			pose.attackModel(state, this, atkTick);
		} else if (state.common().aggressive()) {
			pose.aggressive(state, this, animationPos, animationSpeed);
		} else {
			pose.walking(state, this, animationPos, animationSpeed);
		}

		this.head.yRot = state.yRot * (float) (Math.PI / 180.0);
		this.head.xRot = state.xRot * (float) (Math.PI / 180.0);
		this.rightLeg.xRot = -1.5F * Mth.triangleWave(animationPos, 13.0F) * animationSpeed;
		this.leftLeg.xRot = 1.5F * Mth.triangleWave(animationPos, 13.0F) * animationSpeed;
		this.rightLeg.yRot = 0.0F;
		this.leftLeg.yRot = 0.0F;
		this.rightLeg.zRot = 0.0F;
		this.leftLeg.zRot = 0.0F;
		if (state.isPassenger) {
			this.rightLeg.xRot = -1.4137167F;
			this.rightLeg.yRot = ((float) Math.PI / 10F);
			this.rightLeg.zRot = 0.07853982F;
			this.leftLeg.xRot = -1.4137167F;
			this.leftLeg.yRot = (-(float) Math.PI / 10F);
			this.leftLeg.zRot = -0.07853982F;
		}

	}

	public void prepareMobModel(MetalGolemRenderState entity, float bob, float speed, float pTick) {

	}

	public void renderToBufferInternal(MetalGolemPartType type, Consumer<ModelPart> col) {
		if (type == MetalGolemPartType.BODY) {
			col.accept(body);
			col.accept(head);
		} else if (type == MetalGolemPartType.LEFT) {
			col.accept(leftArm);
		} else if (type == MetalGolemPartType.RIGHT) {
			col.accept(rightArm);
		} else if (type == MetalGolemPartType.LEG) {
			col.accept(leftLeg);
			col.accept(rightLeg);
		}
	}

	public Identifier getTextureLocationInternal(Identifier rl) {
		return rl.withPath(e -> "textures/entity/metal_golem/" + e + ".png");
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

	public void translateToHead(PoseStack pose) {
		pose.translate(0.0F, -0.45F, -0.08F);
		pose.mulPose(Axis.YP.rotationDegrees(180.0F));
		pose.scale(0.625F, -0.625F, -0.625F);
	}

}