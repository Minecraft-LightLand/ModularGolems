package dev.xkmc.modulargolems.compat.materials.twilightforest.client;

import dev.xkmc.modulargolems.content.client.armor.GolemModelPath;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static dev.xkmc.modulargolems.compat.materials.twilightforest.equipments.TFArmorPaths.*;
import static dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels.buildGolemBaseLayers;

public class NagaArmors {

    public static final ModelLayerLocation HELMET_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "naga_helmet"), "main");
    public static final ModelLayerLocation CHESTPLATE_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "naga_chestplate"), "main");
    public static final ModelLayerLocation SHINGUARD_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "naga_shinguard"), "main");
    public static final ModelLayerLocation BOOTS_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "naga_boots"), "main");

    static {
        GolemModelPath.register(NAGA_HELMETS,
                new GolemModelPath(HELMET_LAYER, List.of(
                        List.of("head", "head1")
                )));

        GolemModelPath.register(NAGA_CHESTPLATES,
                new GolemModelPath(CHESTPLATE_LAYER, List.of(
                        List.of("body", "body1"),
                        List.of("right_arm", "body2"),
                        List.of("left_arm", "body3")
                )));

        GolemModelPath.register(NAGA_LEGGINGS,
                new GolemModelPath(SHINGUARD_LAYER, List.of(
                        List.of("body", "legs1"),
                        List.of("right_leg", "legs2"),
                        List.of("left_leg", "legs3")
                )));
        GolemModelPath.register(NAGA_BOOTS,
                new GolemModelPath(BOOTS_LAYER, List.of(
                        List.of("right_leg", "boots1"),
                        List.of("left_leg", "boots2")
                )));
    }

    public static LayerDefinition createHelmet() {
        MeshDefinition mesh = buildGolemBaseLayers();
        PartDefinition head = mesh.getRoot().getChild("head");

        PartDefinition head1 = head.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(48, 49).addBox(-4.5F, -44.0F, -8.0F, 9.0F, 11.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 2.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    public static LayerDefinition createChestplate() {
        MeshDefinition mesh = buildGolemBaseLayers();
        PartDefinition body = mesh.getRoot().getChild("body");
        PartDefinition right_arm = mesh.getRoot().getChild("right_arm");
        PartDefinition left_arm = mesh.getRoot().getChild("left_arm");

        PartDefinition body1 = body.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 0).addBox(-9.5F, -33.5F, -7.0F, 19.0F, 13.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

        PartDefinition body2 = right_arm.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(0, 49).addBox(-13.5F, -34.0F, -3.5F, 5.0F, 31.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

        PartDefinition body3 = left_arm.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(24, 49).addBox(8.5F, -34.0F, -3.5F, 5.0F, 31.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    // 创建裤子模型，使用iron_golem_iron_wood_armor_set中的legs1、legs2、legs3定义
    public static LayerDefinition createLeggings() {
        MeshDefinition mesh = buildGolemBaseLayers();
        PartDefinition body = mesh.getRoot().getChild("body");
        PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
        PartDefinition left_leg = mesh.getRoot().getChild("left_leg");

        PartDefinition legs1 = body.addOrReplaceChild("legs1", CubeListBuilder.create().texOffs(58, 36).addBox(-5.5F, -20.0F, -5.0F, 11.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

        PartDefinition legs2 = right_leg.addOrReplaceChild("legs2", CubeListBuilder.create(), PartPose.offset(4.0F, 13.0F, 0.0F));

        PartDefinition legs3 = left_leg.addOrReplaceChild("legs3", CubeListBuilder.create(), PartPose.offset(-5.0F, 13.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    public static LayerDefinition createBoots() {
        MeshDefinition mesh = buildGolemBaseLayers();
        PartDefinition body = mesh.getRoot().getChild("body");
        PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
        PartDefinition left_leg = mesh.getRoot().getChild("left_leg");

        PartDefinition boots1 = right_leg.addOrReplaceChild("boots1", CubeListBuilder.create().texOffs(68, 69).addBox(-4.0F, -4.5F, -3.5F, 7.0F, 17.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(68, 93).addBox(-3.5F, 11.0F, -5.5F, 6.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition boots2 = left_leg.addOrReplaceChild("boots2", CubeListBuilder.create().texOffs(84, 93).addBox(-3.5F, 12.0F, -5.5F, 6.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(84, 0).addBox(-4.0F, -3.5F, -3.5F, 7.0F, 17.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

}