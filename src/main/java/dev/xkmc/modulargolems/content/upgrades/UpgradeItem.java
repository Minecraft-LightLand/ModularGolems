package dev.xkmc.modulargolems.content.upgrades;

import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UpgradeItem extends Item {

	public static final List<UpgradeItem> LIST = new ArrayList<>();

	private final Supplier<GolemModifier> modifier;

	public UpgradeItem(Properties props, Supplier<GolemModifier> modifier) {
		super(props);
		this.modifier = modifier;
		LIST.add(this);
	}

	public GolemModifier get() {
		return modifier.get();
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		list.add(modifier.get().getTooltip(1));
		list.addAll(modifier.get().getDetail(1));
	}
}
