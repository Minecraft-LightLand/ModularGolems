package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.item.ItemStack;

public interface RangedBehaviorFactory<T> {

	T create(HumanoidGolemEntity golem, ItemStack stack);

}
