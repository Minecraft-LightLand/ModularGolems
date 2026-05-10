package dev.xkmc.modulargolems.content.client.weapon;

import dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPath;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.client.pose.BowPose;
import dev.xkmc.modulargolems.content.client.pose.MetalGolemPose;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import java.util.List;

public class GolemBows {

	public static final ModelLayerLocation BOW_MAINHAND = new ModelLayerLocation(ModularGolems.loc("bow"), "mainhand");
	public static final ModelLayerLocation BOW_OFFHAND = new ModelLayerLocation(ModularGolems.loc("bow"), "offhand");

	public static final AnimationDefinition PULL_MAINHAND;

	static {
		PULL_MAINHAND = AnimationDefinition.Builder.withLength(0.5F)
				.addAnimation("bone", new AnimationChannel(AnimationChannel.Targets.ROTATION,
						new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
						new Keyframe(0.5F, KeyframeAnimations.degreeVec(32.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
				))
				.addAnimation("bone2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
						new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
						new Keyframe(0.5F, KeyframeAnimations.degreeVec(-32.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
				))
				.addAnimation("bone3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
						new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
						new Keyframe(0.5F, KeyframeAnimations.degreeVec(32.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
				))
				.addAnimation("bone4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
						new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
						new Keyframe(0.5F, KeyframeAnimations.degreeVec(-32.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
				))
				.addAnimation("bone4", new AnimationChannel(AnimationChannel.Targets.POSITION,
						new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
						new Keyframe(0.5F, KeyframeAnimations.posVec(0.0F, -1.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
				))
				.addAnimation("bone5", new AnimationChannel(AnimationChannel.Targets.POSITION,
						new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
						new Keyframe(0.5F, KeyframeAnimations.posVec(0.0F, 4.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
				))
				.addAnimation("bone5", new AnimationChannel(AnimationChannel.Targets.SCALE,
						new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
						new Keyframe(0.5F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 0.6F), AnimationChannel.Interpolations.LINEAR)
				))
				.addAnimation("bone6", new AnimationChannel(AnimationChannel.Targets.POSITION,
						new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
						new Keyframe(0.5F, KeyframeAnimations.posVec(0.0F, 4.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
				))
				.addAnimation("bone6", new AnimationChannel(AnimationChannel.Targets.SCALE,
						new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
						new Keyframe(0.5F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 0.6F), AnimationChannel.Interpolations.LINEAR)
				))
				.build();
	}

	static {
		GolemModelPath.register(GolemModelPaths.BOW_MAINHAND,
				new GolemModelPath(BOW_MAINHAND, List.of(List.of("right_arm", "crossbow"))));

		GolemModelPath.register(GolemModelPaths.BOW_OFFHAND,
				new GolemModelPath(BOW_OFFHAND, List.of(List.of("left_arm", "crossbow"))));

		GolemModelAnimations.register(GolemModelPaths.BOW_MAINHAND, PULL_MAINHAND);
		MetalGolemPose.register(GolemModelPaths.BOW_MAINHAND, BowPose.BOW);
	}

	public static LayerDefinition createMainHand() {
		MeshDefinition mesh = GolemEquipmentModels.buildGolemBaseLayers();
		PartDefinition partdefinition = mesh.getRoot();
		PartDefinition right_arm = mesh.getRoot().getChild("right_arm");
		PartDefinition crossbow = right_arm.addOrReplaceChild("crossbow", CubeListBuilder.create().texOffs(0, 44).addBox(-12.0F, -2.0F, -5.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 27.0F, 0.0F));

		PartDefinition cube_r1 = crossbow.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(44, 34).mirror().addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-11.0F, 4.0F, 3.0F, -2.7053F, 0.0F, 3.1416F));

		PartDefinition cube_r2 = crossbow.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(24, 44).addBox(-1.0F, -1.0F, -3.5F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, 5.0F, 0.0F, 2.7053F, 0.0F, 3.1416F));

		PartDefinition cube_r3 = crossbow.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(44, 34).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, 4.0F, -3.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition cube_r4 = crossbow.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(24, 44).addBox(-1.0F, -1.0F, -3.5F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, 5.0F, 0.0F, -0.4363F, 0.0F, 0.0F));

		PartDefinition cube_r5 = crossbow.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(24, 50).addBox(0.5F, -2.0F, -1.0F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, 6.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition bone = crossbow.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(-11.0F, -1.0F, 6.0F));

		PartDefinition cube_r6 = bone.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(44, 9).addBox(-1.5F, -1.0F, -3.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, -2.618F, 0.0F, 0.0F));

		PartDefinition cube_r7 = bone.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(44, 44).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 4.0F, -0.5236F, 0.0F, 0.0F));

		PartDefinition bone2 = crossbow.addOrReplaceChild("bone2", CubeListBuilder.create(), PartPose.offset(-11.0F, -1.0F, -6.0F));

		PartDefinition cube_r8 = bone2.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(44, 44).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -4.0F, 2.618F, 0.0F, -3.1416F));

		PartDefinition cube_r9 = bone2.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(44, 9).addBox(-2.5F, -1.0F, -3.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -1.0F, -1.0F, -0.5236F, 0.0F, 0.0F));

		PartDefinition bone5 = crossbow.addOrReplaceChild("bone5", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r10 = bone5.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, -10.0F, -12.0F, 0.0F, 22.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, -6.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		return LayerDefinition.create(mesh, 64, 64);
	}

	public static LayerDefinition createOffhand() {
		MeshDefinition mesh = GolemEquipmentModels.buildGolemBaseLayers();
		PartDefinition partdefinition = mesh.getRoot();
		PartDefinition left_arm = partdefinition.getChild("left_arm");

		PartDefinition crossbow2 = left_arm.addOrReplaceChild("crossbow", CubeListBuilder.create().texOffs(0, 44).addBox(-12.0F, -2.0F, -5.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(22.0F, 27.0F, 0.0F));

		PartDefinition cube_r11 = crossbow2.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(44, 34).mirror().addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-11.0F, 4.0F, 3.0F, -2.7053F, 0.0F, 3.1416F));

		PartDefinition cube_r12 = crossbow2.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(24, 44).addBox(-1.0F, -1.0F, -3.5F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, 5.0F, 0.0F, 2.7053F, 0.0F, 3.1416F));

		PartDefinition cube_r13 = crossbow2.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(44, 34).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, 4.0F, -3.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition cube_r14 = crossbow2.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(24, 44).addBox(-1.0F, -1.0F, -3.5F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, 5.0F, 0.0F, -0.4363F, 0.0F, 0.0F));

		PartDefinition cube_r15 = crossbow2.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(24, 50).addBox(0.5F, -2.0F, -1.0F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, 6.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition bone3 = crossbow2.addOrReplaceChild("bone3", CubeListBuilder.create(), PartPose.offset(-11.0F, -1.0F, 7.0F));

		PartDefinition cube_r16 = bone3.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(44, 9).addBox(-1.5F, -1.0F, -3.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, -2.618F, 0.0F, 0.0F));

		PartDefinition cube_r17 = bone3.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(44, 44).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 3.0F, -0.5236F, 0.0F, 0.0F));

		PartDefinition bone4 = crossbow2.addOrReplaceChild("bone4", CubeListBuilder.create(), PartPose.offset(-11.0F, -1.0F, -6.0F));

		PartDefinition cube_r18 = bone4.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(44, 44).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -4.0F, 2.618F, 0.0F, -3.1416F));

		PartDefinition cube_r19 = bone4.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(44, 9).addBox(-2.5F, -1.0F, -3.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -1.0F, -1.0F, -0.5236F, 0.0F, 0.0F));

		PartDefinition bone6 = crossbow2.addOrReplaceChild("bone6", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r20 = bone6.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, -10.0F, -12.0F, 0.0F, 22.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, -6.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		return LayerDefinition.create(mesh, 64, 64);
	}

}
