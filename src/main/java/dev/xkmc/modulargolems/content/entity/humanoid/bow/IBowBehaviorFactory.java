package dev.xkmc.modulargolems.content.entity.humanoid.bow;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.item.ItemStack;

public interface IBowBehaviorFactory {

	BowBehaviorData create(HumanoidGolemEntity golem, ItemStack stack);

}
