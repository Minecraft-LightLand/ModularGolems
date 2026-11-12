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

public class AncientMetalArmors {

	public static final ModelLayerLocation HELMET_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "ancient_metal_helmet"), "main");
	public static final ModelLayerLocation CHESTPLATE_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "ancient_metal_chestplate"), "main");
	public static final ModelLayerLocation SHINGUARD_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "ancient_metal_shinguard"), "main");

	static {
		GolemModelPath.register(ANCIENT_METAL_HELMETS,
				new GolemModelPath(HELMET_LAYER, List.of(
						List.of("head", "head1", "head2"),
						List.of("head", "head1", "head2", "head_r1"),
						List.of("head", "head1", "head2", "you"),
						List.of("head", "head1", "head2", "you", "you2"),
						List.of("head", "head1", "head2", "zuo"),
						List.of("head", "head1", "head2", "zuo", "zuo2")
				)));

		GolemModelPath.register(ANCIENT_METAL_CHESTPLATES,
				new GolemModelPath(CHESTPLATE_LAYER, List.of(
						List.of("body", "body1"),
						List.of("right_arm", "body2"),
						List.of("right_arm", "body2", "bone"),
						List.of("left_arm", "body3"),
						List.of("left_arm", "body3", "bone2")
				)));

		GolemModelPath.register(ANCIENT_METAL_LEGGINGS,
				new GolemModelPath(SHINGUARD_LAYER, List.of(
						List.of("body", "legs1"),
						List.of("right_leg", "legs2"),
						List.of("left_leg", "legs3")
				)));
	}

	public static LayerDefinition createHelmet() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition head = mesh.getRoot().getChild("head");

		PartDefinition head1 = head.addOrReplaceChild("head1", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, 2.0F));

		PartDefinition head2 = head1.addOrReplaceChild("head2", CubeListBuilder.create()
				.texOffs(62, 0).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(76, 45).addBox(-3.5F, -4.9F, -10.0F, 7.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.0865F, -0.0005F, 0.0114F));

		head2.addOrReplaceChild("head_r1", CubeListBuilder.create()
				.texOffs(62, 15).addBox(-2.0F, -10.0F, -8.0F, 4.0F, 5.0F, 14.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 1.0F, 3.0F, 0.4363F, 0.0F, 0.0F));

		head2.addOrReplaceChild("xiaba", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 2.0F));

		PartDefinition you = head2.addOrReplaceChild("you", CubeListBuilder.create()
				.texOffs(76, 58).addBox(-4.0F, -2.0F, -4.0F, 4.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.0F, -5.0F, 0.0F, 0.1309F, -0.3491F, -0.0873F));

		you.addOrReplaceChild("you2", CubeListBuilder.create()
				.texOffs(38, 76).addBox(-2.3F, 0.0F, -1.0F, 17.0F, 3.0F, 4.0F, new CubeDeformation(0.05F))
				.texOffs(0, 88).addBox(-2.3F, -4.0F, -1.0F, 4.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.0F, -3.0F, 1.0F, 0.0F, 0.3491F, 0.0436F));

		PartDefinition zuo = head2.addOrReplaceChild("zuo", CubeListBuilder.create()
				.texOffs(80, 72).addBox(0.0F, -1.0F, -4.0F, 4.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(2.0F, -6.0F, 0.0F, 0.1309F, 0.3491F, 0.0873F));

		zuo.addOrReplaceChild("zuo2", CubeListBuilder.create()
				.texOffs(86, 86).addBox(-2.4F, -4.0F, -3.0F, 4.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(4.0F, -2.0F, 3.0F, 0.0F, -0.3491F, -0.0436F));

		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createChestplate() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_arm = mesh.getRoot().getChild("right_arm");
		PartDefinition left_arm = mesh.getRoot().getChild("left_arm");

		body.addOrReplaceChild("body1", CubeListBuilder.create()
				.texOffs(0, 26).addBox(-9.5F, -33.5F, -6.5F, 19.0F, 7.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-9.5F, -33.5F, -6.5F, 19.0F, 14.0F, 12.0F, new CubeDeformation(-0.3F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition body2 = right_arm.addOrReplaceChild("body2", CubeListBuilder.create()
				.texOffs(38, 83).addBox(-14.0F, -12.0F, -4.0F, 4.0F, 9.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(98, 15).addBox(-13.5F, -23.0F, -3.5F, 5.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		body2.addOrReplaceChild("bone", CubeListBuilder.create()
				.texOffs(0, 45).addBox(-4.0F, -4.0F, -6.0F, 9.0F, 8.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(0, 63).addBox(-4.0F, -7.0F, -6.0F, 9.0F, 3.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-13.0F, -30.0F, 1.0F, 0.0F, 0.0F, 0.1309F));

		PartDefinition body3 = left_arm.addOrReplaceChild("body3", CubeListBuilder.create()
				.texOffs(62, 86).addBox(-15.0F, -12.0F, -4.0F, 4.0F, 9.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(86, 99).addBox(-16.5F, -23.0F, -3.5F, 5.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(25.0F, 31.0F, 0.0F));

		body3.addOrReplaceChild("bone2", CubeListBuilder.create()
				.texOffs(38, 45).addBox(-5.0F, -4.0F, -6.0F, 9.0F, 8.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(38, 63).addBox(-5.0F, -7.0F, -6.0F, 9.0F, 3.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-12.0F, -30.0F, 1.0F, 0.0F, 0.0F, -0.1309F));

		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createLeggings() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
		PartDefinition left_leg = mesh.getRoot().getChild("left_leg");

		body.addOrReplaceChild("legs1", CubeListBuilder.create()
				.texOffs(0, 76).addBox(-5.5F, -20.0F, -4.0F, 11.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(62, 34).addBox(-5.5F, -19.0F, -4.0F, 11.0F, 3.0F, 8.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		right_leg.addOrReplaceChild("legs2", CubeListBuilder.create()
				.texOffs(26, 100).addBox(-8.0F, -16.5F, -3.5F, 4.0F, 10.0F, 6.0F, new CubeDeformation(0.1F))
				.texOffs(100, 30).addBox(-5.0F, -16.5F, -3.5F, 4.0F, 9.0F, 6.0F, new CubeDeformation(-0.1F)), PartPose.offset(4.0F, 13.0F, 0.0F));

		left_leg.addOrReplaceChild("legs3", CubeListBuilder.create()
				.texOffs(0, 101).addBox(-5.0F, -16.5F, -3.5F, 4.0F, 10.0F, 6.0F, new CubeDeformation(0.1F))
				.texOffs(102, 0).addBox(-8.0F, -16.5F, -3.5F, 4.0F, 9.0F, 6.0F, new CubeDeformation(-0.1F)), PartPose.offset(4.0F, 13.0F, 0.0F));

		return LayerDefinition.create(mesh, 128, 128);
	}

}
