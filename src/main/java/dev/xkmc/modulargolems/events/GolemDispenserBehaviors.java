package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public class GolemDispenserBehaviors {

	public static void registerDispenseBehaviors() {
		DispenseItemBehavior behavior = (source, stack) -> {
			if (((GolemHolder<?, ?>) stack.getItem()).summon(
					stack, source.getLevel(),
					Vec3.atBottomCenterOf(source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING))),
					null, null)) {
				stack.shrink(1);
			}
			return stack;
		};
		DispenserBlock.registerBehavior(GolemItems.HOLDER_GOLEM.get(), behavior);
		DispenserBlock.registerBehavior(GolemItems.HOLDER_HUMANOID.get(), behavior);
		DispenserBlock.registerBehavior(GolemItems.HOLDER_DOG.get(), behavior);
	}

}
