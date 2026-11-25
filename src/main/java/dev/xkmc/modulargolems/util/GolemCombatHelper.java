package dev.xkmc.modulargolems.util;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Combat primitives shared by the damage skills, kept apart from any one skill family.
 */
public class GolemCombatHelper {

	/** How long a broken shield stays unusable. */
	public static final int SHIELD_BREAK_TICKS = 100;

	/** The golem's own attack attribute, the baseline every damage skill scales off. */
	public static float getAttackDamage(AbstractGolemEntity<?, ?> golem) {
		return (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
	}

	/** Deals damage and takes the target's shield down with it. Returns whether the hit actually landed. */
	public static boolean meleeHit(AbstractGolemEntity<?, ?> golem, LivingEntity target, DamageSource source, float damage) {
		boolean hit = target.hurt(source, damage);
		breakShield(golem, target, source);
		return hit;
	}

	/** Puts a blocking player's shield on cooldown. Never the golem's own rider. */
	public static void breakShield(AbstractGolemEntity<?, ?> golem, LivingEntity target, DamageSource source) {
		if (!(target instanceof Player player)) return;
		if (player == golem.getControllingPassenger()) return;
		if (!target.isDamageSourceBlocked(source)) return;
		ItemStack use = player.getUseItem();
		if (use.isEmpty()) return;
		player.getCooldowns().addCooldown(use.getItem(), SHIELD_BREAK_TICKS);
		player.stopUsingItem();
		golem.level().broadcastEntityEvent(player, (byte) 30);
	}

}
