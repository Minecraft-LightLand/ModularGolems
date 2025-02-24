package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.modulargolems.compat.musket.GolemMusketCompat;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.bow.BowBehaviorRegistry;
import dev.xkmc.modulargolems.content.entity.humanoid.crossbow.CrossbowBehaviorRegistry;
import dev.xkmc.modulargolems.content.entity.humanoid.ranged.GolemBowAttackGoal;
import dev.xkmc.modulargolems.content.entity.humanoid.ranged.GolemCrossbowAttackGoal;
import dev.xkmc.modulargolems.content.entity.humanoid.ranged.GolemShooterHelper;
import dev.xkmc.modulargolems.content.entity.humanoid.ranged.GolemTridentAttackGoal;
import dev.xkmc.modulargolems.init.ModularGolems;
import ewewukek.musketmod.MusketMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

public class WeaponGoalsRegistry {

	private static final LinkedHashMap<ResourceLocation, WeaponGoalEntry> KNOWLEDGE = new LinkedHashMap<>();

	public static void register(ResourceLocation id, boolean supportMelee, IWeaponGoalPredicate item, IWeaponGoalFactory<?> goal) {
		KNOWLEDGE.put(id, new WeaponGoalEntry(supportMelee, item, goal));
	}

	@Nullable
	public static Pair<ResourceLocation, WeaponGoalEntry> find(HumanoidGolemEntity golem, ItemStack weapon, InteractionHand hand) {
		for (var ent : KNOWLEDGE.entrySet())
			if (ent.getValue().item().isValid(golem, weapon, hand))
				return Pair.of(ent.getKey(), ent.getValue());
		return null;
	}

	public static void init() {
		BowBehaviorRegistry.init();
		CrossbowBehaviorRegistry.init();
		register(ModularGolems.loc("throwable"), true,
				GolemShooterHelper::isValidThrowableWeapon,
				(golem, melee) -> new GolemTridentAttackGoal(golem, 1, 20, 15, melee)
		);
		register(ModularGolems.loc("bow"), false,
				(golem, stack, hand) -> BowBehaviorRegistry.isValidBowItem(stack),
				(golem, melee) -> new GolemBowAttackGoal(golem, 1.0D, 20)
		);
		register(ModularGolems.loc("crossbow"), false,
				(golem, stack, hand) -> CrossbowBehaviorRegistry.isValidCrossbowItem(stack),
				(golem, melee) -> new GolemCrossbowAttackGoal(golem, 1.0D, 15)
		);
		if (ModList.get().isLoaded(MusketMod.MODID))
			GolemMusketCompat.init();
	}

}
