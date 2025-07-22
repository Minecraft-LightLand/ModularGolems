package dev.xkmc.modulargolems.content.client.armor;

import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels.buildGolemBaseLayers;
import static dev.xkmc.modulargolems.content.client.armor.GolemModelPaths.*;

public class MonstrosityArmors {

	public static final ModelLayerLocation HELMET_LAYER = new ModelLayerLocation(ModularGolems.loc( "monstrosity_helmet"), "main");
	public static final ModelLayerLocation CHESTPLATE_LAYER = new ModelLayerLocation(ModularGolems.loc( "monstrosity_chestplate"), "main");
	public static final ModelLayerLocation SHINGUARD_LAYER = new ModelLayerLocation(ModularGolems.loc( "monstrosity_shinguard"), "main");

	static {
		GolemModelPath.register(MONSTROSITY_HELMETS,
				new GolemModelPath(HELMET_LAYER, List.of(
						List.of("head", "head1")
				)));

		GolemModelPath.register(MONSTROSITY_CHESTPLATES,
				new GolemModelPath(CHESTPLATE_LAYER, List.of(
						List.of("body", "body1"),
						List.of("right_arm", "body2"),
						List.of("left_arm", "body3")
				)));

		GolemModelPath.register(MONSTROSITY_LEGGINGS,
				new GolemModelPath(SHINGUARD_LAYER, List.of(
						List.of("body", "legs1"),
						List.of("right_leg", "legs2"),
						List.of("left_leg", "legs3")
				)));
	}


	public static LayerDefinition createHelmet() {

		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition head = mesh.getRoot().getChild("head");

		PartDefinition head1 = head.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(20, 75).addBox(-8.0F, -45.0F, -5.0F, 2.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 24).addBox(-4.5F, -45.0F, -8.0F, 9.0F, 11.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(0, 73).addBox(-5.0F, -37.0F, -9.0F, 2.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(74, 24).addBox(3.0F, -37.0F, -9.0F, 2.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(66, 36).addBox(-5.0F, -38.0F, -1.0F, 10.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(44, 75).addBox(-6.0F, -42.0F, -5.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(56, 75).addBox(4.0F, -42.0F, -5.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(32, 75).addBox(6.0F, -45.0F, -5.0F, 2.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 2.0F));
		return LayerDefinition.create(mesh, 128, 128);
	}


	public static LayerDefinition createChestplate() {

		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_arm = mesh.getRoot().getChild("right_arm");
		PartDefinition left_arm = mesh.getRoot().getChild("left_arm");

		PartDefinition body1 = body.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 0).addBox(-9.5F, 15.7F, -6.5F, 19.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(62, 15).addBox(-6.0F, 15.5F, 5.5F, 12.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -18.0F, 0.0F));

		PartDefinition body2 = right_arm.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(36, 36).addBox(-16.0F, -31.0F, -4.0F, 7.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(30, 50).addBox(-14.0F, -36.0F, -4.0F, 5.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(0, 58).addBox(-13.5F, -19.0F, -3.5F, 5.0F, 8.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(68, 75).addBox(-14.0F, -16.0F, 2.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(74, 75).addBox(-14.0F, -16.0F, -4.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition body3 = left_arm.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(0, 44).addBox(9.0F, -31.0F, -4.0F, 7.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(56, 50).addBox(9.0F, -36.0F, -4.0F, 5.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(62, 0).addBox(8.5F, -19.0F, -3.5F, 5.0F, 8.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(76, 63).addBox(13.0F, -16.0F, -4.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(80, 71).addBox(13.0F, -16.0F, 2.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));
		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createLeggings() {

		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
		PartDefinition left_leg = mesh.getRoot().getChild("left_leg");

		PartDefinition legs1 = body.addOrReplaceChild("legs1", CubeListBuilder.create().texOffs(36, 24).addBox(-5.5F, -10.0F, -4.0F, 11.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, 0.0F));

		PartDefinition legs2 = right_leg.addOrReplaceChild("legs2", CubeListBuilder.create().texOffs(24, 63).addBox(-8.0F, -11.0F, -3.5F, 7.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 13.0F, 0.0F));

		PartDefinition legs3 = left_leg.addOrReplaceChild("legs3", CubeListBuilder.create().texOffs(50, 63).addBox(1.0F, -11.0F, -3.5F, 7.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 13.0F, 0.0F));
		return LayerDefinition.create(mesh, 128, 128);
	}

}