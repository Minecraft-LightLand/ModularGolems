package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import dev.xkmc.modulargolems.util.GolemTargetUtil;

public class IgnisJumpModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	private static final String KEY_QUAKE_RADIUS = "IgnisQuakeRadius";
	private static final String KEY_QUAKE_TICK = "IgnisQuakeTimer";
	private static final String KEY_QUAKE_CENTER = "IgnisQuakeCenter";

	/**
	 * Radius of the {@code Flame_Strike_Entity} runes dropped under the nearby targets.
	 */
	private static final float RUNE_RADIUS = 2.0F;

	/**
	 * The landing point gets its own, noticeably wider rune so the centre of the quake reads as the boss
	 * slamming down rather than as one more target marker. Both spreads below are derived from these two,
	 * so changing a radius here moves the spacing with it.
	 */
	private static final float LANDING_RUNE_RADIUS = 5.0F;

	/**
	 * Runes closer than this to one another overlap into mush, so the candidates are spaced apart. Two
	 * ordinary runes just touch at this distance, since each is {@link #RUNE_RADIUS} wide.
	 */
	private static final double RUNE_SPREAD = RUNE_RADIUS * 2;

	public IgnisJumpModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	/**
	 * The quake wave counts down over several ticks, so its radius and parity need to survive between
	 * ticks. It is scratch state, not something the world should remember: keeping it in the persistent
	 * data dirties the NBT every other tick and writes it to the save file for no reason.
	 */
	private static int quakeRadius(AbstractGolemEntity<?, ?> golem) {
		Integer v = golem.getTransientState(KEY_QUAKE_RADIUS);
		return v == null ? 0 : v;
	}

	private static int quakeTick(AbstractGolemEntity<?, ?> golem) {
		Integer v = golem.getTransientState(KEY_QUAKE_TICK);
		return v == null ? 0 : v;
	}

	/**
	 * The point the collapsing wave pulls in on, recorded when the golem touched down. The wave runs for
	 * well after the landing, by which point the golem has already resumed pathing, so leaving the centre
	 * on the golem would drag the whole ring across the map behind it.
	 */
	private static Vec3 quakeCenter(AbstractGolemEntity<?, ?> golem) {
		return golem.getTransientState(KEY_QUAKE_CENTER);
	}

	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		if (golem.level().isClientSide()) return;
		int radius = quakeRadius(golem);
		if (radius <= 0) return;
		int timer = quakeTick(golem) + 1;
		if (timer < 2) {
			golem.setTransientState(KEY_QUAKE_TICK, timer);
			return;
		}
		golem.setTransientState(KEY_QUAKE_TICK, 0);
		boolean soul = CataDispatch.ignisBlue(golem);
		// forward offset is zero: that argument is how far the original swings its greatsword ahead of its
		// body, and these golems smash with their own legs instead, so there is nothing to reach past. Keeping
		// it at the original 2 would also shift the collapsing ring off the landing rune it belongs to.
		GolemCataProxy.ignisQuakeWaveAt(golem, quakeCenter(golem), radius, 2.5f, 0.0f, true, 1.1f, 0.06f, 0.075f, soul);
		golem.setTransientState(KEY_QUAKE_RADIUS, radius <= 6 ? null : radius - 1);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (EarthquakeHelper.isLeaping(entity)) event.setAmount(event.getAmount() * EarthquakeHelper.LEAP_DAMAGE_MULT);
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return 25 * 25;
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int lv) {
		var level = golem.level();
		boolean soul = CataDispatch.ignisBlue(golem);
		GolemCataProxy.ignisEarthquakeFX(golem, 20);
		GolemCataProxy.ignisAreaPunch(golem, 4.85f);
		golem.setTransientState(KEY_QUAKE_RADIUS, 16);
		golem.setTransientState(KEY_QUAKE_TICK, 2);
		var aabb = golem.getBoundingBox().inflate(16, 6, 16);
		var self = golem.position();
		List<Vec3> list = new ArrayList<>();
		// the landing point goes in first so every candidate is measured against it and yields if the two
		// would overlap, which leaves the big centre rune unobstructed instead of just adding one more mark
		Vec3 landing = findFloor(level, self, 4);
		if (landing != null) list.add(landing);
		// the wave keeps running for several dozen ticks, so pin its centre here while the golem is still
		// standing on the spot; without this it would slide along with the golem as it walks off
		golem.setTransientState(KEY_QUAKE_CENTER, landing != null ? landing : self);
		for (var e : level.getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			var ans = findFloor(level, e.position(), 4);
			if (ans == null) continue;
			boolean tooClose = false;
			for (var other : list) {
				// spacing has to be the sum of the two radii, so the wider landing rune claims a wider gap
				// than the ordinary ones would; reusing the small-to-small distance would leave their edges
				// overlapping
				double spread = other == landing ? LANDING_RUNE_RADIUS + RUNE_RADIUS : RUNE_SPREAD;
				if (other.distanceTo(ans) < spread) {
					tooClose = true;
					break;
				}
			}
			if (!tooClose) {
				list.add(ans);
			}
		}
		float atk = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		if (soul) atk *= 4f / 3;
		list.sort(Comparator.comparingDouble(self::distanceToSqr));
		for (int i = 0; i < list.size(); i++) {
			// the landing point is the nearest candidate by construction, but compare by identity rather than
			// assume, so a missing ground does not hand its larger radius to an ordinary target rune
			boolean isLanding = list.get(i) == landing;
			GolemCataProxy.createBlast(golem, list.get(i), 40, i * 2,
					isLanding ? LANDING_RUNE_RADIUS : RUNE_RADIUS, atk, soul);
		}
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		return 200;
	}

	@Override
	public void modifyDamage(AttackCache cache, AbstractGolemEntity<?, ?> entity, int level) {
		var event = cache.getLivingHurtEvent();
		assert event != null;
		var source = event.getSource();
		var direct = source.getDirectEntity();
		if (direct == null || !GolemCataProxy.isIgnisStrike(direct)) return;
		cache.addHurtModifier(DamageModifier.multTotal(MGConfig.COMMON.ignisSkillDamageFactor.get().floatValue()));
		if (entity.getItemBySlot(EquipmentSlot.LEGS).is(CataCompatRegistry.IGNIS_SHINGUARD.get())) {
			cache.addHurtModifier(DamageModifier.multTotal(1 + MGConfig.COMMON.flameStrikeArmorBonus.get().floatValue()));
		}
	}

	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		var source = event.getSource();
		var direct = source.getDirectEntity();
		if (direct != null && GolemCataProxy.isIgnisStrike(direct)) {
			if (GolemCataProxy.isSoul(direct)) {
				event.getEntity().invulnerableTime = 0;
				GolemCataProxy.inflictStun(entity, event.getEntity(), 20);
			}
		}
	}

	@Override
	public void finalizeHurtTarget(AttackCache cache, AbstractGolemEntity<?, ?> golem, int level) {
		var event = cache.getLivingDamageEvent();
		assert event != null;
		var source = event.getSource();
		var direct = source.getDirectEntity();
		if (direct == null || !GolemCataProxy.isIgnisStrike(direct)) return;
		LivingEntity target = cache.getAttackTarget();
		float rate = MGConfig.COMMON.ignitiumHealRate.get().floatValue();
		CataDispatch.stackBlazingBrand(golem, target, rate * cache.getDamageDealt(), GolemCataProxy.isSoul(direct) ? 3 : 1);
	}

	@Nullable
	private static Vec3 findFloor(Level level, Vec3 p, int diff) {
		double x = p.x, maxY = p.y, z = p.z, minY = maxY - diff;
		BlockPos pos = BlockPos.containing(x, maxY, z);
		double dy = 0.0F;

		do {
			BlockPos low = pos.below();
			BlockState lowState = level.getBlockState(low);
			if (lowState.isFaceSturdy(level, low, Direction.UP)) {
				if (!level.isEmptyBlock(pos)) {
					BlockState state = level.getBlockState(pos);
					VoxelShape shape = state.getCollisionShape(level, pos);
					if (!shape.isEmpty()) {
						dy = shape.max(Direction.Axis.Y);
					}
				}
				return new Vec3(x, pos.getY() + dy, z);
			}
			pos = pos.below();
		} while (pos.getY() >= minY);
		return null;
	}


	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", EarthquakeHelper.leapReduction()).withStyle(ChatFormatting.GREEN));
	}

}
