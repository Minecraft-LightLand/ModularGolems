package dev.xkmc.modulargolems.compat.materials.eeeab.annihilator;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;

/**
 * Relic Annihilator - Missile (SHOT / TRICKSHOT) multi-target ranged attack
 * Original: {@code RARangeAttackGoal} -> {@code SHOT_ANIMATION2}/{@code TRICKSHOT_ANIMATION2} tick11
 * Decompiled: com/eeeab/eeeabsmobs/sever/entity/mob/relicron/EntityRelicAnnihilator.java:831-836
 *  + {@code performRangedAttack}:1049-1071
 * <pre>
 *  muzzle = getPosOffset(false, width*2.4, width*0.625, height*0.96) // SHOT
 *          or getPosOffset(false, width, width, height*0.42) // TRICKSHOT
 *  targetPos = target.pos.add(0, height*0.4, 0)
 *  projectileMid = muzzle.add(0,0.25,0)
 *  shootVec = (targetPos - projectileMid).normalize()
 *  element = electrified? BLAZE : VOLT; if health<0.5 && random<0.2 -> SPARKFERNO
 *  missile = new EntityAnnihilatorMissile(level,caster,element)
 *  missile.moveTo(muzzle) ; missile.shoot(shootVec,1.6F, blind?30:0)
 *  level.addFreshEntity(missile)
 * </pre>
 * Uses intermediate {@code EntityAnnihilatorMissile}
 * Reference: {@code EnderGuardianVoidRuneAttackGoal} / {@code AncientRemnantSandstormAttackGoal}
 *  -> {@code MultiTargetRangedGoal}
 */
public class AnnihilatorMissileModifier extends GolemModifier {

	public AnnihilatorMissileModifier() {
		super(StatFilterType.ATTACK, 3);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new AnnihilatorMissileAttackGoal(entity, lv));
	}

}
