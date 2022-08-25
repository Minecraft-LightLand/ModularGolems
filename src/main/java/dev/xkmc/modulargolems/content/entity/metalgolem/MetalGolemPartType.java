package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum MetalGolemPartType implements IGolemPart<MetalGolemPartType> {
	RIGHT, BODY, LEFT, LEG;

	@Override
	public void setupItemRender(PoseStack stack, @Nullable MetalGolemPartType type) {
		stack.mulPose(Vector3f.ZP.rotationDegrees(135));
		stack.mulPose(Vector3f.YP.rotationDegrees(-155));
		if (type == null) {
			float size = 0.375f;
			stack.scale(size, size, size);
			stack.translate(0, -2.2, 0);
		} else if (type == BODY) {
			float size = 0.525f;
			stack.scale(size, size, size);
			stack.translate(0, -1, 0);
		} else if (type == LEG) {
			float size = 0.6f;
			stack.scale(size, size, size);
			stack.translate(0, -2.2, 0);
		} else if (type == LEFT) {
			float size = 0.55f;
			stack.scale(size, size, size);
			stack.translate(-0.7, -1.7, 0);
		}
	}

	@Override
	public MutableComponent getDesc(MutableComponent desc) {
		return Component.translatable("golem_part.metal_golem." + name().toLowerCase(Locale.ROOT), desc).withStyle(ChatFormatting.GREEN);
	}

}
