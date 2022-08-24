package dev.xkmc.modulargolems.content.core;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public interface IGolemPart<P extends IGolemPart<P>> {

	int ordinal();

	@OnlyIn(Dist.CLIENT)
	void setupItemRender(PoseStack stack, @Nullable P part);
}
