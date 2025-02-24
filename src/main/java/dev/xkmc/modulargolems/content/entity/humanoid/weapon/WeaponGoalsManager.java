package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.modulargolems.content.entity.goals.GolemMeleeGoal;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.ItemWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraftforge.common.ToolActions;
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
		if (goals.containsKey(ent.getFirst())) {
			return goals.get(ent.getFirst());
		} else {
			var ans = new WeaponGoalHolder<>(ent.getFirst(), ent.getSecond().goal().create(golem, meleeGoal), ent.getSecond().supportMelee());
			goals.put(ent.getFirst(), ans);
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

				if (!ans.supportMelee()) {
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

	public void performRangedAttack(LivingEntity target, float dist) {
		if (currentGoal != null && currentGoal.goal() instanceof IRangedWeaponGoal goal) {
			InteractionHand hand = golem.getWeaponHand();
			ItemStack stack = golem.getItemInHand(hand);
			goal.performRangedAttack(golem, target, dist, stack, hand);
		}
	}

	public boolean checkSwitch(@Nullable LivingEntity target, ItemWrapper mainhand, ItemWrapper offhand) {
		ItemStack main = mainhand.getItem();
		ItemStack off = offhand.getItem();
		if (main.getItem() instanceof ProjectileWeaponItem) {
			if (target == null || off.isEmpty() ||
					off.getItem() instanceof ProjectileWeaponItem ||
					off.getItem() instanceof ArrowItem &&
							!off.canPerformAction(ToolActions.SHIELD_BLOCK)) {
				return false;
			}
			var holder = getGoalForWeapon(main, null);
			return holder == null || !holder.goal().mayActivate(golem, main) || meleeGoal.canReachTarget(target);
		}
		if (off.getItem() instanceof ProjectileWeaponItem) {
			var holder = getGoalForWeapon(off, null);
			if (holder == null || !holder.goal().mayActivate(golem, off)) return false;
			return target == null || !meleeGoal.canReachTarget(target);
		}
		return main.isEmpty() && !off.isEmpty();
	}

}
