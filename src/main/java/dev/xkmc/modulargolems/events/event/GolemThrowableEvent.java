package dev.xkmc.modulargolems.events.event;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public class GolemThrowableEvent extends GolemItemUseEvent {

	private boolean isThrowable;
	private Function<Level, Projectile> supplier;

	public GolemThrowableEvent(HumanoidGolemEntity golem, ItemStack stack, InteractionHand hand) {
		super(golem, stack, hand);
	}

	public void setThrowable(Function<Level, Projectile> supplier) {
		this.supplier = supplier;
		this.isThrowable = true;
	}

	public boolean isThrowable() {
		return isThrowable;
	}

	public Projectile createProjectile(Level level) {
		return supplier.apply(level);
	}

}
