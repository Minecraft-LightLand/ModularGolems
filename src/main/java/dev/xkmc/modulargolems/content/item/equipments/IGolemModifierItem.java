package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.ModifierInstance;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IGolemModifierItem {

	List<ModifierInstance> getModifier(ItemStack stack, AbstractGolemEntity<?, ?> golem);

}
