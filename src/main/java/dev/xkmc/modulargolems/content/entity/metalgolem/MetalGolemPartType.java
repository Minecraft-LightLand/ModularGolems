package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import org.jetbrains.annotations.Nullable;

public enum MetalGolemPartType implements IGolemPart<MetalGolemPartType> {
	LEFT, BODY, RIGHT, LEG;

	@Override
	public void setupItemRender(PoseStack stack, @Nullable MetalGolemPartType type) {

	}
}
