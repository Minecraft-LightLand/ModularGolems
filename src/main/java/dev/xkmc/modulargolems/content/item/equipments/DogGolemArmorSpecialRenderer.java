package dev.xkmc.modulargolems.content.item.equipments;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.dog.DogArmorRenderer;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemModel;
import dev.xkmc.modulargolems.content.entity.metalgolem.GolemEquipmentRenderer;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface DogGolemArmorSpecialRenderer {

	void render(DogGolemEntity entity, ItemStack stack, PoseStack pose, MultiBufferSource source, int light, float pTick, DogGolemModel renderer);

	interface ProviderItem {

		Optional<DogGolemArmorSpecialRenderer> getSpecialRenderer();

	}

}
