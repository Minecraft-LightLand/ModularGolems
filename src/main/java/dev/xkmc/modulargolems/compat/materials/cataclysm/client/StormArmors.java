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

public class StormArmors {

	public static final ModelLayerLocation HELMET_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "storm_helmet"), "main");
	public static final ModelLayerLocation CHESTPLATE_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "storm_chestplate"), "main");
	public static final ModelLayerLocation SHINGUARD_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "storm_shinguard"), "main");

	static {
		GolemModelPath.register(STORM_HELMETS,
				new GolemModelPath(HELMET_LAYER, List.of(
						List.of("head", "head1"),
						List.of("head", "head1", "cube_r8"),
						List.of("head", "head1", "cube_r9"),
						List.of("head", "head1", "cube_r10"),
						List.of("head", "head1", "cube_r11"),
						List.of("head", "head1", "cube_r12"),
						List.of("head", "head1", "cube_r13")
				)));

		GolemModelPath.register(STORM_CHESTPLATES,
				new GolemModelPath(CHESTPLATE_LAYER, List.of(
						List.of("body", "body1"),
						List.of("body", "body1", "cube_r1"),
						List.of("body", "body1", "bone"),
						List.of("body", "body1", "bone", "cube_r2"),
						List.of("body", "body1", "bone", "cube_r3"),
						List.of("body", "body1", "bone", "cube_r4"),
						List.of("body", "body1", "bone2"),
						List.of("body", "body1", "bone2", "cube_r5"),
						List.of("body", "body1", "bone2", "cube_r6"),
						List.of("body", "body1", "bone2", "cube_r7"),
						List.of("right_arm", "body2"),
						List.of("right_arm", "body2", "cube_r14"),
						List.of("right_arm", "body2", "cube_r15"),
						List.of("left_arm", "body3"),
						List.of("left_arm", "body3", "cube_r16"),
						List.of("left_arm", "body3", "cube_r17")
				)));

		GolemModelPath.register(STORM_LEGGINGS,
				new GolemModelPath(SHINGUARD_LAYER, List.of(
						List.of("body", "legs1"),
						List.of("right_leg", "legs2"),
						List.of("right_leg", "legs2", "cube_r18"),
						List.of("right_leg", "legs2", "cube_r19"),
						List.of("left_leg", "legs3"),
						List.of("left_leg", "legs3", "cube_r20"),
						List.of("left_leg", "legs3", "cube_r21")
				)));
	}

	public static LayerDefinition createHelmet() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition head = mesh.getRoot().getChild("head");

		PartDefinition head1 = head.addOrReplaceChild("head1", CubeListBuilder.create()
				.texOffs(0, 39).addBox(-4.5F, -43.5F, -8.0F, 9.0F, 10.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(100, 0).addBox(-4.5F, -37.5F, -0.5F, 9.0F, 4.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offset(0.0F, 31.0F, 2.0F));

		head1.addOrReplaceChild("cube_r8", CubeListBuilder.create()
				.texOffs(66, 101).addBox(-7.0F, -6.0F, -1.0F, 8.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.0F, -40.0F, -4.0F, 0.0F, 0.9599F, 0.0F));

		head1.addOrReplaceChild("cube_r9", CubeListBuilder.create()
				.texOffs(100, 94).addBox(-1.0F, -6.0F, -1.0F, 8.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(4.0F, -40.0F, -4.0F, 0.0F, -0.9599F, 0.0F));

		head1.addOrReplaceChild("cube_r10", CubeListBuilder.create()
				.texOffs(56, 76).addBox(-2.5F, -0.1305F, -1.4914F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -44.0F, -8.5F, 0.1841F, 0.7769F, 0.1298F));

		head1.addOrReplaceChild("cube_r11", CubeListBuilder.create()
				.texOffs(0, 74).addBox(-1.0F, -2.0F, -5.0F, 3.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(3.0F, -35.5F, -3.5F, 0.0F, 0.0F, -0.1745F));

		head1.addOrReplaceChild("cube_r12", CubeListBuilder.create()
				.texOffs(72, 61).addBox(-2.0F, -2.0F, -5.0F, 3.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-3.0F, -35.5F, -3.5F, 0.0F, 0.0F, 0.1745F));

		head1.addOrReplaceChild("cube_r13", CubeListBuilder.create()
				.texOffs(56, 85).addBox(-1.0F, -1.0F, -4.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -43.5F, -4.0F, 0.1745F, 0.0F, 0.0F));

		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createChestplate() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_arm = mesh.getRoot().getChild("right_arm");
		PartDefinition left_arm = mesh.getRoot().getChild("left_arm");

		PartDefinition body1 = body.addOrReplaceChild("body1", CubeListBuilder.create()
				.texOffs(0, 20).addBox(-9.5F, -33.5F, -6.5F, 19.0F, 7.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-9.5F, -27.5F, -6.5F, 19.0F, 8.0F, 12.0F, new CubeDeformation(-0.3F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		body1.addOrReplaceChild("cube_r1", CubeListBuilder.create()
				.texOffs(36, 39).addBox(-8.0F, -2.0F, -1.0F, 16.0F, 21.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -31.0F, 6.7F, 0.0873F, 0.0F, 0.0F));

		PartDefinition bone = body1.addOrReplaceChild("bone", CubeListBuilder.create()
				.texOffs(82, 101).addBox(-1.0F, -10.0F, 3.0F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(98, 55).addBox(-2.0F, -15.0F, -4.0F, 5.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(8.0F, -32.0F, 4.0F, -0.0873F, 0.0F, 0.48F));

		bone.addOrReplaceChild("cube_r2", CubeListBuilder.create()
				.texOffs(66, 97).addBox(-1.0F, 0.0F, -4.0F, 3.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -12.0F, -4.0F, 0.3491F, 0.0F, 0.0F));

		bone.addOrReplaceChild("cube_r3", CubeListBuilder.create()
				.texOffs(30, 102).addBox(-2.0F, -6.0F, -1.0F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.1F)),
				PartPose.offsetAndRotation(1.0F, -9.0F, 4.0F, 0.7418F, 0.0F, 0.0F));

		bone.addOrReplaceChild("cube_r4", CubeListBuilder.create()
				.texOffs(100, 6).addBox(-2.0F, -2.0F, -1.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.1F)),
				PartPose.offsetAndRotation(1.0F, 0.0F, 1.0F, 0.5672F, 0.0F, 0.0F));

		PartDefinition bone2 = body1.addOrReplaceChild("bone2", CubeListBuilder.create()
				.texOffs(18, 102).addBox(-2.0F, -10.0F, 3.0F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(98, 65).addBox(-3.0F, -15.0F, -4.0F, 5.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-8.0F, -32.0F, 4.0F, 0.0F, 0.0F, -0.3927F));

		bone2.addOrReplaceChild("cube_r5", CubeListBuilder.create()
				.texOffs(100, 15).addBox(-1.0F, 0.0F, -4.0F, 3.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.0F, -12.0F, -4.0F, 0.3491F, 0.0F, 0.0F));

		bone2.addOrReplaceChild("cube_r6", CubeListBuilder.create()
				.texOffs(102, 75).addBox(-2.0F, -6.0F, -1.0F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.1F)),
				PartPose.offsetAndRotation(0.0F, -9.0F, 4.0F, 0.7418F, 0.0F, 0.0F));

		bone2.addOrReplaceChild("cube_r7", CubeListBuilder.create()
				.texOffs(100, 85).addBox(-2.0F, -2.0F, -1.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.1F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.5672F, 0.0F, 0.0F));

		PartDefinition body2 = right_arm.addOrReplaceChild("body2", CubeListBuilder.create()
				.texOffs(68, 23).addBox(-14.0F, -18.0F, -3.5F, 4.0F, 12.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(0, 88).addBox(-12.0F, -17.5F, -3.0F, 3.0F, 11.0F, 6.0F, new CubeDeformation(0.2F))
				.texOffs(62, 23).addBox(-16.0F, -21.0F, 0.0F, 3.0F, 13.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		body2.addOrReplaceChild("cube_r14", CubeListBuilder.create()
				.texOffs(72, 75).addBox(-7.0F, -2.0F, -4.0F, 8.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-9.0F, -25.0F, 0.5F, 0.0F, 0.0F, 0.2618F));

		body2.addOrReplaceChild("cube_r15", CubeListBuilder.create()
				.texOffs(0, 58).addBox(-7.0F, -6.0F, -4.0F, 9.0F, 7.0F, 9.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-10.0F, -28.0F, -0.5F, 0.0F, 0.0F, 0.2618F));

		PartDefinition body3 = left_arm.addOrReplaceChild("body3", CubeListBuilder.create()
				.texOffs(72, 42).addBox(-15.0F, -18.0F, -3.5F, 4.0F, 12.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(90, 23).addBox(-16.0F, -17.5F, -3.0F, 3.0F, 11.0F, 6.0F, new CubeDeformation(0.2F))
				.texOffs(18, 88).addBox(-12.0F, -21.0F, 0.0F, 3.0F, 13.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(25.0F, 31.0F, 0.0F));

		body3.addOrReplaceChild("cube_r16", CubeListBuilder.create()
				.texOffs(26, 76).addBox(-1.0F, -2.0F, -4.0F, 8.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-16.0F, -25.0F, 0.5F, 0.0F, 0.0F, -0.2618F));

		body3.addOrReplaceChild("cube_r17", CubeListBuilder.create()
				.texOffs(36, 60).addBox(-1.0F, -6.0F, -4.0F, 9.0F, 7.0F, 9.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-16.0F, -28.0F, -0.5F, 0.0F, 0.0F, -0.2618F));

		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createLeggings() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
		PartDefinition left_leg = mesh.getRoot().getChild("left_leg");

		body.addOrReplaceChild("legs1", CubeListBuilder.create()
				.texOffs(62, 0).addBox(-5.5F, -20.0F, -4.0F, 11.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(62, 12).addBox(-5.5F, -20.0F, -4.0F, 11.0F, 3.0F, 8.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition legs2 = right_leg.addOrReplaceChild("legs2", CubeListBuilder.create()
				.texOffs(94, 40).addBox(-5.0F, -16.5F, -3.5F, 4.0F, 9.0F, 6.0F, new CubeDeformation(-0.1F)), PartPose.offset(4.0F, 13.0F, 0.0F));

		legs2.addOrReplaceChild("cube_r18", CubeListBuilder.create()
				.texOffs(46, 86).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.5F, -8.0F, -3.0F, 0.0F, 0.0F, -0.7854F));

		legs2.addOrReplaceChild("cube_r19", CubeListBuilder.create()
				.texOffs(80, 85).addBox(-3.0F, -1.0F, -3.0F, 4.0F, 10.0F, 6.0F, new CubeDeformation(0.1F)),
				PartPose.offsetAndRotation(-5.0F, -15.5F, -0.5F, 0.0F, 0.0F, 0.0873F));

		PartDefinition legs3 = left_leg.addOrReplaceChild("legs3", CubeListBuilder.create()
				.texOffs(46, 97).addBox(-8.0F, -16.5F, -3.5F, 4.0F, 9.0F, 6.0F, new CubeDeformation(-0.1F)), PartPose.offset(4.0F, 13.0F, 0.0F));

		legs3.addOrReplaceChild("cube_r20", CubeListBuilder.create()
				.texOffs(46, 91).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.5F, -8.0F, -3.0F, 0.0F, 0.0F, 0.7854F));

		legs3.addOrReplaceChild("cube_r21", CubeListBuilder.create()
				.texOffs(26, 86).addBox(-1.0F, -1.0F, -3.0F, 4.0F, 10.0F, 6.0F, new CubeDeformation(0.1F)),
				PartPose.offsetAndRotation(-4.0F, -15.5F, -0.5F, 0.0F, 0.0F, -0.0873F));

		return LayerDefinition.create(mesh, 128, 128);
	}

}
