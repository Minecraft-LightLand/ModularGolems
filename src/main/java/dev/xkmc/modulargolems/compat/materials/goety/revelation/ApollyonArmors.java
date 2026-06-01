package dev.xkmc.modulargolems.compat.materials.goety.revelation;

import dev.xkmc.modulargolems.content.client.armor.GolemModelPath;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static dev.xkmc.modulargolems.compat.materials.goety.revelation.GRArmorPaths.*;
import static dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels.buildGolemBaseLayers;

public class ApollyonArmors {

	public static final ModelLayerLocation HELMET_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "apocalyptium_helmet"), "main");
	public static final ModelLayerLocation CHESTPLATE_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "apocalyptium_chestplate"), "main");
	public static final ModelLayerLocation SHINGUARD_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "apocalyptium_shinguard"), "main");
	public static final ModelLayerLocation BOOTS_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "apocalyptium_boots"), "main");

	static {
		GolemModelPath.register(APOLLYON_HELMETS,
				new GolemModelPath(HELMET_LAYER, List.of(
						List.of("head", "head1")
				)));

		GolemModelPath.register(APOLLYON_CHESTPLATES,
				new GolemModelPath(CHESTPLATE_LAYER, List.of(
						List.of("body", "body1"),
						List.of("right_arm", "body2"),
						List.of("left_arm", "body3")
				)));

		GolemModelPath.register(APOLLYON_LEGGINGS,
				new GolemModelPath(SHINGUARD_LAYER, List.of(
						List.of("body", "legs1"),
						List.of("right_leg", "legs2"),
						List.of("left_leg", "legs3")
				)));
		GolemModelPath.register(APOLLYON_BOOTS,
				new GolemModelPath(BOOTS_LAYER, List.of(
						List.of("right_leg", "boots1"),
						List.of("left_leg", "boots2")
				)));
	}


	public static LayerDefinition createHelmet() {
		MeshDefinition mesh = buildGolemBaseLayers();

		PartDefinition head = mesh.getRoot().getChild("head");

		PartDefinition head1 = head.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(0, 43).addBox(-4.5F, -45.0F, -8.0F, 9.0F, 11.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(36, 43).addBox(-5.0F, -38.0F, -9.0F, 10.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 2.0F));

		PartDefinition cube_r4 = head1.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(100, 67).addBox(0.0F, -1.0F, -3.0F, 1.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(84, 100).addBox(11.0F, -1.0F, -3.0F, 1.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -44.0F, -4.0F, 1.0036F, 0.0F, 0.0F));

		PartDefinition cube_r5 = head1.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(62, 16).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -43.0F, -7.0F, 0.6981F, 0.0F, 0.0F));

		PartDefinition cube_r6 = head1.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(36, 58).addBox(-4.0F, -3.0F, -4.0F, 9.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -39.0F, -7.0F, 0.4754F, -0.7268F, -0.3295F));

		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createChestplate() {
		MeshDefinition mesh = buildGolemBaseLayers();

		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_arm = mesh.getRoot().getChild("right_arm");
		PartDefinition left_arm = mesh.getRoot().getChild("left_arm");

		PartDefinition body1 = body.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 0).addBox(-9.5F, -33.5F, -6.5F, 19.0F, 13.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(62, 0).addBox(-9.0F, -42.0F, 7.0F, 17.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition cube_r1 = body1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(42, 25).addBox(0.0F, -3.0F, -1.0F, 8.0F, 5.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -30.0F, -6.0F, 0.0F, 0.0F, -0.1745F));

		PartDefinition cube_r2 = body1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 25).addBox(-8.0F, -3.0F, -1.0F, 8.0F, 5.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -30.0F, -6.0F, 0.0F, 0.0F, 0.1745F));

		PartDefinition cube_r3 = body1.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(96, 0).addBox(-4.0F, -4.0F, -1.0F, 8.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -28.0F, -7.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition body2 = right_arm.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(38, 72).addBox(-15.0F, -34.0F, -4.0F, 6.0F, 11.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(66, 77).addBox(-13.5F, -15.0F, -3.5F, 5.0F, 9.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(18, 102).addBox(-17.0F, -18.0F, 0.0F, 4.0F, 13.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(0, 100).addBox(-13.5F, -22.0F, -3.5F, 2.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition cube_r7 = body2.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(90, 89).addBox(-1.0F, -2.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.0F, -35.0F, 1.0F, 0.6109F, 0.0F, 0.0F));

		PartDefinition cube_r8 = body2.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(24, 91).addBox(-1.0F, -2.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-16.0F, -30.0F, 1.0F, 0.4548F, -0.276F, -0.1325F));

		PartDefinition body3 = left_arm.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(72, 58).addBox(-13.0F, -34.0F, -4.0F, 6.0F, 11.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(0, 84).addBox(-13.5F, -15.0F, -3.5F, 5.0F, 9.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(26, 102).addBox(-9.0F, -18.0F, 0.0F, 4.0F, 13.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(100, 55).addBox(-10.5F, -22.0F, -3.5F, 2.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(22.0F, 31.0F, 0.0F));

		PartDefinition cube_r9 = body3.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(64, 93).addBox(-1.0F, -2.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, -35.0F, 1.0F, 0.6109F, 0.0F, 0.0F));

		PartDefinition cube_r10 = body3.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(44, 91).addBox(-1.0F, -2.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -30.0F, 1.0F, 0.4548F, 0.276F, 0.1325F));

		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createLeggings() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
		PartDefinition left_leg = mesh.getRoot().getChild("left_leg");

		PartDefinition legs1 = body.addOrReplaceChild("legs1", CubeListBuilder.create().texOffs(0, 72).addBox(-5.5F, -20.0F, -4.0F, 11.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition legs2 = right_leg.addOrReplaceChild("legs2", CubeListBuilder.create().texOffs(76, 43).addBox(-8.0F, -14.0F, -3.5F, 7.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 13.0F, 0.0F));

		PartDefinition legs3 = left_leg.addOrReplaceChild("legs3", CubeListBuilder.create().texOffs(84, 16).addBox(-8.0F, -14.0F, -3.5F, 7.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 13.0F, 0.0F));

		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createBoots() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
		PartDefinition left_leg = mesh.getRoot().getChild("left_leg");

		PartDefinition boots1 = right_leg.addOrReplaceChild("boots1", CubeListBuilder.create().texOffs(84, 28).addBox(-4.0F, -2.5F, -3.5F, 7.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, 0.0F));
		PartDefinition boots2 = left_leg.addOrReplaceChild("boots2", CubeListBuilder.create().texOffs(90, 77).addBox(-4.0F, -2.5F, -3.5F, 7.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 10.0F, 0.0F));

		return LayerDefinition.create(mesh, 128, 128);
	}

}