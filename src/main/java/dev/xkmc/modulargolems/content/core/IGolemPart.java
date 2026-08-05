package dev.xkmc.modulargolems.content.core;

import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import net.minecraft.network.chat.MutableComponent;

public interface IGolemPart<P extends IGolemPart<P>> {

	int ordinal();

	MutableComponent getDesc(MutableComponent desc);

	GolemPart<?, P> toItem();

	GolemSlot getSlot();

}
