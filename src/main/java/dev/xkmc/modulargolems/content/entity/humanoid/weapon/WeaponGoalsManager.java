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

	public WeaponGoalsManager(HumanoidGolemEntity golem) {
		this.golem = golem;
		meleeGoal = new GolemMeleeGoal(golem);
	}

	public void reassessWeaponGoal() {
		if (golem.level().isClientSide) return;
		golem.goalSelector.removeGoal(this.meleeGoal);
		if (currentGoal != null) {
			golem.goalSelector.removeGoal(currentGoal.goal());
			currentGoal = null;
		}
		InteractionHand hand = golem.getWeaponHand();
		ItemStack weapon = golem.getItemInHand(hand);
		if (!weapon.isEmpty()) {
			var ent = WeaponGoalsRegistry.find(golem, weapon, hand);
			if (ent != null) {
				currentGoal = new WeaponGoalHolder<>(ent.getFirst(), ent.getSecond().goal().create(golem, meleeGoal));
				golem.goalSelector.addGoal(2, currentGoal.goal());
				if (ent.getSecond().supportMelee()) {
					golem.goalSelector.addGoal(3, this.meleeGoal);
				}
				return;
			}
		}
		golem.goalSelector.addGoal(3, this.meleeGoal);
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
			return golem.getProjectile(main).isEmpty() || meleeGoal.canReachTarget(target);
		}
		if (off.getItem() instanceof ProjectileWeaponItem) {
			boolean noArrow = golem.getProjectile(off).isEmpty();
			if (noArrow) {
				return false;
			}
			return target == null || !meleeGoal.canReachTarget(target);
		}
		return main.isEmpty() && !off.isEmpty();
	}

}
