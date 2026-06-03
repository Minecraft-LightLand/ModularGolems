package dev.xkmc.modulargolems.events;

import dev.xkmc.l2damagetracker.contents.attack.AttackListener;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.IAttackListenerWeapon;

public class WeaponAttackListener implements AttackListener {

	@Override
	public boolean onAttack(DamageData.Attack cache) {
		var source = cache.getSource();
		if (cache.getAttacker() instanceof MetalGolemEntity e) {
			if (source.is(L2DamageTypes.DIRECT)) {
				var stack = e.getMainHandItem();
				if (stack.getItem() instanceof IAttackListenerWeapon item) {
					item.onAttack(cache, source, e, stack);
				}
			}
		}
		return false;
	}

	@Override
	public void onHurt(DamageData.Offence data) {
		var source = data.getSource();
		if (data.getAttacker() instanceof MetalGolemEntity e) {
			if (source.is(L2DamageTypes.DIRECT)) {
				var stack = e.getMainHandItem();
				if (stack.getItem() instanceof IAttackListenerWeapon item) {
					item.onHurt(data, source, e, stack);
				}
			}
		}
	}

	@Override
	public void onDamage(DamageData.Defence data) {
		var source = data.getSource();
		if (data.getAttacker() instanceof MetalGolemEntity e) {
			if (source.is(L2DamageTypes.DIRECT)) {
				var stack = e.getMainHandItem();
				if (stack.getItem() instanceof IAttackListenerWeapon item) {
					item.onDamage(data, source, e, stack);
				}
			}
		}
	}

}
