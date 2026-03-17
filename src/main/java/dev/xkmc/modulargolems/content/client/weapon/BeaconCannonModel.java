package dev.xkmc.modulargolems.content.client.weapon;

import dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPath;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.client.pose.BeaconConnonPose;
import dev.xkmc.modulargolems.content.client.pose.GolemShoulderPose;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import java.util.List;

public class BeaconCannonModel {

	public static final ModelLayerLocation BEACON_RIGHT = new ModelLayerLocation(ModularGolems.loc("beacon_cannon"), "right");
	public static final ModelLayerLocation BEACON_LEFT = new ModelLayerLocation(ModularGolems.loc("beacon_cannon"), "left");

	public static final AnimationDefinition RIGHT_START, RIGHT_ACTIVE, RIGHT_END,
			LEFT_START, LEFT_ACTIVE, LEFT_END;

	static {
		RIGHT_START = AnimationDefinition.Builder.withLength(0.25F)
				.addAnimation("flamethrower", new AnimationChannel(AnimationChannel.Targets.ROTATION,
						new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
						new Keyframe(0.25F, KeyframeAnimations.degreeVec(90F, -0, 0), AnimationChannel.Interpolations.LINEAR)
				))
				.addAnimation("flamethrower", new AnimationChannel(AnimationChannel.Targets.POSITION,
						new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
						new Keyframe(0.25F, KeyframeAnimations.posVec(-1.0F, 7.0F, -1.0F), AnimationChannel.Interpolations.LINEAR)
				)).build();
		LEFT_START = AnimationDefinition.Builder.withLength(0.25F)
				.addAnimation("flamethrower2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
						new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
						new Keyframe(0.25F, KeyframeAnimations.degreeVec(90F, -0, 0), AnimationChannel.Interpolations.LINEAR)
				))
				.addAnimation("flamethrower2", new AnimationChannel(AnimationChannel.Targets.POSITION,
						new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
						new Keyframe(0.25F, KeyframeAnimations.posVec(1.0F, 7.0F, -1.0F), AnimationChannel.Interpolations.LINEAR)
				))
				.build();
		RIGHT_ACTIVE = AnimationDefinition.Builder.withLength(0.0F).looping()
				.addAnimation("flamethrower", new AnimationChannel(AnimationChannel.Targets.ROTATION,
						new Keyframe(0.0F, KeyframeAnimations.degreeVec(90F, -0, 0), AnimationChannel.Interpolations.LINEAR)
				))
				.addAnimation("flamethrower", new AnimationChannel(AnimationChannel.Targets.POSITION,
						new Keyframe(0.0F, KeyframeAnimations.posVec(-1.0F, 7.0F, -1.0F), AnimationChannel.Interpolations.LINEAR)
				)).build();
		LEFT_ACTIVE = AnimationDefinition.Builder.withLength(0.0F).looping()
				.addAnimation("flamethrower2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
						new Keyframe(0.0F, KeyframeAnimations.degreeVec(90F, -0, 0), AnimationChannel.Interpolations.LINEAR)
				))
				.addAnimation("flamethrower2", new AnimationChannel(AnimationChannel.Targets.POSITION,
						new Keyframe(0.0F, KeyframeAnimations.posVec(1.0F, 7.0F, -1.0F), AnimationChannel.Interpolations.LINEAR)
				))
				.build();

		RIGHT_END = AnimationDefinition.Builder.withLength(0.25F)
				.addAnimation("flamethrower", new AnimationChannel(AnimationChannel.Targets.ROTATION,
						new Keyframe(0.0F, KeyframeAnimations.degreeVec(99.9904F, -0.0379F, 0.434F), AnimationChannel.Interpolations.LINEAR),
						new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
				))
				.addAnimation("flamethrower", new AnimationChannel(AnimationChannel.Targets.POSITION,
						new Keyframe(0.0F, KeyframeAnimations.posVec(-1.0F, 7.0F, -1.0F), AnimationChannel.Interpolations.LINEAR),
						new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
				)).build();
		LEFT_END = AnimationDefinition.Builder.withLength(0.25F)
				.addAnimation("flamethrower2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
						new Keyframe(0.0F, KeyframeAnimations.degreeVec(99.9904F, -0.0379F, 0.434F), AnimationChannel.Interpolations.LINEAR),
						new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
				))
				.addAnimation("flamethrower2", new AnimationChannel(AnimationChannel.Targets.POSITION,
						new Keyframe(0.0F, KeyframeAnimations.posVec(1.0F, 7.0F, -1.0F), AnimationChannel.Interpolations.LINEAR),
						new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
				))
				.build();
	}

	static {
		GolemModelPath.register(GolemModelPaths.BEACON_RIGHT,
				new GolemModelPath(BEACON_RIGHT, List.of(List.of("body", "flamethrower"))));

		GolemModelPath.register(GolemModelPaths.BEACON_LEFT,
				new GolemModelPath(BEACON_LEFT, List.of(List.of("body", "flamethrower2"))));

		GolemModelAnimations.register(GolemModelPaths.BEACON_RIGHT.withSuffix("_start"), RIGHT_START);
		GolemModelAnimations.register(GolemModelPaths.BEACON_RIGHT.withSuffix("_active"), RIGHT_ACTIVE);
		GolemModelAnimations.register(GolemModelPaths.BEACON_RIGHT.withSuffix("_end"), RIGHT_END);
		GolemModelAnimations.register(GolemModelPaths.BEACON_LEFT.withSuffix("_start"), LEFT_START);
		GolemModelAnimations.register(GolemModelPaths.BEACON_LEFT.withSuffix("_active"), LEFT_ACTIVE);
		GolemModelAnimations.register(GolemModelPaths.BEACON_LEFT.withSuffix("_end"), LEFT_END);

		GolemShoulderPose.register(GolemModelPaths.BEACON_RIGHT, new BeaconConnonPose("flamethrower", -1, 0, 0));
		GolemShoulderPose.register(GolemModelPaths.BEACON_LEFT, new BeaconConnonPose("flamethrower2", 1, 0, 0));
	}

	public static LayerDefinition leftLayer() {
		MeshDefinition meshdefinition = GolemEquipmentModels.buildGolemBaseLayers();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.getChild("body");

		PartDefinition flamethrower2 = body.addOrReplaceChild("flamethrower2", CubeListBuilder.create()
				.texOffs(0, 15).addBox(-2.0002F, -6.996F, 1.5454F, 4.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(20, 15).addBox(-1.0002F, -16.996F, 2.0454F, 2.0F, 10.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(24, 0).addBox(-2.0002F, 0.004F, -0.9546F, 4.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-2.5002F, 0.004F, 1.0454F, 5.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offset(6.0F, 5.0F, 4.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	public static LayerDefinition rightLayer() {
		MeshDefinition meshdefinition = GolemEquipmentModels.buildGolemBaseLayers();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.getChild("body");

		PartDefinition flamethrower = body.addOrReplaceChild("flamethrower", CubeListBuilder.create()
				.texOffs(0, 15).addBox(-2.0F, -7.0F, 1.5F, 4.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(20, 15).addBox(-1.0F, -17.0F, 2.0F, 2.0F, 10.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(24, 0).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-2.5F, 0.0F, 1.0F, 5.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-6.0F, 5.0F, 4.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

}
