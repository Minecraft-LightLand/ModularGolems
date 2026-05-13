package dev.xkmc.modulargolems.content.item.upgrade;

import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.modulargolems.content.modifier.base.ModifierInstance;
import dev.xkmc.modulargolems.content.modifier.common.AddSlotModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.function.Consumer;

public class AddSlotTemplate extends Item implements IUpgradeItem {

	private final Val<AddSlotModifier> sup;

	public AddSlotTemplate(Properties p, Val<AddSlotModifier> sup) {
		super(p);
		this.sup = sup;
	}

	@Override
	public List<ModifierInstance> get() {
		return List.of(new ModifierInstance(sup.get(), 1));
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, TooltipDisplay disp, Consumer<Component> list, TooltipFlag flag) {
		for (var e : get()) {
			list.accept(e.mod().getTooltip(e.level()));
			e.mod().getDetail(e.level()).forEach(list);
		}
	}

}
