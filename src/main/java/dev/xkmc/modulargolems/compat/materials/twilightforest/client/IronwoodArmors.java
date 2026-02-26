package dev.xkmc.modulargolems.compat.materials.twilightforest.client;

import dev.xkmc.modulargolems.content.client.armor.GolemModelPath;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels.buildGolemBaseLayers;
import static dev.xkmc.modulargolems.compat.materials.twilightforest.equipments.TFArmorPaths.*;

public class IronwoodArmors {

    // 定义模型层位置，使用"ironwood"作为前缀
    public static final ModelLayerLocation HELMET_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "ironwood_helmet"), "main");
    public static final ModelLayerLocation CHESTPLATE_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "ironwood_chestplate"), "main");
    public static final ModelLayerLocation SHINGUARD_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "ironwood_shinguard"), "main");
    public static final ModelLayerLocation BOOTS_LAYER = new ModelLayerLocation(new ResourceLocation(ModularGolems.MODID, "ironwood_boots"), "main");

    // 静态块注册模型路径（假设存在相应的GolemModelPaths常量，如IRONWOOD_HELMETS等）
    static {
        // 注：以下常量（如IRONWOOD_HELMETS）需要在实际项目中定义，这里仅为示例格式
        GolemModelPath.register(IRONWOOD_HELMETS,
                new GolemModelPath(HELMET_LAYER, List.of(
                        List.of("head", "head1")
                )));

        GolemModelPath.register(IRONWOOD_CHESTPLATES,
                new GolemModelPath(CHESTPLATE_LAYER, List.of(
                        List.of("body", "body1"),
                        List.of("right_arm", "body2"),
                        List.of("left_arm", "body3")
                )));

        GolemModelPath.register(IRONWOOD_LEGGINGS,
                new GolemModelPath(SHINGUARD_LAYER, List.of(
                        List.of("body", "legs1"),
                        List.of("right_leg", "legs2"),
                        List.of("left_leg", "legs3")
                )));
        GolemModelPath.register(IRONWOOD_BOOTS,
                new GolemModelPath(BOOTS_LAYER, List.of(
                        List.of("right_leg", "legs2"),
                        List.of("left_leg", "legs3")
                )));
    }

    // 创建头盔模型，使用iron_golem_iron_wood_armor_set中的head1定义
    public static LayerDefinition createHelmet() {
        MeshDefinition mesh = buildGolemBaseLayers();
        PartDefinition head = mesh.getRoot().getChild("head");

        // 从iron_golem_iron_wood_armor_set复制head1定义
        PartDefinition head1 = head.addOrReplaceChild("head1",
                CubeListBuilder.create()
                        .texOffs(73, 29)
                        .addBox(-4.5F, -44.0F, -8.0F, 9.0F, 11.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 31.0F, 2.0F)
        );
        return LayerDefinition.create(mesh, 128, 128);
    }

    // 创建胸甲模型，使用iron_golem_iron_wood_armor_set中的body1、body2、body3定义
    public static LayerDefinition createChestplate() {
        MeshDefinition mesh = buildGolemBaseLayers();
        PartDefinition body = mesh.getRoot().getChild("body");
        PartDefinition right_arm = mesh.getRoot().getChild("right_arm");
        PartDefinition left_arm = mesh.getRoot().getChild("left_arm");

        // 从iron_golem_iron_wood_armor_set复制body1定义
        PartDefinition body1 = body.addOrReplaceChild("body1",
                CubeListBuilder.create()
                        .texOffs(4, 1)
                        .addBox(-9.5F, -33.5F, -7.0F, 19.0F, 13.0F, 13.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 31.0F, 0.0F)
        );

        // 从iron_golem_iron_wood_armor_set复制body2定义
        PartDefinition body2 = right_arm.addOrReplaceChild("body2",
                CubeListBuilder.create()
                        .texOffs(1, 31)
                        .addBox(-13.5F, -34.0F, -3.5F, 5.0F, 31.0F, 7.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 31.0F, 0.0F)
        );

        // 从iron_golem_iron_wood_armor_set复制body3定义
        PartDefinition body3 = left_arm.addOrReplaceChild("body3",
                CubeListBuilder.create()
                        .texOffs(43, 31)
                        .addBox(8.5F, -34.0F, -3.5F, 5.0F, 31.0F, 7.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 31.0F, 0.0F)
        );
        return LayerDefinition.create(mesh, 128, 128);
    }

    // 创建裤子模型，使用iron_golem_iron_wood_armor_set中的legs1、legs2、legs3定义
    public static LayerDefinition createLeggings() {
        MeshDefinition mesh = buildGolemBaseLayers();
        PartDefinition body = mesh.getRoot().getChild("body");
        PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
        PartDefinition left_leg = mesh.getRoot().getChild("left_leg");

        // 从iron_golem_iron_wood_armor_set复制legs1定义
        PartDefinition legs1 = body.addOrReplaceChild("legs1",
                CubeListBuilder.create()
                        .texOffs(72, 7)
                        .addBox(-5.5F, -21.0F, -5.0F, 11.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 31.0F, 0.0F)
        );

        // 从iron_golem_iron_wood_armor_set复制legs2定义
        PartDefinition legs2 = right_leg.addOrReplaceChild("legs2",
                CubeListBuilder.create()
                        .texOffs(73, 54)
                        .addBox(-8.0F, -16.5F, -3.5F, 7.0F, 17.0F, 7.0F, new CubeDeformation(0.0F)),
                        PartPose.offset(4.0F, 13.0F, 0.0F)
        );

        // 从iron_golem_iron_wood_armor_set复制legs3定义
        PartDefinition legs3 = left_leg.addOrReplaceChild("legs3",
                CubeListBuilder.create()
                        .texOffs(73, 80)
                        .addBox(1.0F, -16.5F, -3.5F, 7.0F, 17.0F, 7.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-5.0F, 13.0F, 0.0F)
        );
        return LayerDefinition.create(mesh, 128, 128);
    }

    public static LayerDefinition createBoots() {
        MeshDefinition mesh = buildGolemBaseLayers();
        PartDefinition body = mesh.getRoot().getChild("body");
        PartDefinition right_leg = mesh.getRoot().getChild("right_leg");
        PartDefinition left_leg = mesh.getRoot().getChild("left_leg");


        // 从iron_golem_iron_wood_armor_set复制legs2定义
        PartDefinition legs2 = right_leg.addOrReplaceChild("legs2",
                CubeListBuilder.create()
                        .texOffs(3, 78)
                        .addBox(-7.5F, -1.0F, -4.5F, 6.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(22, 55)
                        .addBox(-8.0F, -16.5F, -3.5F, 7.0F, 17.0F, 7.0F, new CubeDeformation(0.0F)),
                PartPose.offset(4.0F, 13.0F, 0.0F));

        // 从iron_golem_iron_wood_armor_set复制legs3定义
        PartDefinition legs3 = left_leg.addOrReplaceChild("legs3",
                CubeListBuilder.create()
                        .texOffs(22, 82)
                        .addBox(1.0F, -16.5F, -3.5F, 7.0F, 17.0F, 7.0F, new CubeDeformation(0.0F))
                        .texOffs(3, 78)
                        .addBox(1.5F, -1.0F, -4.5F, 6.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-5.0F, 13.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

}