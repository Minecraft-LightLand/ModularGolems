package dev.xkmc.modulargolems.compat.materials.cataclysm.client;// Made with Blockbench 4.12.5
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import dev.xkmc.modulargolems.content.client.armor.GolemModelPath;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import java.util.List;

import static dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels.buildGolemBaseLayers;
import static dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorPaths.*;

public class HarbingerArmors {

	public static final ModelLayerLocation HELMET_LAYER = new ModelLayerLocation(ModularGolems.loc( "harbinger_helmet"), "main");
	public static final ModelLayerLocation CHESTPLATE_LAYER = new ModelLayerLocation(ModularGolems.loc( "harbinger_chestplate"), "main");
	public static final ModelLayerLocation SHINGUARD_LAYER = new ModelLayerLocation(ModularGolems.loc( "harbinger_shinguard"), "main");

	static {
		GolemModelPath.register(WITHERITE_HELMETS,
				new GolemModelPath(HELMET_LAYER, List.of(
						List.of("head", "head1"),
						List.of("head", "head2")
				)));

		GolemModelPath.register(WITHERITE_CHESTPLATES,
				new GolemModelPath(CHESTPLATE_LAYER, List.of(
						List.of("body", "body1"),
						List.of("body", "body2"),
						List.of("body", "body3"),
						List.of("right_arm", "body4"),
						List.of("right_arm", "body5"),
						List.of("right_arm", "body5", "body6"),
						List.of("right_arm", "body7"),
						List.of("left_arm", "body8"),
						List.of("left_arm", "body9"),
						List.of("left_arm", "body9", "body10"),
						List.of("left_arm", "body11")
				)));

		GolemModelPath.register(WITHERITE_LEGGINGS,
				new GolemModelPath(SHINGUARD_LAYER, List.of(
						List.of("body", "legs1"),
						List.of("right_leg", "legs2"),
						List.of("left_leg", "legs3")
				)));
	}


	public static LayerDefinition createHelmet() {
		MeshDefinition mesh = buildGolemBaseLayers();

		PartDefinition head = mesh.getRoot().getChild("head");

		PartDefinition head1 = head.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(0, 24).addBox(-1.5F, 1.0F, -6.0F, 9.0F, 11.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(62, 15).addBox(2.0F, -1.0F, -8.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(74, 15).addBox(2.0F, -1.0F, -6.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(80, 48).addBox(-2.5F, 9.0F, -6.0F, 1.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(82, 61).addBox(7.5F, 9.0F, -6.0F, 1.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(74, 27).addBox(-2.0F, 7.0F, 3.0F, 10.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(82, 74).addBox(-3.0F, 5.0F, -6.5F, 2.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -14.0F, 0.0F));

		PartDefinition head2 = head.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(28, 44).addBox(-5.5F, 5.0F, -2.0F, 1.0F, 7.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(36, 79).addBox(4.5F, 5.0F, -2.0F, 1.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -20.0F, 3.0F, -0.4363F, 0.0F, 0.0F));
		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createChestplate() {
		MeshDefinition mesh = buildGolemBaseLayers();

		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_arm = mesh.getRoot().getChild("right_arm");
		PartDefinition left_arm = mesh.getRoot().getChild("left_arm");

		PartDefinition body1 = body.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 0).addBox(-9.5F, 22.7F, -6.5F, 19.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -25.0F, 0.0F));

		PartDefinition body2 = body.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(0, 77).addBox(-2.0F, -12.0F, -1.0F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, -2.0F, -4.0F, -0.6471F, -0.1059F, -0.139F));

		PartDefinition body3 = body.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(18, 79).addBox(7.0F, 2.0F, -3.0F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -13.0F, 7.0F, -0.6356F, 0.1309F, 0.1314F));

		PartDefinition body4 = right_arm.addOrReplaceChild("body4", CubeListBuilder.create().texOffs(0, 62).addBox(2.5F, -2.0F, -3.5F, 5.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-16.0F, 19.0F, 0.0F));

		PartDefinition body5 = right_arm.addOrReplaceChild("body5", CubeListBuilder.create().texOffs(36, 36).addBox(0.0F, -21.0F, -4.0F, 6.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-21.0F, 16.0F, 0.0F, 0.0F, 0.0F, 0.3491F));

		PartDefinition body6 = body5.addOrReplaceChild("body6", CubeListBuilder.create().texOffs(86, 0).addBox(-3.0F, -3.0F, -1.0F, 3.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -19.0F, -2.0F, 0.0F, 0.0F, -0.5236F));

		PartDefinition body7 = right_arm.addOrReplaceChild("body7", CubeListBuilder.create().texOffs(50, 66).addBox(-5.0F, -13.0F, 0.0F, 8.0F, 22.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-16.0F, 21.0F, 0.0F));

		PartDefinition body8 = left_arm.addOrReplaceChild("body8", CubeListBuilder.create().texOffs(62, 0).addBox(-10.5F, 17.0F, -3.5F, 5.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(19.0F, 0.0F, 0.0F));

		PartDefinition body9 = left_arm.addOrReplaceChild("body9", CubeListBuilder.create().texOffs(0, 44).addBox(-10.5F, -4.0F, -4.0F, 6.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(19.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.3491F));

		PartDefinition body10 = body9.addOrReplaceChild("body10", CubeListBuilder.create().texOffs(82, 84).addBox(-3.0F, -4.0F, -3.0F, 3.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6632F));

		PartDefinition body11 = left_arm.addOrReplaceChild("body11", CubeListBuilder.create().texOffs(66, 66).addBox(-13.0F, 8.0F, -1.0F, 8.0F, 22.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(26.0F, 0.0F, 0.0F));
		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createLeggings() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
		PartDefinition left_leg = mesh.getRoot().getChild("left_leg");

		PartDefinition legs1 = body.addOrReplaceChild("legs1", CubeListBuilder.create().texOffs(36, 23).addBox(-5.5F, 36.0F, -4.0F, 11.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -25.0F, 0.0F));

		PartDefinition legs2 = right_leg.addOrReplaceChild("legs2", CubeListBuilder.create().texOffs(24, 67).addBox(-4.0F, -22.0F, -2.5F, 7.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(28, 54).addBox(-4.0F, -12.5F, -2.5F, 7.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, -1.0F));

		PartDefinition legs3 = left_leg.addOrReplaceChild("legs3", CubeListBuilder.create().texOffs(64, 36).addBox(-4.0F, -20.0F, -3.5F, 7.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(54, 54).addBox(-4.0F, -10.5F, -3.5F, 7.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, 0.0F));
		return LayerDefinition.create(mesh, 128, 128);
	}

}