package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.ModifierInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IGolemModifierItem {

	List<ModifierInstance> getModifier(ItemStack stack, @Nullable AbstractGolemEntity<?, ?> golem);

	default void appendModifierText(ItemStack stack, List<Component> list) {
		var modifiers = getModifier(stack, null);
		for (var ins : modifiers) {
			list.add(ins.mod().getTooltip(ins.level()));
			list.addAll(ins.mod().getDetail(ins.level()));
		}
	}

}
