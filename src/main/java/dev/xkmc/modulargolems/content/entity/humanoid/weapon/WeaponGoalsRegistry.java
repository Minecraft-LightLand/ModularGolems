package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.ranged.GolemBowAttackGoal;
import dev.xkmc.modulargolems.content.entity.humanoid.ranged.GolemCrossbowAttackGoal;
import dev.xkmc.modulargolems.content.entity.humanoid.ranged.GolemShooterHelper;
import dev.xkmc.modulargolems.content.entity.humanoid.ranged.GolemTridentAttackGoal;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.projectile_api.api.IBowBehavior;
import dev.xkmc.projectile_api.api.ICrossbowBehavior;
import dev.xkmc.projectile_api.example.SimpleBowBehavior;
import dev.xkmc.projectile_api.example.SimpleCrossbowBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

public class WeaponGoalsRegistry {

	public static final RangedBehaviorRegistry<IBowBehavior> BOW = new RangedBehaviorRegistry<>(
			ModularGolems.loc("bow"), e -> WeaponStatus.RANGED.of(e.getItem() instanceof BowItem),
			(golem, stack) -> new SimpleBowBehavior()
	);

	public static final RangedBehaviorRegistry<ICrossbowBehavior> CROSSBOW = new RangedBehaviorRegistry<>(
			ModularGolems.loc("crossbow"), e -> WeaponStatus.RANGED.of(e.getItem() instanceof CrossbowItem),
			(golem, stack) -> new SimpleCrossbowBehavior()
	);

	private static final LinkedHashMap<ResourceLocation, WeaponGoalEntry> KNOWLEDGE = new LinkedHashMap<>();

	public static void register(ResourceLocation id, IWeaponStatusPredicate item, IWeaponGoalFactory<?> goal) {
		KNOWLEDGE.put(id, new WeaponGoalEntry(item, goal));
	}

	@Nullable
	public static WeaponSearchResult find(HumanoidGolemEntity golem, ItemStack weapon, @Nullable InteractionHand hand) {
		for (var ent : KNOWLEDGE.entrySet()) {
			var status = ent.getValue().item().getProperties(golem, weapon, hand);
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
				(golem, stack, hand) -> BOW.getProperties(stack),
				(golem, melee) -> new GolemBowAttackGoal(golem, melee, 1.0D, 25)
		);
		register(ModularGolems.loc("crossbow"),
				(golem, stack, hand) -> CROSSBOW.getProperties(stack),
				(golem, melee) -> new GolemCrossbowAttackGoal(golem, melee, 1.0D, 25)
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
