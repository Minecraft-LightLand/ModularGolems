package dev.xkmc.modulargolems.compat.materials.alexscaves.modifier;

import dev.xkmc.modulargolems.compat.materials.alexscaves.ACCompatRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import dev.xkmc.modulargolems.compat.materials.alexscaves.client.AtomicBeamSound;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.entity.impl.GolemBeamSource;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Atomic Breath - continuous beam fired from the mouth, modeled after Alex's Caves Tremorzilla.
 * Original: {@code TremorzillaEntity.tickBreath()} (every 3 ticks, steps 7.5 blocks along the beam,
 * hits everything in radius 6 for 20 damage), plus {@code hurtEntitiesAround} which applies
 * irradiated and knockback only to entities it can actually see.
 * <p>
 * Blocks are never destroyed, unlike the original.
 */
public class AtomicBreathModifier extends GolemModifier {

	public static final float STEP = 7.5F;
	public static final float WALK_STEP = 5F;
	public static final int DAMAGE_INTERVAL = 3;
	public static final float END_RADIUS = 6F;
	public static final int IRRADIATED_DURATION = 6000;
	public static final int IRRADIATED_AMPLIFIER = 2;

	/** 冷却（tick）；核能强化期间减半。 */
	public static final int COOLDOWN = 400;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;
	/** 射线长度（格），同时也是施放的最大距离。 */
	public static final int MAX_RANGE = 100;
	/** 每次命中的基础伤害。 */
	public static final double DAMAGE = 20D;
	/** 每次命中的基础半径。 */
	public static final double RADIUS = 6D;
	/** 每次命中的基础击退。 */
	public static final double FORCE = 1D;
	/** 单次吐息持续 tick。 */
	public static final int DURATION = 200;

	public AtomicBreathModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	public static double damage(int lv) {
		return DAMAGE;
	}

	/**
	 * 实际冷却：核能强化（{@code atomic_boost}）期间减半。
	 * 与机械动力冲锋在燃料充足时冷却减半是同一套做法。
	 */
	public static int cooldownFor(AbstractGolemEntity<?, ?> golem) {
		if (golem.hasEffect(ACCompatRegistry.EFF_ATOMIC.get())) {
			return Math.max(1, COOLDOWN / 2);
		}
		return COOLDOWN;
	}

	public static double radius() {
		return RADIUS;
	}

	public static double range() {
		return MAX_RANGE;
	}

	public static double force() {
		return FORCE;
	}

	public static Vec3 shootFrom(AbstractGolemEntity<?, ?> golem) {
		return golem.getBeamOrigin();
	}

	public static Vec3 beamEnd(AbstractGolemEntity<?, ?> golem) {
		Vec3 from = shootFrom(golem);
		Vec3 to = from.add(GolemBeamSource.lookVector(golem, 0.0F).normalize().scale(range()));
		return golem.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, golem)).getLocation();
	}

	public static boolean canSee(AbstractGolemEntity<?, ?> golem, Vec3 center, Vec3 target) {
		if (golem.hasFlag(GolemFlags.SEE_THROUGH)) return true;
		return golem.level().clip(new ClipContext(center, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, golem)).getType() == HitResult.Type.MISS;
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new AtomicBreathAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public void onClientTick(AbstractGolemEntity<?, ?> golem, int level) {
		AtomicBeamSound.update(golem);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		var val = Component.translatable(ACEffectRegistry.IRRADIATED.get().getDescriptionId());
		val = Component.translatable("potion.withAmplifier", val, Component.translatable("potion.potency." + IRRADIATED_AMPLIFIER));
		val = Component.translatable("potion.withDuration", val, Component.literal(StringUtil.formatTickDuration(IRRADIATED_DURATION)));
		val = val.withStyle(ChatFormatting.RED);
		return List.of(Component.translatable(getDescriptionId() + ".desc", val).withStyle(ChatFormatting.GREEN));
	}

}
