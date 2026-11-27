package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;

public interface IAttackListenerWeapon {

	default void onHurt(AttackCache cache, DamageSource source, MetalGolemEntity e, ItemStack stack) {

	}

	default void onAttack(AttackCache cache, DamageSource source, MetalGolemEntity e, ItemStack stack) {

	}

	default void onDamage(AttackCache cache, DamageSource source, MetalGolemEntity e, ItemStack stack) {

	}

	default void onTick(MetalGolemEntity e, ItemStack stack, InteractionHand hand) {

	}

	/** 攻击源创建阶段（可用于护甲穿透等 DefaultDamageState）。 */
	default void onCreateSource(AbstractGolemEntity<?, ?> golem, CreateSourceEvent event, ItemStack stack) {

	}

}
