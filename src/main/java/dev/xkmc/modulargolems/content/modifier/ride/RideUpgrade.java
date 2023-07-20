package dev.xkmc.modulargolems.content.modifier.ride;

import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.AttributeGolemModifier;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.function.Consumer;

public class RideUpgrade extends AttributeGolemModifier {

	public RideUpgrade(int max, AttrEntry... entries) {
		super(max, entries);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.PASSIVE);
	}

	@Override
	public boolean fitsOn(GolemType<?, ?> type) {
		return type == GolemTypes.TYPE_DOG.get();
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		var ans = super.getDetail(v);
		ans.add(0, Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.GREEN));
		return ans;
	}
}
