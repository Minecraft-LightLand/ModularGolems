package dev.xkmc.modulargolems.content.client.weapon;

import dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPath;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.client.pose.BowPose;
import dev.xkmc.modulargolems.content.client.pose.MetalGolemPose;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import java.util.List;

public class SonicCannonModel {

	public static final ModelLayerLocation SONIC_MAINHAND = new ModelLayerLocation(ModularGolems.loc("sonic_cannon"), "mainhand");
	public static final ModelLayerLocation SONIC_OFFHAND = new ModelLayerLocation(ModularGolems.loc("sonic_cannon"), "offhand");

	static {
		GolemModelPath.register(GolemModelPaths.SONIC_MAINHAND,
				new GolemModelPath(SONIC_MAINHAND, List.of(List.of("right_arm", "bone"))));

		GolemModelPath.register(GolemModelPaths.SONIC_OFFHAND,
				new GolemModelPath(SONIC_OFFHAND, List.of(List.of("left_arm", "bone2"))));

		MetalGolemPose.register(GolemModelPaths.SONIC_OFFHAND, BowPose.BOW);
	}

	public static LayerDefinition createMainHand() {
		MeshDefinition mesh = GolemEquipmentModels.buildGolemBaseLayers();
		PartDefinition right_arm = mesh.getRoot().getChild("right_arm");

		PartDefinition bone = right_arm.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-13.5F, -24.0F, -9.0F, 5.0F, 21.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(22, 17).addBox(-14.5F, -3.0F, -10.0F, 7.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(0, 30).addBox(-14.0F, 2.0F, -9.5F, 6.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(22, 0).addBox(-14.0F, -33.0F, -10.0F, 6.0F, 9.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(26, 30).addBox(-13.0F, -4.0F, -3.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(26, 39).addBox(-12.0F, -9.0F, -3.0F, 2.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(0, 42).addBox(-12.0F, -13.0F, -3.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 32.0F, -1.0F));

		return LayerDefinition.create(mesh, 64, 64);
	}

	public static LayerDefinition createOffhand() {
		MeshDefinition mesh = GolemEquipmentModels.buildGolemBaseLayers();
		PartDefinition left_arm = mesh.getRoot().getChild("left_arm");

		PartDefinition bone2 = left_arm.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(0, 0).addBox(-13.5F, -24.0F, -9.0F, 5.0F, 21.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(22, 17).addBox(-14.5F, -3.0F, -10.0F, 7.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(0, 30).addBox(-14.0F, 2.0F, -9.5F, 6.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
				.texOffs(22, 0).addBox(-14.0F, -33.0F, -10.0F, 6.0F, 9.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(26, 30).addBox(-13.0F, -4.0F, -3.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(26, 39).addBox(-12.0F, -9.0F, -3.0F, 2.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(0, 42).addBox(-12.0F, -13.0F, -3.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(22.0F, 32.0F, -1.0F));

		return LayerDefinition.create(mesh, 64, 64);
	}

}
