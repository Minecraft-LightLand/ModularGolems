package dev.xkmc.modulargolems.compat.materials.cataclysm.client;

import dev.xkmc.modulargolems.content.client.armor.GolemModelPath;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorPaths.*;
import static dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels.buildGolemBaseLayers;

public class EnderGuardianArmors {

	public static final ModelLayerLocation HELMET_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "ender_guardian_helmet"), "main");
	public static final ModelLayerLocation CHESTPLATE_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "ender_guardian_chestplate"), "main");
	public static final ModelLayerLocation SHINGUARD_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "ender_guardian_shinguard"), "main");

	static {
		GolemModelPath.register(ENDER_GUARDIAN_HELMETS,
				new GolemModelPath(HELMET_LAYER, List.of(
						List.of("head", "head1"),
						List.of("head", "head1", "cube_r1"),
						List.of("head", "head1", "cube_r2"),
						List.of("head", "head1", "cube_r3")
				)));

		GolemModelPath.register(ENDER_GUARDIAN_CHESTPLATES,
				new GolemModelPath(CHESTPLATE_LAYER, List.of(
						List.of("body", "body1"),
						List.of("right_arm", "body2"),
						List.of("right_arm", "body2", "cube_r4"),
						List.of("left_arm", "body3"),
						List.of("left_arm", "body3", "cube_r5")
				)));

		GolemModelPath.register(ENDER_GUARDIAN_LEGGINGS,
				new GolemModelPath(SHINGUARD_LAYER, List.of(
						List.of("body", "legs1"),
						List.of("right_leg", "legs2"),
						List.of("right_leg", "legs2", "cube_r6"),
						List.of("left_leg", "legs3"),
						List.of("left_leg", "legs3", "cube_r7")
				)));
	}

	public static LayerDefinition createHelmet() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition head = mesh.getRoot().getChild("head");

		PartDefinition head1 = head.addOrReplaceChild("head1", CubeListBuilder.create()
				.texOffs(0, 59).addBox(-4.5F, -43.5F, -8.0F, 9.0F, 10.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(102, 39).addBox(-4.5F, -39.5F, -8.5F, 9.0F, 4.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offset(0.0F, 31.0F, 2.0F));

		head1.addOrReplaceChild("cube_r1", CubeListBuilder.create()
				.texOffs(64, 73).addBox(-1.0F, -2.0F, -6.0F, 3.0F, 5.0F, 11.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(3.0F, -35.5F, -3.5F, 0.0F, 0.0F, -0.1745F));

		head1.addOrReplaceChild("cube_r2", CubeListBuilder.create()
				.texOffs(36, 73).addBox(-2.0F, -2.0F, -6.0F, 3.0F, 5.0F, 11.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-3.0F, -35.5F, -3.5F, 0.0F, 0.0F, 0.1745F));

		head1.addOrReplaceChild("cube_r3", CubeListBuilder.create()
				.texOffs(74, 89).addBox(-1.0F, -1.0F, -4.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -43.5F, -4.0F, 0.1745F, 0.0F, 0.0F));

		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createChestplate() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_arm = mesh.getRoot().getChild("right_arm");
		PartDefinition left_arm = mesh.getRoot().getChild("left_arm");

		body.addOrReplaceChild("body1", CubeListBuilder.create()
				.texOffs(0, 20).addBox(-9.5F, -33.5F, -6.5F, 19.0F, 7.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-9.5F, -27.5F, -6.5F, 19.0F, 8.0F, 12.0F, new CubeDeformation(-0.3F))
				.texOffs(0, 39).addBox(-10.0F, -35.0F, -7.0F, 5.0F, 7.0F, 13.0F, new CubeDeformation(0.0F))
				.texOffs(36, 39).addBox(5.0F, -35.0F, -7.0F, 5.0F, 7.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition body2 = right_arm.addOrReplaceChild("body2", CubeListBuilder.create()
				.texOffs(72, 23).addBox(-16.0F, -34.0F, -4.0F, 7.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(68, 101).addBox(-13.0F, -24.0F, -3.0F, 4.0F, 8.0F, 6.0F, new CubeDeformation(0.2F))
				.texOffs(74, 59).addBox(-13.5F, -8.0F, -3.5F, 6.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(98, 87).addBox(-14.5F, -24.0F, -3.5F, 6.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(0, 78).addBox(-13.5F, -16.0F, -3.5F, 5.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		body2.addOrReplaceChild("cube_r4", CubeListBuilder.create()
				.texOffs(100, 59).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-14.0F, -32.0F, 0.0F, 0.0F, 0.0F, -0.6981F));

		PartDefinition body3 = left_arm.addOrReplaceChild("body3", CubeListBuilder.create()
				.texOffs(72, 41).addBox(-16.0F, -34.0F, -4.0F, 7.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(102, 15).addBox(-16.0F, -24.0F, -3.0F, 4.0F, 8.0F, 6.0F, new CubeDeformation(0.2F))
				.texOffs(48, 89).addBox(-17.5F, -8.0F, -3.5F, 6.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(98, 97).addBox(-16.5F, -24.0F, -3.5F, 6.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(24, 89).addBox(-16.5F, -16.0F, -3.5F, 5.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(25.0F, 31.0F, 0.0F));

		body3.addOrReplaceChild("cube_r5", CubeListBuilder.create()
				.texOffs(102, 29).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-11.0F, -32.0F, 0.0F, 0.0F, 0.0F, 0.6981F));

		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createLeggings() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
		PartDefinition left_leg = mesh.getRoot().getChild("left_leg");

		body.addOrReplaceChild("legs1", CubeListBuilder.create()
				.texOffs(62, 0).addBox(-5.5F, -20.0F, -4.0F, 11.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(62, 12).addBox(-5.5F, -19.0F, -4.0F, 11.0F, 3.0F, 8.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition legs2 = right_leg.addOrReplaceChild("legs2", CubeListBuilder.create()
				.texOffs(100, 0).addBox(-5.0F, -16.5F, -3.5F, 4.0F, 9.0F, 6.0F, new CubeDeformation(-0.1F)), PartPose.offset(4.0F, 13.0F, 0.0F));

		legs2.addOrReplaceChild("cube_r6", CubeListBuilder.create()
				.texOffs(92, 71).addBox(-3.0F, -1.0F, -3.0F, 4.0F, 10.0F, 6.0F, new CubeDeformation(0.1F)),
				PartPose.offsetAndRotation(-5.0F, -15.5F, -0.5F, 0.0F, 0.0F, -0.0873F));

		PartDefinition legs3 = left_leg.addOrReplaceChild("legs3", CubeListBuilder.create()
				.texOffs(48, 101).addBox(-8.0F, -16.5F, -3.5F, 4.0F, 9.0F, 6.0F, new CubeDeformation(-0.1F)), PartPose.offset(4.0F, 13.0F, 0.0F));

		legs3.addOrReplaceChild("cube_r7", CubeListBuilder.create()
				.texOffs(0, 93).addBox(-1.0F, -1.0F, -3.0F, 4.0F, 10.0F, 6.0F, new CubeDeformation(0.1F)),
				PartPose.offsetAndRotation(-4.0F, -15.5F, -0.5F, 0.0F, 0.0F, 0.0873F));

		return LayerDefinition.create(mesh, 128, 128);
	}

}
