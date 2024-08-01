package dev.xkmc.modulargolems.content.core;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

public interface IGolemPart<P extends IGolemPart<P>> {

	int ordinal();

	void setupItemRender(PoseStack stack, ItemDisplayContext type, @Nullable P part);

	MutableComponent getDesc(MutableComponent desc);

	GolemPart<?, P> toItem();

}
