package dev.xkmc.modulargolems.content.item.equipments;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.metalgolem.GolemEquipmentRenderer;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface GolemItemSpecialRenderer {

	void render(MetalGolemEntity entity, ItemStack stack, PoseStack pose, MultiBufferSource source, int light, float pTick, GolemEquipmentRenderer renderer);

	interface ProviderItem {

		Optional<GolemItemSpecialRenderer> getSpecialRenderer();

	}

}
