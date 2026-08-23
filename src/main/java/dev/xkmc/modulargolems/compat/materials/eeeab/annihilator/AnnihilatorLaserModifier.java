package dev.xkmc.modulargolems.compat.materials.eeeab.annihilator;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * Relic Annihilator - Laser (single-target)
 * Original: {@code RARangeAttackGoal} -> {@code LASER_ANIMATION} (90)
 * Decompiled: com/eeeab/eeeabsmobs/sever/entity/mob/relicron/EntityRelicAnnihilator.java:1374-1388
 * <pre>
 *  tick9:  x=scope.getX, y=scope.getEyeY(0.24), z=scope.getZ
 *          ray = new EntityInfraredRay(level,caster,x,y,z,29) -> add
 *  tick49: laser = new EntityGuardianLaser(level,caster, golem.getX(), golem.getY(), golem.getZ(),20)
 *          laser.setCountDown(1); type=RELIC_ANNIHILATOR; updateWithEntity(caster,wOffset,hOffset) -> add
 * </pre>
 * Uses intermediates {@code EntityInfraredRay} + {@code EntityGuardianLaser}
 * Single-target, reference {@code GuardianLaserAttackGoal} / {@code HarbingerDeathBeamAttackGoal}
 */
public class AnnihilatorLaserModifier extends GolemModifier {

	public AnnihilatorLaserModifier() {
		super(StatFilterType.ATTACK, 3);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new AnnihilatorLaserAttackGoal(entity, lv));
	}

}
