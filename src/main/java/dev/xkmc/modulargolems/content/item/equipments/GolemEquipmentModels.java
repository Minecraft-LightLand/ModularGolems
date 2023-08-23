package dev.xkmc.modulargolems.content.item.equipments;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class GolemEquipmentModels {
    record GolemModelPath(ModelLayerLocation mll,List<List<String>> l){
    }
    public static final ModelLayerLocation HELMET_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID,"golemguard_helmet"), "main");
    public static final ModelLayerLocation CHESTPLATE_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID,"golemguard_chestplate"), "main");
    public static final GolemModelPath HELMETS =new GolemModelPath(HELMET_LAYER,List.of(List.of("head","main_head")));
    public static final GolemModelPath CHESTPLATES =new GolemModelPath(CHESTPLATE_LAYER,List.of(List.of("body","main_body")));
    public static MeshDefinition BuildGolemBaseLayers(){
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition partdefinition = mesh.getRoot();
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F).texOffs(24, 0).addBox(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F), PartPose.offset(0.0F, -7.0F, -2.0F));
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F).texOffs(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -7.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F), PartPose.offset(0.0F, -7.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(60, 58).addBox(9.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F), PartPose.offset(0.0F, -7.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F), PartPose.offset(-4.0F, 11.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(60, 0).mirror().addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F), PartPose.offset(5.0F, 11.0F, 0.0F));
        return mesh;
    }
    public static LayerDefinition createHelmetLayer(){
        MeshDefinition mesh=BuildGolemBaseLayers();
        PartDefinition root1 = mesh.getRoot().getChild("head");
        PartDefinition Mainbody1 = root1.addOrReplaceChild("main_head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -12.1F, -6.5F, 10.0F, 10.0F, 10.0F), PartPose.offset(0.0F, -7.0F, -2.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }
    public static LayerDefinition createChesplateLayer(){
        MeshDefinition mesh=BuildGolemBaseLayers();
        PartDefinition root2=mesh.getRoot().getChild("body");
        PartDefinition Mainbody2 = root2.addOrReplaceChild("main_body", CubeListBuilder.create().texOffs(0, 0).addBox(-9.1F, -2.1F, -6.1F, 18.2F, 12.1F, 11.2F), PartPose.offset(0.0F, -7.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

}

