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

/** Obliterator annihilation armor model, split into helmet / chestplate / shinguard layers. */
public class ObliteratorArmors {

	public static final ModelLayerLocation HELMET_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "obliteratorarmors_helmet"), "main");
	public static final ModelLayerLocation CHESTPLATE_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "obliteratorarmors_chestplate"), "main");
	public static final ModelLayerLocation SHINGUARD_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "obliteratorarmors_shinguard"), "main");

	static {
		GolemModelPath.register(OBLITERATOR_HELMETS,
				new GolemModelPath(HELMET_LAYER, List.of(
						List.of("head", "head1")
				)));

		GolemModelPath.register(OBLITERATOR_CHESTPLATES,
				new GolemModelPath(CHESTPLATE_LAYER, List.of(
						List.of("body", "body1"),
						List.of("right_arm", "body2"),
						List.of("left_arm", "body3")
				)));

		GolemModelPath.register(OBLITERATOR_LEGGINGS,
				new GolemModelPath(SHINGUARD_LAYER, List.of(
						List.of("body", "legs1"),
						List.of("right_leg", "legs2"),
						List.of("left_leg", "legs3")
				)));

	}

	public static LayerDefinition createHelmet() {
		MeshDefinition mesh = buildGolemBaseLayers();

		PartDefinition head = mesh.getRoot().getChild("head");

		PartDefinition head1 = head.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(0, 64).addBox(-4.5F, -45.0F, -8.0F, 9.0F, 11.0F, 9.0F, new CubeDeformation(0.0F))
				.texOffs(62, 0).addBox(-5.0F, -45.0F, -9.0F, 10.0F, 12.0F, 11.0F, new CubeDeformation(0.0F))
				.texOffs(100, 78).addBox(-5.0F, -53.0F, -9.0F, 10.0F, 8.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(60, 78).addBox(-10.0F, -48.0F, -3.0F, 7.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(20, 102).addBox(3.0F, -48.0F, -3.0F, 7.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 2.0F));

		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createChestplate() {
		MeshDefinition mesh = buildGolemBaseLayers();

		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_arm = mesh.getRoot().getChild("right_arm");
		PartDefinition left_arm = mesh.getRoot().getChild("left_arm");

		PartDefinition body1 = body.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 0).addBox(-9.5F, -33.5F, -6.5F, 19.0F, 13.0F, 12.0F, new CubeDeformation(0.0F))
				.texOffs(0, 25).addBox(-9.5F, -34.0F, -7.0F, 19.0F, 7.0F, 13.0F, new CubeDeformation(0.0F))
				.texOffs(0, 45).addBox(-10.0F, -27.0F, -7.0F, 20.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition body2 = right_arm.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(36, 78).addBox(-13.5F, -13.0F, -3.5F, 5.0F, 8.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(96, 32).addBox(-13.5F, -14.0F, -3.5F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.2F))
				.texOffs(96, 91).addBox(-13.5F, -22.0F, -3.5F, 3.0F, 6.0F, 7.0F, new CubeDeformation(0.2F))
				.texOffs(66, 23).addBox(-15.5F, -35.0F, -4.0F, 7.0F, 11.0F, 8.0F, new CubeDeformation(0.2F))
				.texOffs(100, 64).addBox(-20.0F, -40.0F, 0.0F, 8.0F, 14.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(60, 91).addBox(-21.0F, -18.0F, 0.0F, 9.0F, 18.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition body3 = left_arm.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(0, 99).addBox(10.5F, -22.0F, -3.5F, 3.0F, 6.0F, 7.0F, new CubeDeformation(0.2F))
				.texOffs(66, 42).addBox(8.5F, -35.0F, -4.0F, 7.0F, 11.0F, 8.0F, new CubeDeformation(0.2F))
				.texOffs(0, 84).addBox(8.5F, -13.0F, -3.5F, 5.0F, 8.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(78, 91).addBox(12.0F, -18.0F, 0.0F, 9.0F, 18.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(96, 41).addBox(8.5F, -14.0F, -3.5F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.2F))
				.texOffs(100, 50).addBox(12.0F, -40.0F, 0.0F, 8.0F, 14.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createLeggings() {
		MeshDefinition mesh = buildGolemBaseLayers();

		PartDefinition body = mesh.getRoot().getChild("body");
		PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
		PartDefinition left_leg = mesh.getRoot().getChild("left_leg");

		PartDefinition legs1 = body.addOrReplaceChild("legs1", CubeListBuilder.create().texOffs(36, 64).addBox(-5.5F, -21.0F, -4.0F, 11.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

		PartDefinition legs2 = right_leg.addOrReplaceChild("legs2", CubeListBuilder.create().texOffs(74, 61).addBox(-8.0F, -17.0F, -3.5F, 7.0F, 9.0F, 6.0F, new CubeDeformation(-0.2F))
				.texOffs(96, 23).addBox(-8.0F, -10.0F, -3.5F, 7.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 13.0F, 0.0F));

		PartDefinition legs3 = left_leg.addOrReplaceChild("legs3", CubeListBuilder.create().texOffs(74, 76).addBox(1.0F, -17.0F, -3.5F, 7.0F, 9.0F, 6.0F, new CubeDeformation(-0.2F))
				.texOffs(24, 93).addBox(1.0F, -10.0F, -3.5F, 7.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 13.0F, 0.0F));

		return LayerDefinition.create(mesh, 128, 128);
	}

}