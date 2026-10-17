package dev.xkmc.modulargolems.content.modifier.special;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * A ranged skill that fires a burst of shots in a row. The shots are spread over the nearby targets,
 * and when there is only one target in range every shot goes at that one.
 *
 * Subclasses only supply {@link #fire} and, if the burst size differs, {@link #getBurstCount}; the shot
 * count, the wait between them and who each shot locks on are handled here.
 */
public abstract class BurstRangedGoal extends BaseRangedAttackGoal {

	private static final int BURST_DELAY = 5;
	private static final float[] LATERALS = {0.0F, -0.5F, 0.5F};

	private final int farRange;
private int delay = -1;
	private int shot;
	private final List<Pair<LivingEntity, Float>> volley = new ArrayList<>();
	private final List<LivingEntity> targets = new ArrayList<>();

	protected BurstRangedGoal(int near, int far, AbstractGolemEntity<?, ?> golem, int lv) {
		super(200 >> (lv - 1), near, far, golem, lv);
		this.farRange = far;
	}

	/** how many shots one burst holds */
	protected int getBurstCount() {
		return 3;
	}

	/**
	 * Ticks between two shots of a burst. The first shot always leaves immediately. Zero fires the whole
	 * burst in one go, one releases everything left after the first, and anything higher lets the shots
	 * leave one by one at that interval.
	 */
	protected int getBurstDelay() {
		return BURST_DELAY;
	}

@Override
	protected boolean performAttack(LivingEntity target) {
		if (delay < 0) {
			volley.clear();
			buildVolley(target);
			return fireBurst();
		}
		delay--;
		if (delay > 0) {
			return false;
		}
		// the firing delay has run out, so from here on the burst itself drives the timing
		return continueBurst();
	}

	private boolean fireBurst() {
		int gap = getBurstDelay();
		if (gap <= 0) {
			for (var p : volley) {
				fire(GolemTargetUtil.resolve(golem, p.getFirst()), p.getSecond());
			}
			return true;
		}
		shot = 1;
		fire(volley.get(0).getFirst(), volley.get(0).getSecond());
		if (gap == 1) {
			// a one tick gap would send everything down together anyway, so keep the staggered path
			delay = BURST_DELAY;
			return false;
		}
		delay = gap;
		return false;
	}

	private boolean continueBurst() {
		if (getBurstDelay() <= 1) {
			// only the first shot was held back, the rest follow it at once
			for (int i = shot; i < volley.size(); i++) {
				var p = volley.get(i);
				fire(GolemTargetUtil.resolve(golem, p.getFirst()), p.getSecond());
			}
			return true;
		}
		// release a single shot per interval, cycling the target list so a lone target keeps being hit
		var p = volley.get(shot % volley.size());
		fire(GolemTargetUtil.resolve(golem, p.getFirst()), p.getSecond());
		shot++;
		if (shot >= volley.size()) {
			return true;
		}
		delay = getBurstDelay();
		return false;
	}

	private void buildVolley(LivingEntity target) {
		int count = getBurstCount();
		GolemTargetUtil.collect(golem, target, farRange, count, targets);
		for (int i = 0; i < count; i++) {
			int seen = 0;
			for (int j = 0; j < i; j++) {
				if (volley.get(j).getFirst() == targets.get(i % targets.size())) seen++;
			}
			volley.add(Pair.of(targets.get(i % targets.size()), LATERALS[seen % LATERALS.length]));
		}
	}

	protected abstract void fire(LivingEntity target, float lateral);

@Override
	public void stop() {
		delay = -1;
		shot = 0;
		volley.clear();
		super.stop();
	}

}
