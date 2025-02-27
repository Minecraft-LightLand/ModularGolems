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

	private @Nullable WeaponGoalHolder<?> getGoalForWeapon(ItemStack stack, @Nullable InteractionHand hand) {
		var ent = WeaponGoalsRegistry.find(golem, stack, hand);
		if (ent == null) return null;
		if (goals.containsKey(ent.id())) {
			return goals.get(ent.id());
		} else {
			var ans = new WeaponGoalHolder<>(ent.id(), ent.entry().goal().create(golem, meleeGoal), ent.status());
			goals.put(ent.id(), ans);
			return ans;
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
				currentGoal = ans;
				golem.goalSelector.addGoal(2, currentGoal.goal());

				if (!ans.status.isMelee()) {
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
		if (mainGoal != null && mainGoal.status.isRanged()) {
			if (target == null || off.isEmpty() || off.getItem() instanceof ArrowItem) {
				return false;
			}
			if (!mainGoal.goal().mayActivate(golem, main)) {
				return true;
			}
			if (offGoal != null) {
				if (offGoal.status.priority() > mainGoal.status.priority() && offGoal.goal().mayActivate(golem, off)) {
					return true;
				}
				if (!offGoal.status.isMelee() && offGoal.status.isRanged()) {
					return false;
				}
			}
			return meleeGoal.canReachTarget(target);
		}
		if (offGoal != null && offGoal.status.isRanged()) {
			if (!offGoal.goal().mayActivate(golem, off)) return false;
			if (target == null) return true;
			if (mainGoal != null) {
				if (offGoal.status.priority() < mainGoal.status.priority() && mainGoal.goal().mayActivate(golem, main)) {
					return false;
				}
			}
			return !meleeGoal.canReachTarget(target);
		}
		return main.isEmpty() && !off.isEmpty();
	}

	private record WeaponGoalHolder<T extends Goal & IWeaponGoal>(ResourceLocation id, T goal, WeaponStatus status) {
	}
}
