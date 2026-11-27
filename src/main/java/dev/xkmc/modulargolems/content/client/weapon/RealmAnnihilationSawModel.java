package dev.xkmc.modulargolems.content.client.weapon;

import dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPath;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import java.util.List;

/**
 * 歼灭刃锯的手臂模型：把 {@code 歼灭刃锯.java} 里双臂的圆锯几何挂到金属傀儡的
 * {@code right_arm}/{@code left_arm} 骨骼上，并用 {@code 刃锯动画.java}（原版 AnimationDefinition）
 * 让锯盘（bone / bone6）持续旋转。
 */
public class RealmAnnihilationSawModel {

	public static final ModelLayerLocation REALM_SAW_MAINHAND = new ModelLayerLocation(ModularGolems.loc("realm_annihilation_saw"), "mainhand");
	public static final ModelLayerLocation REALM_SAW_OFFHAND = new ModelLayerLocation(ModularGolems.loc("realm_annihilation_saw"), "offhand");

	public static final AnimationDefinition SPIN = AnimationDefinition.Builder.withLength(0.25F).looping()
			.addAnimation("bone", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.25F, KeyframeAnimations.degreeVec(-360.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("bone6", new AnimationChannel(AnimationChannel.Targets.ROTATION,
					new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.25F, KeyframeAnimations.degreeVec(-360.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
			.build();

	static {
		GolemModelPath.register(GolemModelPaths.SAW_MAINHAND, new GolemModelPath(REALM_SAW_MAINHAND, List.of(
				List.of("right_arm", "Circular saw"),
				List.of("right_arm", "Circular saw", "bone"),
				List.of("right_arm", "Circular saw", "bone", "bone2"),
				List.of("right_arm", "Circular saw", "bone", "bone2", "cube_r1"),
				List.of("right_arm", "Circular saw", "bone", "bone3"),
				List.of("right_arm", "Circular saw", "bone", "bone3", "cube_r2"),
				List.of("right_arm", "Circular saw", "bone", "bone4"),
				List.of("right_arm", "Circular saw", "bone", "bone4", "cube_r3"),
				List.of("right_arm", "Circular saw", "bone", "bone5"),
				List.of("right_arm", "Circular saw", "bone", "bone5", "cube_r4"))));
		GolemModelPath.register(GolemModelPaths.SAW_OFFHAND, new GolemModelPath(REALM_SAW_OFFHAND, List.of(
				List.of("left_arm", "Circular_saw2"),
				List.of("left_arm", "Circular_saw2", "bone6"),
				List.of("left_arm", "Circular_saw2", "bone6", "bone7"),
				List.of("left_arm", "Circular_saw2", "bone6", "bone7", "cube_r5"),
				List.of("left_arm", "Circular_saw2", "bone6", "bone8"),
				List.of("left_arm", "Circular_saw2", "bone6", "bone8", "cube_r6"),
				List.of("left_arm", "Circular_saw2", "bone6", "bone9"),
				List.of("left_arm", "Circular_saw2", "bone6", "bone9", "cube_r7"),
				List.of("left_arm", "Circular_saw2", "bone6", "bone10"),
				List.of("left_arm", "Circular_saw2", "bone6", "bone10", "cube_r8"))));
		GolemModelAnimations.register(GolemModelPaths.SAW_MAINHAND, SPIN);
		GolemModelAnimations.register(GolemModelPaths.SAW_OFFHAND, SPIN);
	}

	public static LayerDefinition createMainHand() {
		MeshDefinition mesh = GolemEquipmentModels.buildGolemBaseLayers();
		PartDefinition right_arm = mesh.getRoot().getChild("right_arm");
		PartDefinition saw = right_arm.addOrReplaceChild("Circular saw", CubeListBuilder.create()
						.texOffs(0, 0).addBox(-13.5F, -12.0F, -3.5F, 5.0F, 10.0F, 7.0F, new CubeDeformation(-0.1F)),
				PartPose.offset(0.0F, 31.0F, 0.0F));
		PartDefinition bone = saw.addOrReplaceChild("bone", CubeListBuilder.create()
						.texOffs(0, 17).addBox(-1.0F, -3.0F, -3.0F, 2.0F, 6.0F, 6.0F, new CubeDeformation(0.2F)),
				PartPose.offsetAndRotation(-11.0F, -6.0F, 0.0F, 2.3562F, 0.0F, 3.1416F));
		PartDefinition bone2 = bone.addOrReplaceChild("bone2", CubeListBuilder.create()
						.texOffs(24, 0).addBox(-1.0F, -2.5F, -5.0F, 2.0F, 5.0F, 5.0F),
				PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.0F, 0.0F, 0.2182F));
		bone2.addOrReplaceChild("cube_r1", CubeListBuilder.create()
						.texOffs(30, 34).addBox(-1.0F, -4.3807F, -0.57F, 2.0F, 5.0F, 3.0F, new CubeDeformation(-0.2F)),
				PartPose.offsetAndRotation(0.0F, -2.0F, -4.5F, -0.3491F, 0.0F, 0.0F));
		PartDefinition bone3 = bone.addOrReplaceChild("bone3", CubeListBuilder.create()
						.texOffs(16, 27).addBox(-1.0F, 0.0F, -2.5F, 2.0F, 5.0F, 5.0F),
				PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, -0.2182F, 0.0F));
		bone3.addOrReplaceChild("cube_r2", CubeListBuilder.create()
						.texOffs(30, 18).addBox(-1.0F, -2.45F, -4.3807F, 2.0F, 3.0F, 5.0F, new CubeDeformation(-0.2F)),
				PartPose.offsetAndRotation(0.0F, 4.5F, -2.0F, -0.3491F, 0.0F, 0.0F));
		PartDefinition bone4 = bone.addOrReplaceChild("bone4", CubeListBuilder.create()
						.texOffs(0, 29).addBox(-1.0F, -2.5F, 0.0F, 2.0F, 5.0F, 5.0F),
				PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.0F, 0.0F, -0.2182F));
		bone4.addOrReplaceChild("cube_r3", CubeListBuilder.create()
						.texOffs(30, 26).addBox(-1.0F, -0.6193F, -3.49F, 2.0F, 5.0F, 3.0F, new CubeDeformation(-0.2F)),
				PartPose.offsetAndRotation(0.0F, 2.0F, 5.5F, -0.3491F, 0.0F, 0.0F));
		PartDefinition bone5 = bone.addOrReplaceChild("bone5", CubeListBuilder.create()
						.texOffs(16, 17).addBox(-1.0F, -5.0F, -2.5F, 2.0F, 5.0F, 5.0F),
				PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.2182F, 0.0F));
		bone5.addOrReplaceChild("cube_r4", CubeListBuilder.create()
						.texOffs(30, 10).addBox(-1.0F, -0.54F, -0.6193F, 2.0F, 3.0F, 5.0F, new CubeDeformation(-0.25F)),
				PartPose.offsetAndRotation(0.0F, -4.5F, 2.0F, -0.3491F, 0.0F, 0.0F));
		return LayerDefinition.create(mesh, 64, 64);
	}

	public static LayerDefinition createOffhand() {
		MeshDefinition mesh = GolemEquipmentModels.buildGolemBaseLayers();
		PartDefinition left_arm = mesh.getRoot().getChild("left_arm");
		PartDefinition saw2 = left_arm.addOrReplaceChild("Circular_saw2", CubeListBuilder.create()
						.texOffs(0, 0).addBox(-2.5F, -6.0F, -3.5F, 5.0F, 10.0F, 7.0F, new CubeDeformation(-0.1F)),
				PartPose.offset(11.0F, 25.0F, 0.0F));
		PartDefinition bone6 = saw2.addOrReplaceChild("bone6", CubeListBuilder.create()
						.texOffs(0, 17).addBox(-1.0F, -3.0F, -3.0F, 2.0F, 6.0F, 6.0F, new CubeDeformation(0.2F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.3562F, 0.0F, 3.1416F));
		PartDefinition bone7 = bone6.addOrReplaceChild("bone7", CubeListBuilder.create()
						.texOffs(24, 0).addBox(-1.0F, -2.5F, -5.0F, 2.0F, 5.0F, 5.0F),
				PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.0F, 0.0F, 0.2182F));
		bone7.addOrReplaceChild("cube_r5", CubeListBuilder.create()
						.texOffs(30, 34).addBox(-1.0F, -4.3807F, -0.57F, 2.0F, 5.0F, 3.0F, new CubeDeformation(-0.2F)),
				PartPose.offsetAndRotation(0.0F, -2.0F, -4.5F, -0.3491F, 0.0F, 0.0F));
		PartDefinition bone8 = bone6.addOrReplaceChild("bone8", CubeListBuilder.create()
						.texOffs(16, 27).addBox(-1.0F, 0.0F, -2.5F, 2.0F, 5.0F, 5.0F),
				PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, -0.2182F, 0.0F));
		bone8.addOrReplaceChild("cube_r6", CubeListBuilder.create()
						.texOffs(30, 18).addBox(-1.0F, -2.45F, -4.3807F, 2.0F, 3.0F, 5.0F, new CubeDeformation(-0.2F)),
				PartPose.offsetAndRotation(0.0F, 4.5F, -2.0F, -0.3491F, 0.0F, 0.0F));
		PartDefinition bone9 = bone6.addOrReplaceChild("bone9", CubeListBuilder.create()
						.texOffs(0, 29).addBox(-1.0F, -2.5F, 0.0F, 2.0F, 5.0F, 5.0F),
				PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.0F, 0.0F, -0.2182F));
		bone9.addOrReplaceChild("cube_r7", CubeListBuilder.create()
						.texOffs(30, 26).addBox(-1.0F, -0.6193F, -3.49F, 2.0F, 5.0F, 3.0F, new CubeDeformation(-0.2F)),
				PartPose.offsetAndRotation(0.0F, 2.0F, 5.5F, -0.3491F, 0.0F, 0.0F));
		PartDefinition bone10 = bone6.addOrReplaceChild("bone10", CubeListBuilder.create()
						.texOffs(16, 17).addBox(-1.0F, -5.0F, -2.5F, 2.0F, 5.0F, 5.0F),
				PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.2182F, 0.0F));
		bone10.addOrReplaceChild("cube_r8", CubeListBuilder.create()
						.texOffs(30, 10).addBox(-1.0F, -0.54F, -0.6193F, 2.0F, 3.0F, 5.0F, new CubeDeformation(-0.25F)),
				PartPose.offsetAndRotation(0.0F, -4.5F, 2.0F, -0.3491F, 0.0F, 0.0F));
		return LayerDefinition.create(mesh, 64, 64);
	}

}
