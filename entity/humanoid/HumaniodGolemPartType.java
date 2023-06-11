package dev.xkmc.modulargolems.content.entity.humanoid;

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

public enum HumaniodGolemPartType implements IGolemPart<HumaniodGolemPartType> {
	BODY, ARMS, LEGS;

	@Override
	public MutableComponent getDesc(MutableComponent desc) {
		return Component.translatable("golem_part.humanoid_golem." + name().toLowerCase(Locale.ROOT), desc).withStyle(ChatFormatting.GREEN);
	}

	@Override
	public GolemPart<?, HumaniodGolemPartType> toItem() {
		return switch (this) {
			case BODY -> GolemItems.HUMANOID_BODY.get();
			case ARMS -> GolemItems.HUMANOID_ARMS.get();
			case LEGS -> GolemItems.HUMANOID_LEGS.get();
		};
	}

	@Override
	public void setupItemRender(PoseStack stack, ItemTransforms.TransformType transform, @Nullable HumaniodGolemPartType part) {
		HumanoidGolemRenderer.transform(stack, transform, part);
	}

}
