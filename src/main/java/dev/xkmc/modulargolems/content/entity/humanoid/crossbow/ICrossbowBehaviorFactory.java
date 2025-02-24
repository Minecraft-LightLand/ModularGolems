package dev.xkmc.modulargolems.content.entity.humanoid.crossbow;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.item.ItemStack;

public interface ICrossbowBehaviorFactory {

	CrossbowBehaviorData create(HumanoidGolemEntity golem, ItemStack stack);

}
