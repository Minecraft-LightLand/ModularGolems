package dev.xkmc.modulargolems.content.client.armor;

import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels.buildGolemBaseLayers;
import static dev.xkmc.modulargolems.content.client.armor.GolemModelPaths.*;

public class IgnisArmors {


	public static final ModelLayerLocation HELMET_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "ignis_helmet"), "main");
	public static final ModelLayerLocation CHESTPLATE_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "ignis_chestplate"), "main");
	public static final ModelLayerLocation SHINGUARD_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "ignis_shinguard"), "main");

	static {
		GolemModelPath.register(IGNIS_HELMETS,
				new GolemModelPath(HELMET_LAYER, List.of(
						List.of("head", "head1"),
						List.of("head", "head1", "right_helmet"),
						List.of("head", "head1", "left_helmet"),
						List.of("head", "head1", "head_plate"),
						List.of("head", "head1", "right_horn"),
						List.of("head", "head1", "right_horn", "right_horn2"),
						List.of("head", "head1", "right_horn", "right_horn2", "right_horn3"),
						List.of("head", "head1", "left_horn"),
						List.of("head", "head1", "left_horn", "left_horn2"),
						List.of("head", "head1", "left_horn", "left_horn2", "left_horn3")
				)));

		GolemModelPath.register(IGNIS_CHESTPLATES,
				new GolemModelPath(CHESTPLATE_LAYER, List.of(
						List.of("body", "body1"),
						List.of("body", "body1", "cube_r1"),
						List.of("body", "body1", "cube_r2"),

						List.of("right_arm", "body2"),
						List.of("right_arm", "body2", "cube_r3"),
						List.of("right_arm", "body2", "cube_r4"),
						List.of("right_arm", "body2", "cube_r5"),

						List.of("left_arm", "body3"),
						List.of("left_arm", "body3", "cube_r6"),
						List.of("left_arm", "body3", "cube_r7"),
						List.of("left_arm", "body3", "cube_r8")
				)));

		GolemModelPath.register(IGNIS_LEGGINGS,
				new GolemModelPath(SHINGUARD_LAYER, List.of(
						List.of("body", "legs1"),
						List.of("right_leg", "legs2"),
						List.of("left_leg", "legs3")
				)));
	}

	public static LayerDefinition createHelmet() {
		MeshDefinition mesh = buildGolemBaseLayers();

		PartDefinition head = mesh.getRoot().getChild("head");

		PartDefinition head1 = head.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(0, 50).addBox(-4.0F, -8.0F, -4.5F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, -4.5F, -1.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition right_helmet = head1.addOrReplaceChild("right_helmet", CubeListBuilder.create().texOffs(88, 93).addBox(-0.5F, 1.5F, -4.0F, 0.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, -3.0F, -4.5F, 0.0F, -0.829F, 0.0F));

		PartDefinition left_helmet = head1.addOrReplaceChild("left_helmet", CubeListBuilder.create().texOffs(94, 75).addBox(0.5F, 1.5F, -4.0F, 0.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, -3.0F, -4.5F, 0.0F, 0.829F, 0.0F));

		PartDefinition head_plate = head1.addOrReplaceChild("head_plate", CubeListBuilder.create().texOffs(24, 76).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, -4.0F, -0.2618F, 0.0F, 0.0F));

		PartDefinition right_horn = head1.addOrReplaceChild("right_horn", CubeListBuilder.create().texOffs(24, 68).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -7.5F, -3.5F, 0.3927F, 0.2182F, -0.1309F));

		PartDefinition right_horn2 = right_horn.addOrReplaceChild("right_horn2", CubeListBuilder.create().texOffs(32, 97).addBox(-0.5F, -7.0F, 0.0F, 1.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, -1.0F, -1.3526F, 0.0F, 0.0F));

		PartDefinition right_horn3 = right_horn2.addOrReplaceChild("right_horn3", CubeListBuilder.create().texOffs(62, 19).addBox(-0.5F, -0.01F, -3.99F, 1.0F, 1.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, -6.99F, 1.99F, -0.5236F, 0.0F, 0.0F));

		PartDefinition left_horn = head1.addOrReplaceChild("left_horn", CubeListBuilder.create().texOffs(24, 97).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, -7.5F, -3.5F, 0.3927F, -0.2182F, 0.1309F));

		PartDefinition left_horn2 = left_horn.addOrReplaceChild("left_horn2", CubeListBuilder.create().texOffs(38, 97).addBox(-0.5F, -7.0F, 0.0F, 1.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, -1.0F, -1.3526F, 0.0F, 0.0F));

		PartDefinition left_horn3 = left_horn2.addOrReplaceChild("left_horn3", CubeListBuilder.create().texOffs(92, 13).addBox(-0.5F, -0.01F, -3.99F, 1.0F, 1.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, -6.99F, 1.99F, -0.5236F, 0.0F, 0.0F));


		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createChestplate() {
		MeshDefinition mesh = buildGolemBaseLayers();

		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_arm = mesh.getRoot().getChild("right_arm");
		PartDefinition left_arm = mesh.getRoot().getChild("left_arm");

		PartDefinition body1 = body.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 0).addBox(-9.5F, -33.5F, -6.5F, 19.0F, 13.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(70, 51).addBox(-3.0F, -33.0F, -8.0F, 6.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition cube_r1 = body1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(38, 25).addBox(-1.0F, -1.5F, -1.0F, 6.0F, 12.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -33.0F, -6.0F, 0.0F, 0.0F, 0.1309F));

		PartDefinition cube_r2 = body1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 25).addBox(-5.0F, -1.5F, -1.0F, 6.0F, 12.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -33.0F, -6.0F, 0.0F, 0.0F, -0.1309F));

		PartDefinition body2 = right_arm.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(86, 63).addBox(-13.5F, -26.0F, -3.5F, 5.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(76, 19).addBox(-23.0F, -41.0F, 0.0F, 12.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(62, 62).addBox(-13.5F, -15.0F, -3.5F, 5.0F, 12.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition cube_r3 = body2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(92, 0).addBox(-2.0F, -2.0F, -4.5F, 3.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.0F, -18.0F, 1.0F, 0.0F, 0.0F, -0.1309F));

		PartDefinition cube_r4 = body2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(24, 81).addBox(-1.0F, -2.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-15.0F, -12.0F, 0.0F, 0.0F, 0.0F, -0.3491F));

		PartDefinition cube_r5 = body2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(62, 0).addBox(0.0F, -3.0F, -4.0F, 7.0F, 11.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-15.0F, -33.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

		PartDefinition body3 = left_arm.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(0, 87).addBox(-14.5F, -26.0F, -3.5F, 5.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(76, 35).addBox(-12.0F, -41.0F, 0.0F, 12.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(0, 68).addBox(-14.5F, -15.0F, -3.5F, 5.0F, 12.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(23.0F, 31.0F, 0.0F));

		PartDefinition cube_r6 = body3.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(68, 93).addBox(-2.0F, -2.0F, -4.5F, 3.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, -18.0F, 1.0F, 0.0F, 0.0F, 0.1309F));

		PartDefinition cube_r7 = body3.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(46, 81).addBox(-1.0F, -2.0F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, -12.0F, 0.0F, 0.0F, 0.0F, 0.3491F));

		PartDefinition cube_r8 = body3.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(32, 62).addBox(-0.4F, -4.5F, -4.0F, 7.0F, 11.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-14.0F, -30.0F, 0.0F, 0.0F, 0.0F, -0.2618F));


		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createLeggings() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
		PartDefinition left_leg = mesh.getRoot().getChild("left_leg");

		PartDefinition legs1 = body.addOrReplaceChild("legs1", CubeListBuilder.create().texOffs(32, 50).addBox(-5.5F, -20.0F, -4.0F, 11.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition legs2 = right_leg.addOrReplaceChild("legs2", CubeListBuilder.create().texOffs(68, 81).addBox(-8.0F, -14.0F, -3.5F, 7.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 13.0F, 0.0F));

		PartDefinition legs3 = left_leg.addOrReplaceChild("legs3", CubeListBuilder.create().texOffs(86, 51).addBox(1.0F, -14.0F, -3.5F, 7.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 13.0F, 0.0F));

		return LayerDefinition.create(mesh, 128, 128);
	}

}