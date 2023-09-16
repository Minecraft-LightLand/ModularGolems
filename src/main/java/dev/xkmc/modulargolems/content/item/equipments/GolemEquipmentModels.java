package dev.xkmc.modulargolems.content.item.equipments;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import java.util.ArrayList;
import java.util.List;
public class GolemEquipmentModels {

	public record GolemModelPath(ModelLayerLocation mll, List<List<String>> l) {
	}

	public static final List<ModelLayerLocation> LIST = new ArrayList<>();

	public static final ModelLayerLocation HELMET_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "golemguard_helmet"), "main");
	public static final ModelLayerLocation CHESTPLATE_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "golemguard_chestplate"), "main");
	public static final ModelLayerLocation METALGOLEM =new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID,"metalgolem"),"model");

	public static final GolemModelPath HELMETS = new GolemModelPath(HELMET_LAYER, List.of(List.of("head", "main_head")));
	public static final GolemModelPath CHESTPLATES = new GolemModelPath(CHESTPLATE_LAYER, List.of(List.of("body", "main_body")));

	public static MeshDefinition buildGolemBaseLayers() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition partdefinition = mesh.getRoot();
		partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F).texOffs(24, 0).addBox(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F), PartPose.offset(0.0F, -7.0F, -2.0F));
		partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F).texOffs(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -7.0F, 0.0F));
		partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4.0F, 14.0F, 6.0F).texOffs(60,35).addBox(-13.0F, 11.5F, -3.0F, 4.0F, 16.0F, 6.0F), PartPose.offset(0.0F, -7.0F, 0.0F));
		partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(60, 58).addBox(9.0F, -2.5F, -3.0F, 4.0F, 14.0F, 6.0F).texOffs(60,72).addBox(9.0F, 11.5F, -3.0F, 4.0F, 16.0F, 6.0F), PartPose.offset(0.0F, -7.0F, 0.0F));
		partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F), PartPose.offset(-4.0F, 11.0F, 0.0F));
		partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(60, 0).mirror().addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F), PartPose.offset(5.0F, 11.0F, 0.0F));
		return mesh;
	}

	public static LayerDefinition createHelmetLayer() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition root = mesh.getRoot().getChild("head");
		root.addOrReplaceChild("main_head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -5.0F, -4.5F, 10.0F, 10.0F, 10.0F), PartPose.offset(0.0F, -7.0F, -2.0F));
		return LayerDefinition.create(mesh, 64, 64);
	}

	public static LayerDefinition createChestplateLayer() {
		MeshDefinition mesh = buildGolemBaseLayers();
		PartDefinition root = mesh.getRoot().getChild("body");
		root.addOrReplaceChild("main_body", CubeListBuilder.create().texOffs(0, 0).addBox(-9.001F, 5.0F, -6.001F, 18.001F, 12.001F, 11.001F), PartPose.offset(0.0F, -7.0F, 0.0F));
		return LayerDefinition.create(mesh, 64, 64);
	}

	public static LayerDefinition createGolemLayer() {
		MeshDefinition mesh = buildGolemBaseLayers();
		return LayerDefinition.create(mesh,128,128);
	}

	public static void registerArmorLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
		LIST.add(HELMET_LAYER);
		event.registerLayerDefinition(HELMET_LAYER, GolemEquipmentModels::createHelmetLayer);
		LIST.add(CHESTPLATE_LAYER);
		event.registerLayerDefinition(CHESTPLATE_LAYER, GolemEquipmentModels::createChestplateLayer);
		event.registerLayerDefinition(METALGOLEM,GolemEquipmentModels::createGolemLayer);
	}
}

