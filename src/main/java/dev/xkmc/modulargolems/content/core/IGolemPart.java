package dev.xkmc.modulargolems.content.core;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.item.GolemPart;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public interface IGolemPart<P extends IGolemPart<P>> {

	int ordinal();

	@OnlyIn(Dist.CLIENT)
	void setupItemRender(PoseStack stack, ItemTransforms.TransformType type, @Nullable P part);

	MutableComponent getDesc(MutableComponent desc);

	GolemPart<?, P> toItem();

}
