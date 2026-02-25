package dev.xkmc.modulargolems.compat.materials.twilightforest.client;

import dev.xkmc.modulargolems.content.client.armor.GolemModelPath;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static dev.xkmc.modulargolems.compat.materials.twilightforest.armor.TFArmorPaths.*;
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
                        List.of("right_leg", "legs2"),
                        List.of("left_leg", "legs3")
                )));
    }

    public static LayerDefinition createHelmet() {
        MeshDefinition mesh = buildGolemBaseLayers();
        PartDefinition head = mesh.getRoot().getChild("head");

        return LayerDefinition.create(mesh, 128, 128);
    }

    public static LayerDefinition createChestplate() {
        MeshDefinition mesh = buildGolemBaseLayers();
        PartDefinition body = mesh.getRoot().getChild("body");
        PartDefinition right_arm = mesh.getRoot().getChild("right_arm");
        PartDefinition left_arm = mesh.getRoot().getChild("left_arm");

        return LayerDefinition.create(mesh, 128, 128);
    }

    public static LayerDefinition createLeggings() {
        MeshDefinition mesh = buildGolemBaseLayers();
        PartDefinition body = mesh.getRoot().getChild("body");
        PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
        PartDefinition left_leg = mesh.getRoot().getChild("left_leg");

        return LayerDefinition.create(mesh, 128, 128);
    }

    public static LayerDefinition createBoots() {
        MeshDefinition mesh = buildGolemBaseLayers();
        PartDefinition body = mesh.getRoot().getChild("body");
        PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
        PartDefinition left_leg = mesh.getRoot().getChild("left_leg");


        return LayerDefinition.create(mesh, 128, 128);
    }

}