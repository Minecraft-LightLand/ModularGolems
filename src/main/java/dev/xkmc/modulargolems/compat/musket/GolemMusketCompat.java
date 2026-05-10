package dev.xkmc.modulargolems.compat.musket;

import dev.xkmc.mob_weapon_api.registry.WeaponStatus;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.GolemWeaponRegistry;
import ewewukek.musketmod.GunItem;
import ewewukek.musketmod.MusketMod;
import net.minecraft.resources.Identifier;

public class GolemMusketCompat {

	public static void init() {
		GolemWeaponRegistry.HUMANOID.register(
				Identifier.fromNamespaceAndPath(MusketMod.MODID, "musket"),
				(golem, stack, hand) -> WeaponStatus.RANGED.of(stack.getItem() instanceof GunItem item && item.canUseFrom(golem, hand)),
				(golem, melee) -> new GolemMusketGoal(golem)
		);
	}

}
