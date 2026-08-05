package dev.xkmc.modulargolems.content.entity.humanoid;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.core.GolemSlot;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum HumanoidGolemPartType implements IGolemPart<HumanoidGolemPartType> {
	BODY(GolemSlot.UP),
	ARMS(GolemSlot.MIDDLE),
	LEGS(GolemSlot.DOWN);

	private final GolemSlot slot;

	HumanoidGolemPartType(GolemSlot slot) {
		this.slot = slot;
	}

	@Override
	public GolemSlot getSlot() {
		return slot;
	}

	@Override
	public MutableComponent getDesc(MutableComponent desc) {
		return Component.translatable("golem_part.humanoid_golem." + name().toLowerCase(Locale.ROOT), desc).withStyle(ChatFormatting.GREEN);
	}

	@Override
	public GolemPart<?, HumanoidGolemPartType> toItem() {
		return switch (this) {
			case BODY -> GolemItems.HUMANOID_BODY.get();
			case ARMS -> GolemItems.HUMANOID_ARMS.get();
			case LEGS -> GolemItems.HUMANOID_LEGS.get();
		};
	}

	@Override
	public void setupItemRender(PoseStack stack, ItemDisplayContext transform, @Nullable HumanoidGolemPartType part) {
		HumanoidGolemRenderer.transform(stack, transform, part);
	}

}
