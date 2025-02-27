package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.mob_weapon_api.registry.WeaponStatus;
import dev.xkmc.modulargolems.content.entity.goals.GolemMeleeGoal;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.ItemWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

public class WeaponGoalsManager {

	private final HumanoidGolemEntity golem;
	private final GolemMeleeGoal meleeGoal;
	private final LinkedHashMap<ResourceLocation, WeaponGoalHolder<?>> goals = new LinkedHashMap<>();

	@Nullable
	private WeaponGoalHolder<?> currentGoal = null;
	private boolean meleeActive = false;

	public WeaponGoalsManager(HumanoidGolemEntity golem) {
		this.golem = golem;
		meleeGoal = new GolemMeleeGoal(golem);
	}

	private @Nullable WeaponHolder<?> getGoalForWeapon(ItemStack stack, @Nullable InteractionHand hand) {
		var ent = WeaponGoalsRegistry.find(golem, stack, hand);
		if (ent == null) return null;
		if (goals.containsKey(ent.id())) {
			return new WeaponHolder<>(golem, stack, goals.get(ent.id()), ent.status());
		} else {
			var ans = new WeaponGoalHolder<>(ent.id(), ent.entry().goal().create(golem, meleeGoal));
			goals.put(ent.id(), ans);
			return new WeaponHolder<>(golem, stack, ans, ent.status());
		}
	}

	public void reassessWeaponGoal() {
		if (golem.level().isClientSide) return;
		InteractionHand hand = golem.getWeaponHand();
		ItemStack weapon = golem.getItemInHand(hand);
		if (!weapon.isEmpty()) {
			var ans = getGoalForWeapon(weapon, hand);
			if (ans != null) {
				if (currentGoal != null) {
					golem.goalSelector.removeGoal(currentGoal.goal());
				}
				currentGoal = ans.holder();
				golem.goalSelector.addGoal(2, currentGoal.goal());

				if (!ans.isMelee()) {
					if (meleeActive) {
						golem.goalSelector.removeGoal(this.meleeGoal);
						meleeActive = false;
					}
					return;
				}
			}
		}
		if (!meleeActive) {
			golem.goalSelector.addGoal(3, this.meleeGoal);
			meleeActive = true;
		}
	}

	public void performRangedAttack(LivingEntity target, float power) {
		if (currentGoal != null && currentGoal.goal() instanceof IRangedWeaponGoal goal) {
			InteractionHand hand = golem.getWeaponHand();
			ItemStack stack = golem.getItemInHand(hand);
			goal.performRangedAttack(golem, target, power, stack, hand);
		}
	}

	public boolean checkSwitch(@Nullable LivingEntity target, ItemWrapper mainhand, ItemWrapper offhand) {
		ItemStack main = mainhand.getItem();
		ItemStack off = offhand.getItem();
		var mainGoal = getGoalForWeapon(main, null);
		var offGoal = getGoalForWeapon(off, null);
		if (mainGoal != null && mainGoal.isRanged()) {
			if (target == null || off.isEmpty() || off.getItem() instanceof ArrowItem) {
				return false;
			}
			if (!mainGoal.mayActivate()) {
				return true;
			}
			if (offGoal != null) {
				if (offGoal.priority() > mainGoal.priority() && offGoal.mayActivate()) {
					if (offGoal.isWithinRange(target, 0))
						return true;
				}
				if (!offGoal.isMelee() && offGoal.isRanged()) {
					return false;
				}
			}
			return meleeGoal.canReachTarget(target);
		}
		if (offGoal != null && offGoal.isRanged()) {
			if (!offGoal.mayActivate()) return false;
			if (target == null) return true;
			if (mainGoal != null) {
				if (!mainGoal.isWithinRange(target, 4)) {
					return true;
				}
				if (offGoal.priority() < mainGoal.priority() && mainGoal.mayActivate()) {
					return false;
				}
			}
			return !meleeGoal.canReachTarget(target);
		}
		return main.isEmpty() && !off.isEmpty();
	}

	private record WeaponGoalHolder<T extends Goal & IWeaponGoal>(ResourceLocation id, T goal) {
	}

	private record WeaponHolder<T extends Goal & IWeaponGoal>(
			HumanoidGolemEntity golem, ItemStack stack, WeaponGoalHolder<T> holder, WeaponStatus status
	) {

		public T goal() {
			return holder().goal();
		}

		public int priority() {
			return status().priority();
		}

		public boolean isMelee() {
			return status().isMelee();
		}

		public boolean isRanged() {
			return status().isRanged();
		}

		public boolean mayActivate() {
			return goal().mayActivate(golem, stack);
		}

		public boolean isWithinRange(LivingEntity target, double buffer) {
			return golem.distanceTo(target) < goal().range(golem, stack) + buffer;
		}

	}

}
