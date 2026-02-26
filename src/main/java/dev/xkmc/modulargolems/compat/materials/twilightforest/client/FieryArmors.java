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

public class FieryArmors {

    public static final ModelLayerLocation HELMET_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "fiery_helmet"), "main");
    public static final ModelLayerLocation CHESTPLATE_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "fiery_chestplate"), "main");
    public static final ModelLayerLocation SHINGUARD_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "fiery_shinguard"), "main");
    public static final ModelLayerLocation BOOTS_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "fiery_boots"), "main");

    static {
        GolemModelPath.register(FIERY_HELMETS,
                new GolemModelPath(HELMET_LAYER, List.of(
                        List.of("head", "head1")
                )));

        GolemModelPath.register(FIERY_CHESTPLATES,
                new GolemModelPath(CHESTPLATE_LAYER, List.of(
                        List.of("body", "body1"),
                        List.of("right_arm", "body2"),
                        List.of("left_arm", "body3")
                )));

        GolemModelPath.register(FIERY_LEGGINGS,
                new GolemModelPath(SHINGUARD_LAYER, List.of(
                        List.of("body", "legs1"),
                        List.of("right_leg", "legs2"),
                        List.of("left_leg", "legs3")
                )));
        GolemModelPath.register(FIERY_BOOTS,
                new GolemModelPath(BOOTS_LAYER, List.of(
                        List.of("right_leg", "boots1"),
                        List.of("left_leg", "boots2")
                )));
    }

    public static LayerDefinition createHelmet() {
        MeshDefinition mesh = buildGolemBaseLayers();
        PartDefinition head = mesh.getRoot().getChild("head");

        PartDefinition head1 = head.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -43.0F, -8.5F, 3.0F, 3.0F, 2.0F, new CubeDeformation(-0.2F))
                .texOffs(77, 90).addBox(-6.0F, -35.75F, -8.5F, 12.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(108, 0).addBox(-4.5F, -39.5F, -8.75F, 9.0F, 4.0F, 1.0F, new CubeDeformation(-0.3F))
                .texOffs(27, 64).addBox(-5.0F, -44.0F, -8.0F, 10.0F, 10.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 2.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    public static LayerDefinition createChestplate() {
        MeshDefinition mesh = buildGolemBaseLayers();
        PartDefinition body = mesh.getRoot().getChild("body");
        PartDefinition right_arm = mesh.getRoot().getChild("right_arm");
        PartDefinition left_arm = mesh.getRoot().getChild("left_arm");

        PartDefinition body1 = body.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 0).addBox(-9.5F, -33.5F, -7.0F, 19.0F, 13.0F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.5F, -29.5F, -7.5F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 31.0F, 0.0F));

        PartDefinition body2 = right_arm.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(0, 68).addBox(-13.5F, -34.0F, -3.5F, 3.0F, 10.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(70, 39).addBox(-13.5F, -17.0F, -3.5F, 5.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

        PartDefinition body3 = left_arm.addOrReplaceChild("body3", CubeListBuilder.create().texOffs(0, 68).addBox(10.5F, -34.0F, -3.5F, 3.0F, 10.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(70, 39).addBox(8.5F, -17.0F, -3.5F, 5.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    public static LayerDefinition createLeggings() {
        MeshDefinition mesh = buildGolemBaseLayers();
        PartDefinition body = mesh.getRoot().getChild("body");
        PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
        PartDefinition left_leg = mesh.getRoot().getChild("left_leg");

        PartDefinition legs1 = body.addOrReplaceChild("legs1", CubeListBuilder.create().texOffs(0, 27).addBox(-5.5F, -21.0F, -5.0F, 11.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 31.0F, 0.0F));
        PartDefinition leg2 = right_leg.addOrReplaceChild("legs2", CubeListBuilder.create().texOffs(66, 64).addBox(-8.0F, -13.5F, -3.5F, 7.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 13.0F, 0.0F));
        PartDefinition leg3 = left_leg.addOrReplaceChild("legs3", CubeListBuilder.create().texOffs(65, 13).addBox(1.0F, -13.5F, -3.5F, 7.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 13.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    public static LayerDefinition createBoots() {
        MeshDefinition mesh = buildGolemBaseLayers();
        PartDefinition body = mesh.getRoot().getChild("body");
        PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
        PartDefinition left_leg = mesh.getRoot().getChild("left_leg");

        PartDefinition boots1 = right_leg.addOrReplaceChild("boots1", CubeListBuilder.create().texOffs(0, 54).addBox(-8.0F, -5.5F, -3.5F, 7.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 13.0F, 0.0F));
        PartDefinition boots2 = left_leg.addOrReplaceChild("boots2", CubeListBuilder.create().texOffs(41, 50).addBox(1.0F, -5.5F, -3.5F, 7.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 13.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

}