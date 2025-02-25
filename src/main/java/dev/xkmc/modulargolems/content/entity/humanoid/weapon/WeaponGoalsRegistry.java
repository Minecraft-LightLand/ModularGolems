package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.mob_weapon_api.registry.IWeaponStatusPredicate;
import dev.xkmc.mob_weapon_api.registry.WeaponRegistry;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;
import dev.xkmc.modulargolems.content.entity.humanoid.ranged.*;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

public class WeaponGoalsRegistry {

	private static final LinkedHashMap<ResourceLocation, WeaponGoalEntry> KNOWLEDGE = new LinkedHashMap<>();

	public static void register(ResourceLocation id, IWeaponStatusPredicate item, IWeaponGoalFactory<?> goal) {
		KNOWLEDGE.put(id, new WeaponGoalEntry(item, goal));
	}

	@Nullable
	public static WeaponSearchResult find(LivingEntity user, ItemStack weapon, @Nullable InteractionHand hand) {
		for (var ent : KNOWLEDGE.entrySet()) {
			var status = ent.getValue().item().getProperties(user, weapon, hand);
			if (status.isPresent())
				return new WeaponSearchResult(ent.getKey(), status.get(), ent.getValue());
		}
		return null;
	}

	public static void init() {
		register(ModularGolems.loc("throwable"),
				(golem, stack, hand) -> WeaponStatus.OFFENSIVE.of(GolemShooterHelper.isValidThrowableWeapon(golem, stack, hand)),
				(golem, melee) -> new GolemTridentAttackGoal(golem, 1, 20, 25, melee)
		);
		register(ModularGolems.loc("bow"),
				(golem, stack, hand) -> WeaponRegistry.BOW.getProperties(stack),
				(golem, melee) -> new GolemBowAttackGoal(golem, melee, 1.0D, 25)
		);
		register(ModularGolems.loc("crossbow"),
				(golem, stack, hand) -> WeaponRegistry.CROSSBOW.getProperties(stack),
				(golem, melee) -> new GolemCrossbowAttackGoal(golem, melee, 1.0D, 25)
		);
		register(ModularGolems.loc("instant"),
				(golem, stack, hand) -> WeaponRegistry.INSTANT.getProperties(stack),
				(golem, melee) -> new GolemSimpleRangedAttackGoal(golem, melee, 1.0D)
		);
		register(ModularGolems.loc("hold"),
				(golem, stack, hand) -> WeaponRegistry.HOLD.getProperties(stack),
				(golem, melee) -> new GolemHoldRangedAttackGoal(golem, melee, 1.0D)
		);
	}

	public record WeaponSearchResult(ResourceLocation id, WeaponStatus status, WeaponGoalEntry entry) {
	}

	public record WeaponGoalEntry(
			IWeaponStatusPredicate item,
			IWeaponGoalFactory<?> goal

	) {
	}
}
