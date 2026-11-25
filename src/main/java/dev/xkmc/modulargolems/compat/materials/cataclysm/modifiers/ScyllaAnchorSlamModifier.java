package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ScyllaAnchorSlamModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public static final int COOLDOWN = 160;

	/** 起跳可及的最大水平距离（格）。 */
	public static final int MAX_RANGE = 25;

	/** 坠雷击各段伤害自落地起延续约 25 tick，这里留 15 tick 余量。 */
	public static final int BYPASS_WINDOW = 40;
	private static final String KEY_BYPASS_UNTIL = "ScyllaSlamBypassUntil";

	public ScyllaAnchorSlamModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	/** 坠雷击穿透窗口的截止 gameTime；没开窗时为 0。 */
	public static long slamBypassUntil(AbstractGolemEntity<?, ?> golem) {
		return golem.getPersistentData().getLong(KEY_BYPASS_UNTIL);
	}

	/** 风暴护腿：坠雷击直伤无视目标无敌帧（窗口内由 onAnchorSlamSource 放行）。 */
	@Override
	public void modifySource(AbstractGolemEntity<?, ?> golem, CreateSourceEvent event, int value) {
		ScyllaInvulnerabilityBypass.onAnchorSlamSource(golem, event);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, DamageData.Defence event, int level) {
		if (EarthquakeHelper.isLeaping(entity)) event.addDealtModifier(DamageModifier.multTotal(EarthquakeHelper.LEAP_DAMAGE_MULT, getRegistryName()));
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return (double) MAX_RANGE * MAX_RANGE;
	}

	/** 风暴护腿：所有技能留下的电火花（cataclysm:spark 及其落地后的闪电区域）伤害提高。 */
	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, DamageData.Offence cache, int level) {
		if (!CataArmorHelper.stormLegs(entity)) return;
		if (!ScyllaInvulnerabilityBypass.isSpark(cache.getSource())) return;
		cache.addHurtModifier(DamageModifier.multTotal(1 + MGConfig.COMMON.sparkArmorBonus.get().floatValue(), getRegistryName()));
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int lv) {
		var ans = findFloor(golem.level(), golem.position(), 16);
		if (ans == null) return;
		golem.getPersistentData().putLong(KEY_BYPASS_UNTIL, golem.level().getGameTime() + BYPASS_WINDOW);
		GolemCataProxy.scyllaAnchorSlamFX(golem);
		int bolts;
		if (CataArmorHelper.stormChest(golem) || CataArmorHelper.isBelow(golem, CataArmorHelper.ONE_THIRD)) {
			bolts = 6;
		} else if (CataArmorHelper.isBelow(golem, CataArmorHelper.TWO_THIRDS)) {
			bolts = 4;
		} else {
			bolts = 2;
		}
		float range = anchorSlamRangeScale(golem);
		GolemCataProxy.scyllaFallingBurstAt(golem, ans.x, ans.y, ans.z, bolts, range);
		if (CataArmorHelper.stormLegs(golem) && CataArmorHelper.stormFinalForm(golem)) {
			lightningSmash(golem, ans, range);
		}
	}

	/** 风暴护腿：坠雷击的攻击范围倍率（+100% 即 ×2）。 */
	public static float anchorSlamRangeScale(AbstractGolemEntity<?, ?> golem) {
		if (!CataArmorHelper.stormLegs(golem)) return 1.0F;
		return 1.0F + MGConfig.COMMON.anchorSlamRangeBonus.get().floatValue();
	}

	/** 闪电重砸：一次落雷结算 + 一圈电火花飞溅（无敌帧由护腿特性统一处理）。 */
	private static void lightningSmash(AbstractGolemEntity<?, ?> golem, Vec3 at, float range) {
		GolemCataProxy.scyllaLightningExplosion(golem, at.x, at.y, at.z, range);
		for (int i = 0; i < 8; i++) {
			GolemCataProxy.shootSparkAt(golem, at.x, at.y, at.z, 2.0F * range);
		}
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		return COOLDOWN;
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
		return List.of(Component.translatable(getDescriptionId() + ".desc",
				EarthquakeHelper.leapReduction(),
				CataArmorHelper.healthPct(CataArmorHelper.TWO_THIRDS)).withStyle(ChatFormatting.GREEN));
	}

}
