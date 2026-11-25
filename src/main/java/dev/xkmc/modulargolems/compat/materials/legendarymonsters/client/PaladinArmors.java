package dev.xkmc.modulargolems.compat.materials.legendarymonsters.client;

import dev.xkmc.modulargolems.content.client.armor.GolemModelPath;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorPaths.*;
import static dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels.buildGolemBaseLayers;

/** Paladin soul knight armor model, split into helmet / chestplate / shinguard layers. */
public class PaladinArmors {

	public static final ModelLayerLocation HELMET_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModularGolems.MODID, "paladinarmors_helmet"), "main");
	public static final ModelLayerLocation CHESTPLATE_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModularGolems.MODID, "paladinarmors_chestplate"), "main");
	public static final ModelLayerLocation SHINGUARD_LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ModularGolems.MODID, "paladinarmors_shinguard"), "main");

	static {
		GolemModelPath.register(PALADIN_HELMETS,
				new GolemModelPath(HELMET_LAYER, List.of(
						List.of("head", "head1"),
						List.of("head", "head1", "cube_r3"),
						List.of("head", "head1", "cube_r4"),
						List.of("head", "head1", "cube_r5"),
						List.of("head", "head1", "cube_r6")
				)));

		GolemModelPath.register(PALADIN_CHESTPLATES,
				new GolemModelPath(CHESTPLATE_LAYER, List.of(
						List.of("body", "body1"),
						List.of("body", "body1", "cube_r1"),
						List.of("body", "body1", "cube_r2"),
						List.of("right_arm", "body2"),
						List.of("right_arm", "body2", "cube_r7"),
						List.of("right_arm", "body2", "cube_r8"),
						List.of("left_arm", "body3"),
						List.of("left_arm", "body3", "cube_r9"),
						List.of("left_arm", "body3", "cube_r10")
				)));

		GolemModelPath.register(PALADIN_LEGGINGS,
				new GolemModelPath(SHINGUARD_LAYER, List.of(
						List.of("body", "legs1"),
						List.of("right_leg", "legs2"),
						List.of("right_leg", "legs2", "cube_r11"),
						List.of("left_leg", "legs3"),
						List.of("left_leg", "legs3", "cube_r12")
				)));

	}

	public static LayerDefinition createHelmet() {
		MeshDefinition mesh = buildGolemBaseLayers();

		PartDefinition head = mesh.getRoot().getChild("head");

		PartDefinition head1 = head.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(0, 62).addBox(-4.5F, -45.0F, -8.0F, 9.0F, 11.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 2.0F));

		PartDefinition cube_r3 = head1.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(50, 100).addBox(1.0F, -1.0F, -6.0F, 0.0F, 7.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, -46.0F, -4.0F, 1.0144F, -0.1166F, -0.1848F));

		PartDefinition cube_r4 = head1.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 112).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -45.0F, -6.0F, 0.829F, 0.0F, 0.0F));

		PartDefinition cube_r5 = head1.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(62, 0).addBox(-4.0F, -5.0F, -4.0F, 9.0F, 10.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -39.0F, -7.0F, 0.4754F, -0.7268F, -0.3295F));

		PartDefinition cube_r6 = head1.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(72, 100).addBox(-1.0F, -1.0F, -6.0F, 0.0F, 7.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -46.0F, -4.0F, 1.0144F, 0.1166F, 0.1848F));

		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createChestplate() {
		MeshDefinition mesh = buildGolemBaseLayers();

		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_arm = mesh.getRoot().getChild("right_arm");
		PartDefinition left_arm = mesh.getRoot().getChild("left_arm");

		PartDefinition body1 = body.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 0).addBox(-9.5F, -33.5F, -6.5F, 19.0F, 13.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(0, 25).addBox(-10.0F, -34.0F, -7.0F, 20.0F, 7.0F, 13.0F, new CubeDeformation(0.0F))
				.texOffs(0, 45).addBox(-10.0F, -24.0F, -7.0F, 20.0F, 4.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition cube_r1 = body1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(66, 34).addBox(-9.0F, -2.0F, -1.0F, 18.0F, 15.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -17.0F, 8.0F, 0.2182F, 0.0F, 0.0F));

		PartDefinition cube_r2 = body1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(66, 19).addBox(-9.0F, -2.0F, -1.0F, 18.0F, 15.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -31.0F, 7.0F, 0.0436F, 0.0F, 0.0F));

		PartDefinition body2 = right_arm.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(36, 76).addBox(-13.5F, -19.0F, -3.5F, 5.0F, 14.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(0, 103).addBox(-13.5F, -20.0F, -3.5F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.2F))
				.texOffs(94, 100).addBox(-13.5F, -29.0F, -3.5F, 5.0F, 6.0F, 7.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition cube_r7 = body2.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(60, 85).addBox(-3.5F, -2.0F, -4.5F, 6.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0F, -16.0F, 0.5F, 0.0F, 0.0F, -0.3054F));

		PartDefinition cube_r8 = body2.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(74, 49).addBox(-7.5F, -5.0F, -4.5F, 7.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, -29.0F, 0.5F, 0.0F, 0.0F, 0.3927F));

		PartDefinition body3 = left_arm.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(102, 15).addBox(8.5F, -29.0F, -3.5F, 5.0F, 6.0F, 7.0F, new CubeDeformation(0.2F))
				.texOffs(0, 82).addBox(8.5F, -19.0F, -3.5F, 5.0F, 14.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(104, 46).addBox(8.5F, -20.0F, -3.5F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition cube_r9 = body3.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(88, 85).addBox(-2.5F, -2.0F, -4.5F, 6.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.0F, -16.0F, 0.5F, 0.0F, 0.0F, 0.3054F));

		PartDefinition cube_r10 = body3.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(74, 67).addBox(0.5F, -5.0F, -4.5F, 7.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, -29.0F, 0.5F, 0.0F, 0.0F, -0.3927F));

		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createLeggings() {
		MeshDefinition mesh = buildGolemBaseLayers();

		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
		PartDefinition left_leg = mesh.getRoot().getChild("left_leg");

		PartDefinition legs1 = body.addOrReplaceChild("legs1", CubeListBuilder.create().texOffs(36, 62).addBox(-5.5F, -21.0F, -4.0F, 11.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition legs2 = right_leg.addOrReplaceChild("legs2", CubeListBuilder.create().texOffs(24, 97).addBox(-8.0F, -17.0F, -3.5F, 7.0F, 9.0F, 6.0F, new CubeDeformation(-0.2F))
				.texOffs(102, 37).addBox(-8.0F, -10.0F, -3.5F, 7.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 13.0F, 0.0F));

		PartDefinition cube_r11 = legs2.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(104, 55).addBox(-3.0F, -1.0F, -1.5F, 7.0F, 5.0F, 3.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(-5.0F, -14.0F, -4.0F, 0.48F, 0.0F, 0.0F));

		PartDefinition legs3 = left_leg.addOrReplaceChild("legs3", CubeListBuilder.create().texOffs(98, 0).addBox(1.0F, -17.0F, -3.5F, 7.0F, 9.0F, 6.0F, new CubeDeformation(-0.2F))
				.texOffs(102, 28).addBox(1.0F, -10.0F, -3.5F, 7.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 13.0F, 0.0F));

		PartDefinition cube_r12 = legs3.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(104, 71).addBox(-4.0F, -1.0F, -1.5F, 7.0F, 5.0F, 3.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(5.0F, -14.0F, -4.0F, 0.48F, 0.0F, 0.0F));

		return LayerDefinition.create(mesh, 128, 128);
	}

}