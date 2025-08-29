package dev.xkmc.modulargolems.content.client.armor;

import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels.buildGolemBaseLayers;
import static dev.xkmc.modulargolems.content.client.armor.GolemModelPaths.*;

public class GolemBoots {

	public static final ModelLayerLocation DIAMOND_BOOTS_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "wind_spirit_boots"), "main");
	public static final ModelLayerLocation BEACON_BOOTS_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "beacon_boots"), "main");
	public static final ModelLayerLocation NETHERITE_BOOTS_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "barbaric_vanguard_boots"), "main");

	static {
		GolemModelPath.register(BOOTS_DIAMOND,
				new GolemModelPath(DIAMOND_BOOTS_LAYER, List.of(
						List.of("right_leg", "boots1"),
						List.of("right_leg", "boots1", "boots2"),
						List.of("right_leg", "boots1", "boots3"),
						List.of("right_leg", "boots1", "boots4"),
						List.of("left_leg", "boots5"),
						List.of("left_leg", "boots5", "boots6"),
						List.of("left_leg", "boots5", "boots7"),
						List.of("left_leg", "boots5", "boots8")
				)));

		GolemModelPath.register(BOOTS_NETHERITE,
				new GolemModelPath(NETHERITE_BOOTS_LAYER, List.of(
						List.of("right_leg", "boots1"),
						List.of("right_leg", "boots1", "boots2"),
						List.of("left_leg", "boots3"),
						List.of("left_leg", "boots3", "boots4")
				)));

		GolemModelPath.register(BOOTS_BEACON,
				new GolemModelPath(BEACON_BOOTS_LAYER, List.of(
						List.of("right_leg", "boots1"),
						List.of("left_leg", "boots2")
				)));

	}


	public static LayerDefinition createDiamondBoots() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition partdefinition = mesh.getRoot();

		PartDefinition right_leg = partdefinition.getChild("right_leg");

		PartDefinition left_leg = partdefinition.getChild("left_leg");

		PartDefinition bone = right_leg.addOrReplaceChild("boots1", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -4.9F, -3.5F, 7.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(0, 22).addBox(-7.5F, -1.9F, -4.5F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 13.0F, 0.0F));

		PartDefinition boots5_r1 = bone.addOrReplaceChild("boots2", CubeListBuilder.create().texOffs(26, 0).addBox(-1.0F, -0.9F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -3.0F, 3.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition boots4_r1 = bone.addOrReplaceChild("boots3", CubeListBuilder.create().texOffs(0, 25).addBox(-1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -4.0F, 2.0F, 1.1345F, 0.0F, 0.0F));

		PartDefinition boots3_r1 = bone.addOrReplaceChild("boots4", CubeListBuilder.create().texOffs(16, 25).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -2.0F, 2.0F, 0.829F, 0.0F, 0.0F));

		PartDefinition bone2 = left_leg.addOrReplaceChild("boots5", CubeListBuilder.create().texOffs(0, 11).addBox(1.0F, -4.9F, -3.5F, 7.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(14, 22).addBox(1.5F, -1.9F, -4.5F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 13.0F, 0.0F));

		PartDefinition boots10_r1 = bone2.addOrReplaceChild("boots6", CubeListBuilder.create().texOffs(26, 3).addBox(0.0F, -0.8F, -1.1F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, -3.0F, 3.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition boots9_r1 = bone2.addOrReplaceChild("boots7", CubeListBuilder.create().texOffs(8, 25).addBox(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, -4.0F, 2.0F, 1.1781F, 0.0F, 0.0F));

		PartDefinition boots8_r1 = bone2.addOrReplaceChild("boots8", CubeListBuilder.create().texOffs(22, 25).addBox(-2.0F, -4.0F, 1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, 1.0F, 2.0F, 0.829F, 0.0F, 0.0F));

		return LayerDefinition.create(mesh, 32, 32);
	}

	public static LayerDefinition createNetheriteBoots() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition partdefinition = mesh.getRoot();

		PartDefinition right_leg = partdefinition.getChild("right_leg");

		PartDefinition bone = right_leg.addOrReplaceChild("boots1", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -4.9F, -3.5F, 7.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(20, 22).addBox(-7.5F, -1.9F, -4.5F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 13.0F, 0.0F));

		PartDefinition boots2_r1 = bone.addOrReplaceChild("boots2", CubeListBuilder.create().texOffs(0, 22).addBox(0.0F, -4.5F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, -1.0F, 2.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition left_leg = partdefinition.getChild("left_leg");

		PartDefinition bone2 = left_leg.addOrReplaceChild("boots3", CubeListBuilder.create().texOffs(0, 11).addBox(1.0F, -4.9F, -3.5F, 7.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(20, 25).addBox(1.5F, -1.9F, -4.5F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 13.0F, 0.0F));

		PartDefinition boots4_r1 = bone2.addOrReplaceChild("boots4", CubeListBuilder.create().texOffs(10, 22).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, -2.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		return LayerDefinition.create(mesh, 64, 64);
	}


	public static LayerDefinition createBeaconBoots() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition partdefinition = mesh.getRoot();

		PartDefinition right_leg = partdefinition.getChild("right_leg");

		PartDefinition left_leg = partdefinition.getChild("left_leg");


		PartDefinition bone = right_leg.addOrReplaceChild("boots1", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -5.9F, -3.5F, 7.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(26, 7).addBox(-8.5F, -2.8F, 0.0F, 8.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(0, 24).addBox(-8.5F, -0.8F, -6.0F, 8.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(28, 19).addBox(-7.0F, -3.0F, -5.6F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 13.0F, 0.0F));

		PartDefinition bone2 = left_leg.addOrReplaceChild("boots2", CubeListBuilder.create().texOffs(0, 12).addBox(1.0F, -5.9F, -3.5F, 7.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(26, 13).addBox(0.5F, -2.8F, 0.0F, 8.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(26, 0).addBox(0.5F, -0.8F, -6.0F, 8.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(28, 25).addBox(2.0F, -3.0F, -5.6F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 13.0F, 0.0F));

		return LayerDefinition.create(mesh, 64, 64);
	}


}
