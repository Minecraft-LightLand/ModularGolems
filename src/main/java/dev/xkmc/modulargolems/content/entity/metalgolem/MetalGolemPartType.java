package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.core.GolemSlot;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.render.GolemTransformType;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum MetalGolemPartType implements IGolemPart<MetalGolemPartType> {
	RIGHT(GolemSlot.LEFT),
	BODY(GolemSlot.MIDDLE),
	LEFT(GolemSlot.RIGHT),
	LEG(GolemSlot.DOWN);

	private final GolemSlot slot;

	MetalGolemPartType(GolemSlot slot) {
		this.slot = slot;
	}

	@Override
	public GolemSlot getSlot() {
		return slot;
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
