package dev.xkmc.modulargolems.content.item.equipments;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModel;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;

import static dev.xkmc.modulargolems.content.item.equipments.GolemEquipmentModels.CHESTPLATE_LAYER;
import static dev.xkmc.modulargolems.content.item.equipments.GolemEquipmentModels.HELMET_LAYER;
public class GolemEquipmentRenderer extends RenderLayer<MetalGolemEntity, MetalGolemModel<LivingEntity>> {
   public HashMap<ModelLayerLocation, ModelPart> map=new HashMap<>();
    public GolemEquipmentRenderer (RenderLayerParent<MetalGolemEntity, MetalGolemModel<LivingEntity>> r,EntityRendererProvider.Context e) {
        super(r);
        map.put(HELMET_LAYER,e.bakeLayer(HELMET_LAYER));
        map.put(CHESTPLATE_LAYER,e.bakeLayer(CHESTPLATE_LAYER));
    }
    @Override
    public void render(@NotNull PoseStack stack, MultiBufferSource source, int i, @NotNull MetalGolemEntity entity, float p_117353_, float p_117354_, float p_117355_, float p_117356_, float p_117357_, float p_117358_) {
        map.get(GolemEquipmentModels.HELMET_LAYER).getChild("head").getChild("main_head").render(stack, source.getBuffer(RenderType.entityCutoutNoCull(new ResourceLocation(ModularGolems.MODID,"textures/item/equipments/golemguard_helmet"))), i, OverlayTexture.NO_OVERLAY,1,1,1,1);
        map.get(GolemEquipmentModels.HELMET_LAYER).getChild("head").getChild("main_head").render(stack, source.getBuffer(RenderType.entityCutoutNoCull(new ResourceLocation(ModularGolems.MODID,"textures/item/equipments/golemguard_helmet"))), i, OverlayTexture.NO_OVERLAY,1,1,1,1);
        }
}

