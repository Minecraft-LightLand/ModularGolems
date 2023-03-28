package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum MetalGolemPartType implements IGolemPart<MetalGolemPartType> {
	RIGHT, BODY, LEFT, LEG;

	@Override
	public void setupItemRender(PoseStack stack, ItemTransforms.TransformType transform, @Nullable MetalGolemPartType part) {
		MetalGolemRenderer.transform(stack, transform, part);
	}

	@Override
	public MutableComponent getDesc(MutableComponent desc) {
		return Component.translatable("golem_part.metal_golem." + name().toLowerCase(Locale.ROOT), desc).withStyle(ChatFormatting.GREEN);
	}

	@Override
	public GolemPart<?, MetalGolemPartType> toItem() {
		return switch (this) {
			case BODY -> GolemItems.GOLEM_BODY.get();
			case LEG -> GolemItems.GOLEM_LEGS.get();
			default -> GolemItems.GOLEM_ARM.get();
		};
	}

}
