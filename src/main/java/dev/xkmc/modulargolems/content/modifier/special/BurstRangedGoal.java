package dev.xkmc.modulargolems.content.modifier.special;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 连续发射一串弹幕的远程技能。弹幕分摊到附近目标；只有一个目标时每发都打向它。
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

	protected int getBurstCount() {
		return 3;
	}

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
			delay = BURST_DELAY;
			return false;
		}
		delay = gap;
		return false;
	}

	private boolean continueBurst() {
		if (getBurstDelay() <= 1) {
			for (int i = shot; i < volley.size(); i++) {
				var p = volley.get(i);
				fire(GolemTargetUtil.resolve(golem, p.getFirst()), p.getSecond());
			}
			return true;
		}
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
