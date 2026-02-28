package dev.xkmc.modulargolems.events;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.AttackListener;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.item.ItemStack;

public class GolemAttackListener implements AttackListener {

	@Override
	// 当攻击源创建时触发。如果攻击者是 AbstractGolemEntity 的实例，则遍历该傀儡的修饰符，并调用每个修饰符的 modifySource 方法来修改攻击源。
	public void onCreateSource(CreateSourceEvent event) {
		if (event.getAttacker() instanceof AbstractGolemEntity<?, ?> golem) {
			for (var e : golem.getModifiers().entrySet()) {
				e.getKey().modifySource(golem, event, e.getValue());
			}
		}
	}

	@Override
	public void onHurt(AttackCache cache, ItemStack weapon) {
		if (cache.getAttacker() instanceof AbstractGolemEntity<?, ?> golem) {
			for (var entry : golem.getModifiersExtended().entrySet()) {
				entry.getKey().modifyDamage(cache, golem, entry.getValue());
			}
		}
	}

	@Override
	public void onDamage(AttackCache cache, ItemStack weapon) {
		if (cache.getAttackTarget() instanceof AbstractGolemEntity<?, ?> golem) {
			for (var entry : golem.getModifiersExtended().entrySet()) {
				entry.getKey().onDamaged(cache, golem, entry.getValue());
			}
		}
	}

	@Override
	public void onDamageFinalized(AttackCache cache, ItemStack weapon) {
		if (cache.getAttacker() instanceof AbstractGolemEntity<?, ?> golem) {
			var owner = golem.getOwner();
			// 如果傀儡有主人，则将伤害者设为主人
			if (owner != null) {
				cache.getAttackTarget().setLastHurtByPlayer(owner);
			}
			for (var entry : golem.getModifiers().entrySet()) {
				entry.getKey().finalizeHurtTarget(cache, golem, entry.getValue());
			}
		}
		if (cache.getAttackTarget() instanceof AbstractGolemEntity<?, ?> golem) {
			for (var entry : golem.getModifiersExtended().entrySet()) {
				entry.getKey().onDamageMax(cache, golem, entry.getValue());
			}
		}
	}

}
