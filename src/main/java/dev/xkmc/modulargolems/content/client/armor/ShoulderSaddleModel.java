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
 *   <li>单边鞍：只画一只侧翼（左/右各一层），对应只画了那半边的单边纹理；</li>
 *   <li>整体鞍：鞍身 + 两只侧翼，对应"全"纹理。</li>
 * </ul>
 * 模型空间里负 X 是实体右侧（与玩家模型 right_arm 同侧），正 X 是左侧。
 */
public class ShoulderSaddleModel {

	public static final ModelLayerLocation RIGHT_LAYER = new ModelLayerLocation(ModularGolems.loc("shoulder_saddle"), "right");
	public static final ModelLayerLocation LEFT_LAYER = new ModelLayerLocation(ModularGolems.loc("shoulder_saddle"), "left");
	public static final ModelLayerLocation FULL_LAYER = new ModelLayerLocation(ModularGolems.loc("shoulder_saddle"), "full");
	public static final ResourceLocation SADDLE_RIGHT = ModularGolems.loc("shoulder_saddle_right");
	public static final ResourceLocation SADDLE_LEFT = ModularGolems.loc("shoulder_saddle_left");
	public static final ResourceLocation SADDLE_FULL = ModularGolems.loc("shoulder_saddle_full");

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
	 * 单边鞍用几何和整体鞍相同，靠左/右纹理来"只显示那一半"：
	 * 左/右纹理只画了本侧侧翼和主鞍身上的一小段卡扣，其余面透明，
	 * 所以整体几何 + 单边纹理 = 完整的单边鞍（有鞍座也有底下的卡扣），且不会多渲染中间部分。
	 */
	private static LayerDefinition createSide(boolean left) {
		return createFullLayer();
	}

	public static LayerDefinition createFullLayer() {
		MeshDefinition mesh = GolemEquipmentModels.buildGolemBaseLayers();
		PartDefinition body = mesh.getRoot().getChild("body");
		body.addOrReplaceChild("saddle", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-9.0F, -33.0F, -6.0F, 18.0F, 12.0F, 11.0F, new CubeDeformation(0.1F))
				.texOffs(0, 46).addBox(-5.0F, -35.0F, 0.0F, 10.0F, 10.0F, 6.0F, new CubeDeformation(-0.5F))
				.texOffs(32, 46).addBox(-5.0F, -32.1F, -7.0F, 10.0F, 7.0F, 2.0F, new CubeDeformation(-0.4F))
				.texOffs(0, 23).addBox(-10.0F, -35.0F, -7.0F, 6.0F, 10.0F, 13.0F, new CubeDeformation(-0.5F))
				.texOffs(38, 23).addBox(4.0F, -35.0F, -7.0F, 6.0F, 10.0F, 13.0F, new CubeDeformation(-0.5F)),
				PartPose.offset(0.0F, 31.0F, 0.0F));
		return LayerDefinition.create(mesh, 128, 128);
	}

}
