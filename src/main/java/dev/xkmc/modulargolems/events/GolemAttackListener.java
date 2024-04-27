package dev.xkmc.modulargolems.events;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.AttackListener;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.item.ItemStack;

public class GolemAttackListener implements AttackListener {

	@Override
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
			cache.addHurtModifier(DamageModifier.nonlinearPre(213, f -> modifyDamange(golem, f)));
		}
	}

	private float modifyDamange(AbstractGolemEntity<?, ?> golem, float ans) {
		for (var entry : golem.getModifiers().entrySet()) {
			ans = entry.getKey().modifyDamage(ans, golem, entry.getValue());
		}
		return ans;
	}

}
