package dev.xkmc.modulargolems.content.client.weapon;

import dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPath;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.client.pose.BeaconConnonPose;
import dev.xkmc.modulargolems.content.client.pose.GolemShoulderPose;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import java.util.List;

public class FlameThrowerModel {

	public static final ModelLayerLocation FLAME_RIGHT = new ModelLayerLocation(ModularGolems.loc("flame_thrower"), "right");
	public static final ModelLayerLocation FLAME_LEFT = new ModelLayerLocation(ModularGolems.loc("flame_thrower"), "left");

	static {
		GolemModelPath.register(GolemModelPaths.FLAME_RIGHT,
				new GolemModelPath(FLAME_RIGHT, List.of(List.of("body", "flamethrower"))));

		GolemModelPath.register(GolemModelPaths.FLAME_LEFT,
				new GolemModelPath(FLAME_LEFT, List.of(List.of("body", "flamethrower2"))));

		GolemShoulderPose.register(GolemModelPaths.FLAME_RIGHT, new BeaconConnonPose("flamethrower", -1, 0, 0));
		GolemShoulderPose.register(GolemModelPaths.FLAME_LEFT, new BeaconConnonPose("flamethrower2", 1, 0, 0));
	}


	public static LayerDefinition leftLayer() {
		MeshDefinition meshdefinition = GolemEquipmentModels.buildGolemBaseLayers();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.getChild("body");

		PartDefinition flamethrower2 = body.addOrReplaceChild("flamethrower2", CubeListBuilder.create().texOffs(0, 14).addBox(-4.0F, -7.0F, 2.0F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(28, 27).addBox(-3.0F, -14.0F, 4.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 26).addBox(-3.5F, -11.0F, 2.5F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 26).addBox(-3.5F, -9.0F, 2.5F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(18, 14).addBox(-4.0F, -4.0F, 0.0F, 4.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-4.5F, 0.0F, 2.0F, 5.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 5.0F, 4.0F));

		PartDefinition cube_r2 = flamethrower2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(22, 0).addBox(-1.5F, -8.0F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -13.0F, 5.0F, 0.0F, 0.7854F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	public static LayerDefinition rightLayer() {
		MeshDefinition meshdefinition = GolemEquipmentModels.buildGolemBaseLayers();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.getChild("body");

		PartDefinition flamethrower = body.addOrReplaceChild("flamethrower", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, -7.0F, 2.0F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(28, 27).addBox(0.0F, -14.0F, 4.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 26).addBox(-0.5F, -11.0F, 2.5F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 26).addBox(-0.5F, -9.0F, 2.5F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(18, 14).addBox(-1.0F, -4.0F, 0.0F, 4.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-1.5F, 0.0F, 2.0F, 5.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 5.0F, 4.0F));

		PartDefinition cube_r1 = flamethrower.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(22, 0).addBox(-1.5F, -8.0F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -13.0F, 5.0F, 0.0F, 0.7854F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

}