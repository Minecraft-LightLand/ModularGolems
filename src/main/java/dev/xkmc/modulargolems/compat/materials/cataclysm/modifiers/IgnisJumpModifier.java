package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class IgnisJumpModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	private static final String KEY_QUAKE_RADIUS = "IgnisQuakeRadius";
	private static final String KEY_QUAKE_TICK = "IgnisQuakeTimer";
	private static final String KEY_QUAKE_CENTER = "IgnisQuakeCenter";

	private static final float RUNE_RADIUS = 2.0F;
	private static final float LANDING_RUNE_RADIUS = 5.0F;
	private static final double RUNE_SPREAD = RUNE_RADIUS * 2;

	public IgnisJumpModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	private static int quakeRadius(AbstractGolemEntity<?, ?> golem) {
		Integer v = golem.getTransientState(KEY_QUAKE_RADIUS);
		return v == null ? 0 : v;
	}

	private static int quakeTick(AbstractGolemEntity<?, ?> golem) {
		Integer v = golem.getTransientState(KEY_QUAKE_TICK);
		return v == null ? 0 : v;
	}

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
		GolemCataProxy.ignisQuakeWaveAt(golem, quakeCenter(golem), radius, 2.5f, 0.0f, true, 1.1f, 0.06f, 0.075f, soul);
		golem.setTransientState(KEY_QUAKE_RADIUS, radius <= 6 ? null : radius - 1);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, DamageData.Defence event, int level) {
		if (EarthquakeHelper.isLeaping(entity))
			event.addDealtModifier(DamageModifier.multTotal(EarthquakeHelper.LEAP_DAMAGE_MULT, getRegistryName()));
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
		Vec3 landing = findFloor(level, self, 4);
		if (landing != null) list.add(landing);
		golem.setTransientState(KEY_QUAKE_CENTER, landing != null ? landing : self);
		for (var e : level.getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			var ans = findFloor(level, e.position(), 4);
			if (ans == null) continue;
			boolean tooClose = false;
			for (var other : list) {
				double spread = other == landing ? LANDING_RUNE_RADIUS + RUNE_RADIUS : RUNE_SPREAD;
				if (other.distanceTo(ans) < spread) {
					tooClose = true;
					break;
				}
			}
			if (!tooClose) list.add(ans);
		}
		float atk = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		if (soul) atk *= 4f / 3;
		list.sort(Comparator.comparingDouble(self::distanceToSqr));
		for (int i = 0; i < list.size(); i++) {
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
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, DamageData.Offence cache, int level) {
		var source = cache.getSource();
		var direct = source.getDirectEntity();
		if (direct == null || !GolemCataProxy.isIgnisStrike(direct)) return;
		cache.addHurtModifier(DamageModifier.multTotal(MGConfig.COMMON.ignisSkillDamageFactor.get().floatValue(), getRegistryName()));
		if (entity.getItemBySlot(EquipmentSlot.LEGS).is(CataCompatRegistry.IGNIS_SHINGUARD.get())) {
			cache.addHurtModifier(DamageModifier.multTotal(1 + MGConfig.COMMON.flameStrikeArmorBonus.get().floatValue(), getRegistryName()));
		}
	}

	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, DamageData.Attack event, int level) {
		var source = event.getSource();
		var direct = source.getDirectEntity();
		if (direct != null && GolemCataProxy.isIgnisStrike(direct)) {
			if (GolemCataProxy.isSoul(direct)) {
				event.getTarget().invulnerableTime = 0;
				GolemCataProxy.inflictStun(entity, event.getTarget(), 20);
			}
		}
	}

	@Override
	public void postHurtTarget(AbstractGolemEntity<?, ?> golem, DamageData.DefenceMax cache, int level) {
		var source = cache.getSource();
		var direct = source.getDirectEntity();
		if (direct == null || !GolemCataProxy.isIgnisStrike(direct)) return;
		LivingEntity target = cache.getTarget();
		float rate = MGConfig.COMMON.ignitiumHealRate.get().floatValue();
		CataDispatch.stackBlazingBrand(golem, target, rate * cache.getDamageFinal(), GolemCataProxy.isSoul(direct) ? 3 : 1);
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
