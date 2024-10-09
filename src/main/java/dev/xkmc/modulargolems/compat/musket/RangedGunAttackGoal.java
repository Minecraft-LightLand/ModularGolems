package dev.xkmc.modulargolems.compat.musket;

import ewewukek.musketmod.GunItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class RangedGunAttackGoal<T extends Mob> extends Goal {
	public final T mob;
	private boolean isLoading;

	public RangedGunAttackGoal(T mob) {
		this(mob, EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	public RangedGunAttackGoal(T mob, EnumSet<Goal.Flag> flags) {
		this.mob = mob;
		this.setFlags(flags);
	}

	public boolean canUse() {
		return this.isTargetValid() && this.canUseGun();
	}

	public boolean isTargetValid() {
		return this.mob.getTarget() != null && this.mob.getTarget().isAlive();
	}

	public boolean canUseGun() {
		return GunItem.isHoldingGun(this.mob) && GunItem.canUse(this.mob);
	}

	public boolean isReady() {
		InteractionHand hand = GunItem.getHoldingHand(this.mob);
		if (hand == null) {
			return false;
		} else {
			ItemStack stack = this.mob.getItemInHand(hand);
			return GunItem.isReady(stack);
		}
	}

	public boolean isLoading() {
		return this.isLoading;
	}

	public void onReady() {
	}

	public void reload() {
		InteractionHand hand = GunItem.getHoldingHand(this.mob);
		if (hand != null) {
			ItemStack stack = this.mob.getItemInHand(hand);
			if (!this.isLoading && !GunItem.isLoaded(stack)) {
				GunItem.setLoadingStage(stack, 1);
				this.mob.startUsingItem(hand);
				this.isLoading = true;
			}

		}
	}

	public void fire(float spread) {
		InteractionHand hand = GunItem.getHoldingHand(this.mob);
		if (hand != null) {
			ItemStack stack = this.mob.getItemInHand(hand);
			GunItem gun = (GunItem) stack.getItem();
			Vec3 direction = gun.aimAt(this.mob, this.mob.getTarget());
			if (spread > 0.0F) {
				direction = GunItem.addUniformSpread(direction, this.mob.getRandom(), spread);
			}

			gun.mobUse(this.mob, hand, direction);
		}
	}

	public boolean requiresUpdateEveryTick() {
		return true;
	}

	public void tick() {
		if (this.isLoading) {
			if (this.mob.isUsingItem()) {
				if (GunItem.isLoaded(this.mob.getUseItem())) {
					this.mob.releaseUsingItem();
					this.isLoading = false;
					this.onReady();
				}
			} else {
				this.isLoading = false;
			}
		}

	}

	public void stop() {
		super.stop();
		this.mob.setAggressive(false);
		this.mob.setTarget(null);
		this.isLoading = false;
		if (this.mob.isUsingItem()) {
			this.mob.stopUsingItem();
		}

	}
}