package dev.xkmc.modulargolems.compat.materials.cataclysm.client;

import dev.xkmc.modulargolems.content.client.armor.GolemModelPath;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import java.util.List;

import static dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorPaths.*;
import static dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels.buildGolemBaseLayers;

public class MaledictusArmors {

	public static final ModelLayerLocation HELMET_LAYER = new ModelLayerLocation(ModularGolems.loc("maledictus_helmet"), "main");
	public static final ModelLayerLocation CHESTPLATE_LAYER = new ModelLayerLocation(ModularGolems.loc("maledictus_chestplate"), "main");
	public static final ModelLayerLocation SHINGUARD_LAYER = new ModelLayerLocation(ModularGolems.loc("maledictus_shinguard"), "main");

	static {
		GolemModelPath.register(MALEDICTUS_HELMETS,
				new GolemModelPath(HELMET_LAYER, List.of(
						List.of("head", "head1")
				)));

		GolemModelPath.register(MALEDICTUS_CHESTPLATES,
				new GolemModelPath(CHESTPLATE_LAYER, List.of(
						List.of("body", "body1"),
						List.of("right_arm", "body2"),
						List.of("left_arm", "body3")
				)));

		GolemModelPath.register(MALEDICTUS_LEGGINGS,
				new GolemModelPath(SHINGUARD_LAYER, List.of(
						List.of("body", "legs1"),
						List.of("right_leg", "legs2"),
						List.of("left_leg", "legs3")
				)));
	}


	public static LayerDefinition createHelmet() {
		MeshDefinition mesh = buildGolemBaseLayers();

		PartDefinition head = mesh.getRoot().getChild("head");

		PartDefinition head1 = head.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(0, 26).addBox(-4.5F, -43.5F, -8.0F, 9.0F, 10.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(36, 38).addBox(-5.0F, -38.0F, -9.0F, 10.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 2.0F));

		PartDefinition bone2 = head1.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(80, 51).addBox(-2.0F, -1.0F, -2.0F, 6.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(86, 4).addBox(-3.0F, 3.0F, -0.5F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(0, 79).addBox(4.0F, -7.0F, -2.0F, 4.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(56, 83).addBox(5.0F, -4.0F, -0.5F, 6.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -42.0F, -3.0F, 0.0F, 0.0F, -0.6109F));

		PartDefinition bone = head1.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(70, 44).addBox(-4.0F, -1.0F, -2.0F, 6.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(86, 0).addBox(-3.0F, 3.0F, -0.5F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(78, 67).addBox(-8.0F, -7.0F, -2.0F, 4.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(78, 80).addBox(-11.0F, -4.0F, -0.5F, 6.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -42.0F, -3.0F, 0.0F, 0.0F, 0.6109F));

		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createChestplate() {
		MeshDefinition mesh = buildGolemBaseLayers();

		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_arm = mesh.getRoot().getChild("right_arm");
		PartDefinition left_arm = mesh.getRoot().getChild("left_arm");

		PartDefinition body1 = body.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 0).addBox(-9.5F, -33.5F, -7.0F, 19.0F, 13.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition cube_r1 = body1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(24, 69).addBox(0.0F, -8.0F, -2.0F, 0.0F, 13.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, -29.0F, 7.0F, 0.0F, 0.6109F, 0.0F));

		PartDefinition cube_r2 = body1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(40, 69).addBox(0.0F, -8.0F, -2.0F, 0.0F, 13.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, -29.0F, 7.0F, 0.0F, -0.6109F, 0.0F));

		PartDefinition body2 = right_arm.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(56, 51).addBox(-14.0F, -29.0F, -3.5F, 5.0F, 9.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(64, 0).mirror().addBox(-14.0F, -16.0F, -3.5F, 4.0F, 9.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition cube_r4 = body2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 45).addBox(-5.0F, -3.0F, -4.0F, 6.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, -30.0F, 0.0F, 0.0F, 0.0F, 1.0472F));

		PartDefinition body3 = left_arm.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(0, 63).addBox(9.0F, -29.0F, -3.5F, 5.0F, 9.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(56, 67).addBox(10.0F, -16.0F, -3.5F, 4.0F, 9.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition cube_r5 = body3.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(28, 51).addBox(-1.0F, -3.0F, -4.0F, 6.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, -30.0F, 0.0F, 0.0F, 0.0F, -1.0472F));

		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createLeggings() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
		PartDefinition left_leg = mesh.getRoot().getChild("left_leg");

		PartDefinition legs1 = body.addOrReplaceChild("legs1", CubeListBuilder.create().texOffs(36, 26).addBox(-5.5F, -20.0F, -4.0F, 11.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition cube_r3 = legs1.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(80, 58).addBox(-4.0F, -2.0F, 0.5F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -18.0F, -5.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition legs2 = right_leg.addOrReplaceChild("legs2", CubeListBuilder.create().texOffs(74, 16).addBox(-8.0F, -16.3F, -3.5F, 5.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 13.0F, 0.0F));

		PartDefinition legs3 = left_leg.addOrReplaceChild("legs3", CubeListBuilder.create().texOffs(74, 30).addBox(3.0F, -16.3F, -3.5F, 5.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 13.0F, 0.0F));

		return LayerDefinition.create(mesh, 128, 128);
	}

}