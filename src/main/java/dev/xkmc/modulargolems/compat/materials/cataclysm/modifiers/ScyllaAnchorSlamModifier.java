package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.data.MGConfig;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ScyllaAnchorSlamModifier extends GolemModifier implements EarthquakeHelper.Modifier {
	public static final int COOLDOWN = 160;
	/** 起跳可及的最大水平距离（格）。 */
	public static final int MAX_RANGE = 25;

	public ScyllaAnchorSlamModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
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
		return (double) MAX_RANGE * MAX_RANGE;
	}

	/**
	 * 风暴护腿：所有技能留下的电火花（cataclysm:spark 及其落地后的闪电区域）伤害提高。
	 */
	@Override
	public void modifyDamage(AttackCache cache, AbstractGolemEntity<?, ?> entity, int level) {
		var event = cache.getLivingHurtEvent();
		if (event == null) return;
		if (!CataArmorHelper.stormLegs(entity)) return;
		if (!ScyllaInvulnerabilityBypass.isSpark(event.getSource())) return;
		cache.addHurtModifier(DamageModifier.multTotal(1 + MGConfig.COMMON.sparkArmorBonus.get().floatValue()));
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int lv) {
		var ans = findFloor(golem.level(), golem.position(), 16);
		if (ans == null) return;
		GolemCataProxy.scyllaAnchorSlamFX(golem);
		// 本体 AnchorThrow（attackState 13）按阶段给火花数量：满血 2 道、phase1（≤2/3）4 道、phase2（≤1/3）6 道。
		// 风暴胸甲把傀儡钉在最终形态，直接按 6 道算。
		int bolts;
		if (CataArmorHelper.stormChest(golem) || CataArmorHelper.isBelow(golem, CataArmorHelper.ONE_THIRD)) {
			bolts = 6;
		} else if (CataArmorHelper.isBelow(golem, CataArmorHelper.TWO_THIRDS)) {
			bolts = 4;
		} else {
			bolts = 2;
		}
		GolemCataProxy.scyllaFallingBurstAt(golem, ans.x, ans.y, ans.z, bolts);
		// 风暴护腿：完整形态（半血以下，或被风暴胸甲钉住）时额外触发一次闪电重砸
		if (CataArmorHelper.stormLegs(golem) && CataArmorHelper.stormFinalForm(golem)) {
			lightningSmash(golem, ans);
		}
	}

	/**
	 * 闪电重砸：按 wiki 描述复刻斯库拉的锚暴摔落点效果——大量电火花向四周坠落、落雷与静电场同时铺开。
	 */
	private static void lightningSmash(AbstractGolemEntity<?, ?> golem, Vec3 at) {
		// 闪电重砸无视无敌帧：先把落点附近的受击无敌帧清零，避免被前面的锚砸吞掉伤害
		for (var e : golem.level().getEntitiesOfClass(LivingEntity.class,
				new AABB(at.x - 6, at.y - 6, at.z - 6, at.x + 6, at.y + 6, at.z + 6))) {
			if (e == golem || e.isAlliedTo(golem)) continue;
			e.invulnerableTime = 0;
		}
		// 落雷 + 范围伤害
		GolemCataProxy.scyllaLightningExplosion(golem, at.x, at.y, at.z);
		// 电火花四散落地
		for (int i = 0; i < 8; i++) {
			GolemCataProxy.shootSpark(golem, 2.0F);
		}
		// 周围铺开静电场
		for (int i = 0; i < 6; i++) {
			double angle = i * Math.PI * 2.0 / 6.0;
			double x = at.x + Math.cos(angle) * 3.5;
			double z = at.z + Math.sin(angle) * 3.5;
			GolemCataProxy.spawnLightningArea(golem, x, at.y, z);
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
		return List.of(Component.translatable(getDescriptionId() + ".desc", EarthquakeHelper.leapReduction()).withStyle(ChatFormatting.GREEN));
	}

}
