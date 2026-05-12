package dev.xkmc.modulargolems.events;

import dev.xkmc.l2damagetracker.contents.attack.*;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.DamageTypeTags;

public class GolemAttackListener implements AttackListener {

	private static final Identifier WEAPON_INHERENT = ModularGolems.loc("weapon_inherent");

	@Override
	public void onCreateSource(OnDamageSourceModifyEvent event) {
		if (event.getAttacker() instanceof AbstractGolemEntity<?, ?> golem) {
			for (var e : golem.getModifiersExtended().entrySet()) {
				e.getKey().modifySource(golem, event, e.getValue());
			}
		}
	}

	@Override
	public boolean onAttack(DamageData.Attack data) {
		if (data.getAttacker() instanceof AbstractGolemEntity<?, ?> golem) {
			if (!golem.canAttack(data.getTarget())) {
				return true;
			}
			for (var e : golem.getModifiersExtended().entrySet()) {
				e.getKey().onAttackTarget(golem, data, e.getValue());
			}
		}
		if (data.getTarget() instanceof AbstractGolemEntity<?, ?> golem) {
			if (data.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
			for (var e : golem.getModifiersExtended().entrySet()) {
				if (e.getKey().onAttacked(golem, data, e.getValue())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onHurt(DamageData.Offence data) {
		if (data.getAttacker() instanceof AbstractGolemEntity<?, ?> golem) {
			data.addHurtModifier(DamageModifier.nonlinearPre(-1000,
					dmg -> dmg + golem.getMainHandItem().getItem().getAttackDamageBonus(data.getTarget(), dmg, data.getSource()),
					WEAPON_INHERENT
			));
			for (var entry : golem.getModifiersExtended().entrySet()) {
				entry.getKey().onHurtTarget(golem, data, entry.getValue());
			}
		}
	}

	@Override
	public void onHurtMaximized(DamageData.OffenceMax data) {

	}

	@Override
	public void onDamage(DamageData.Defence data) {
		if (data.getTarget() instanceof AbstractGolemEntity<?, ?> golem) {
			if (data.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
			for (var e : golem.getModifiersExtended().entrySet()) {
				e.getKey().onDamaged(golem, data, e.getValue());
			}
		}
	}

	@Override
	public void onDamageFinalized(DamageData.DefenceMax data) {
		if (data.getAttacker() instanceof AbstractGolemEntity<?, ?> golem) {
			for (var entry : golem.getModifiersExtended().entrySet()) {
				entry.getKey().postHurtTarget(golem, data, entry.getValue());
			}
			var owner = golem.getOwner();
			if (owner != null) {
				data.getTarget().setLastHurtByPlayer(owner);
			}
		}
		if (data.getTarget() instanceof AbstractGolemEntity<?, ?> golem) {
			for (var entry : golem.getModifiersExtended().entrySet()) {
				entry.getKey().postDamaged(golem, data, entry.getValue());
			}
		}
	}

}
