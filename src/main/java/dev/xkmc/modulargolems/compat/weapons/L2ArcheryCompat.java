package dev.xkmc.modulargolems.compat.weapons;

import dev.xkmc.l2archery.content.item.GenericBowItem;
import dev.xkmc.l2archery.init.L2Archery;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.WeaponGoalsRegistry;
import dev.xkmc.modulargolems.content.entity.humanoid.weapon.WeaponStatus;
import dev.xkmc.projectile_api.integration.l2archery.L2BowBehavior;
import net.minecraft.resources.ResourceLocation;

public class L2ArcheryCompat {

	public static void init() {
		WeaponGoalsRegistry.BOW.register(new ResourceLocation(L2Archery.MODID, "bow"),
				e -> WeaponStatus.RANGED.of(e.getItem() instanceof GenericBowItem),
				(golem, stack) -> new L2BowBehavior()
		);
	}

}
