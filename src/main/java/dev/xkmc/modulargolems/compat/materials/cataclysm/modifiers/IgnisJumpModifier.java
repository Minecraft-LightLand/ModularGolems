package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataclysmProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

	public IgnisJumpModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return 25 * 25;
	}

	@Override
	public void performJump(AbstractGolemEntity<?, ?> golem, int lv) {
		var target = golem.getTarget();
		if (target == null)
			golem.addDeltaMovement(new Vec3(0, 1.3, 0));
		else golem.setDeltaMovement(
				(target.getX() - golem.getX()) * 0.15,
				1.3,
				(target.getZ() - golem.getZ()) * 0.15
		);
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int lv) {
		var level = golem.level();
		var aabb = golem.getBoundingBox().inflate(16, 6, 16);
		List<Vec3> list = new ArrayList<>();
		for (var e : level.getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!golem.predicateTarget(e)) continue;
			var ans = findFloor(level, e.position(), 4);
			if (ans == null) continue;
			boolean tooClose = false;
			for (var other : list) {
				if (other.distanceTo(ans) < 6) {
					tooClose = true;
					break;
				}
			}
			if (!tooClose) {
				list.add(ans);
			}
		}
		boolean soul = CataDispatch.ignisBlue(golem);
		float atk = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		if (soul) atk *= 4f / 3;
		var self = golem.position();
		list.sort(Comparator.comparingDouble(self::distanceToSqr));
		for (int i = 0; i < list.size(); i++) {
			CataclysmProxy.createBlast(golem, list.get(i), 40, i * 2, 3, atk, soul);
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
		if (direct == null || !CataclysmProxy.isIgnisStrike(direct)) return;
		if (entity.getItemBySlot(EquipmentSlot.HEAD).is(CataCompatRegistry.IGNIS_HELMET.get())) {
			cache.addHurtModifier(DamageModifier.multTotal(1 + MGConfig.COMMON.flameStrikeArmorBonus.get().floatValue(), getRegistryName()));
		}
	}

	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, DamageData.Attack event, int level) {
		var source = event.getSource();
		var direct = source.getDirectEntity();
		if (direct != null && CataclysmProxy.isIgnisStrike(direct)) {
			if (CataclysmProxy.isSoul(direct)) {
				event.getTarget().invulnerableTime = 0;
				CataclysmProxy.stun(event.getTarget(), 40);
			} else CataclysmProxy.stun(event.getTarget(), 20);
		}
	}

	@Override
	public void postHurtTarget(AbstractGolemEntity<?, ?> golem, DamageData.DefenceMax cache, int level) {
		var source = cache.getSource();
		var direct = source.getDirectEntity();
		if (direct == null || !CataclysmProxy.isIgnisStrike(direct)) return;
		LivingEntity target = cache.getTarget();
		float rate = MGConfig.COMMON.ignitiumHealRate.get().floatValue();
		CataclysmProxy.stackBlazingBrand(golem, target, rate * cache.getDamageFinal(), CataclysmProxy.isSoul(direct) ? 3 : 1);
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

}
