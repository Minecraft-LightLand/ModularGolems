package dev.xkmc.modulargolems.content.core;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public interface IGolemPart<P extends IGolemPart<P>> {

	int ordinal();

	@OnlyIn(Dist.CLIENT)
	void setupItemRender(PoseStack stack, ItemDisplayContext type, @Nullable P part);

	MutableComponent getDesc(MutableComponent desc);

	GolemPart<?, P> toItem();

}
