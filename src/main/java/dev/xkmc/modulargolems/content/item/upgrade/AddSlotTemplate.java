package dev.xkmc.modulargolems.content.item.upgrade;

import dev.xkmc.modulargolems.content.modifier.base.ModifierInstance;
import dev.xkmc.modulargolems.content.modifier.common.AddSlotModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class AddSlotTemplate extends Item implements IUpgradeItem {

	private final Supplier<AddSlotModifier> sup;

	public AddSlotTemplate(Properties p, Supplier<AddSlotModifier> sup) {
		super(p);
		this.sup = sup;
	}

	@Override
	public List<ModifierInstance> get() {
		return List.of(new ModifierInstance(sup.get(), 1));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		for (var e : get()) {
			list.add(e.mod().getTooltip(e.level()));
			list.addAll(e.mod().getDetail(e.level()));
		}
	}

}
