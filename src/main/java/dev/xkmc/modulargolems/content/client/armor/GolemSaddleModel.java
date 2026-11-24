package dev.xkmc.modulargolems.content.client.armor;

import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * 肩鞍模型，三套：
 * <ul>
 *   <li>单边鞍：鞍底 + 本侧侧翼（左/右各一层），对应只画了那半边的单边纹理；</li>
 *   <li>整体鞍：鞍底 + 两侧侧翼 + 前/后鞍桥，对应"全"纹理。</li>
 * </ul>
 * 模型空间里负 X 是实体右侧（与玩家模型 right_arm 同侧），正 X 是左侧。
 */
public class GolemSaddleModel {

	public static final ModelLayerLocation RIGHT_LAYER = new ModelLayerLocation(ModularGolems.loc("golem_saddle"), "right");
	public static final ModelLayerLocation LEFT_LAYER = new ModelLayerLocation(ModularGolems.loc("golem_saddle"), "left");
	public static final ModelLayerLocation FULL_LAYER = new ModelLayerLocation(ModularGolems.loc("golem_saddle"), "full");
	public static final ResourceLocation SADDLE_RIGHT = ModularGolems.loc("golem_saddle_right");
	public static final ResourceLocation SADDLE_LEFT = ModularGolems.loc("golem_saddle_left");
	public static final ResourceLocation SADDLE_FULL = ModularGolems.loc("golem_saddle_full");

	static {
		GolemModelPath.register(SADDLE_RIGHT, new GolemModelPath(RIGHT_LAYER, List.of(List.of("body", "saddle"))));
		GolemModelPath.register(SADDLE_LEFT, new GolemModelPath(LEFT_LAYER, List.of(List.of("body", "saddle"))));
		GolemModelPath.register(SADDLE_FULL, new GolemModelPath(FULL_LAYER, List.of(List.of("body", "saddle"))));
	}

	public static LayerDefinition createRightLayer() {
		return createSide(false);
	}

	public static LayerDefinition createLeftLayer() {
		return createSide(true);
	}

	/**
	 * 单边鞍 = 鞍底 + 本侧侧翼。单边纹理只画了这两部分，
	 * 其余（另一侧侧翼、前后鞍桥）保持透明，所以不会多渲染中间/另一侧。
	 */
	private static LayerDefinition createSide(boolean left) {
		MeshDefinition mesh = GolemEquipmentModels.buildGolemBaseLayers();
		PartDefinition body = mesh.getRoot().getChild("body");
		CubeListBuilder cubes = CubeListBuilder.create()
				.texOffs(0, 0).addBox(-9.0F, -24.0F, -6.0F, 18.0F, 3.0F, 11.0F, new CubeDeformation(0.2F));
		if (left) {
			cubes.texOffs(0, 38).addBox(5.0F, -34.0F, -7.5F, 5.0F, 10.0F, 14.0F, new CubeDeformation(0.0F));
		} else {
			cubes.texOffs(0, 14).addBox(-10.0F, -34.0F, -7.5F, 5.0F, 10.0F, 14.0F, new CubeDeformation(0.0F));
		}
		body.addOrReplaceChild("saddle", cubes, PartPose.offset(0.0F, 31.0F, 0.0F));
		return LayerDefinition.create(mesh, 128, 128);
	}

	public static LayerDefinition createFullLayer() {
		MeshDefinition mesh = GolemEquipmentModels.buildGolemBaseLayers();
		PartDefinition body = mesh.getRoot().getChild("body");
		body.addOrReplaceChild("saddle", CubeListBuilder.create()
				.texOffs(0, 14).addBox(-10.0F, -34.0F, -7.5F, 5.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(0, 38).addBox(5.0F, -34.0F, -7.5F, 5.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(38, 30).addBox(-5.0F, -33.0F, -7.5F, 10.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(38, 14).addBox(-5.0F, -34.0F, 1.5F, 10.0F, 11.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-9.0F, -24.0F, -6.0F, 18.0F, 3.0F, 11.0F, new CubeDeformation(0.2F)),
				PartPose.offset(0.0F, 31.0F, 0.0F));
		return LayerDefinition.create(mesh, 128, 128);
	}

}
