package dev.xkmc.modulargolems.events;

import dev.xkmc.l2damagetracker.contents.attack.AttackListener;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;

public class GolemAttackListener implements AttackListener {

	@Override
	public void onCreateSource(CreateSourceEvent event) {
		if (event.getAttacker() instanceof AbstractGolemEntity<?, ?> golem) {
			for (var e : golem.getModifiers().entrySet()) {
				e.getKey().modifySource(golem, event, e.getValue());
			}
		}
	}
}
