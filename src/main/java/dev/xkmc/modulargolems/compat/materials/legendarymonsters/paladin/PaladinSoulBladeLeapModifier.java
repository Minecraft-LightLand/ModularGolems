package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

/**
 * 堕落魂刃阵：跃向目标并召唤数根堕落魂刃（FallingSoulBladeEntity）。
 * 使用 LMProxy.spawnPaladinLeapBlades 调用 Legendary Monsters 的技能。
 * 红色变色：当 golem 血量低于最大值 65% 时变红。
 * 推荐大型傀儡 (TYPE_GOLEM) 与下肢 (MOVEMENT) 部件。
 */
public class PaladinSoulBladeLeapModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public PaladinSoulBladeLeapModifier() {
		super(StatFilterType.MOVEMENT, 1);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.EARTH_QUAKE);
	}

//	@Override
//	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
//		addGoal.accept(5, new PaladinLeapGoal(entity, lv));
//	}

	@Override
	public boolean fitsOn(GolemType<?, ?> type) {
		return type == GolemTypes.TYPE_GOLEM.get();
	}

	@Override
	public boolean canExistOn(GolemPart<?, ?> part) {
		return part.getEntityType() == GolemTypes.TYPE_GOLEM.get() && super.canExistOn(part);
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int level) {
		LMProxy.spawnPaladinLeapBlades(golem,level);
		if (golem.level() instanceof ServerLevel sl) {
			var list = LMProxy.stun(sl, golem.getX(), golem.getY(), golem.getZ(), golem, 5.0f, level * 2);
			for (var e : list) {
				EarthquakeHelper.launch(golem, e, 1.5f);
			}
		}
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return 25.0;
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		return 200;
	}

	//	@Override
//	public void performJump(AbstractGolemEntity<?, ?> golem, int lv) {
//		EarthquakeHelper.Modifier.super.performJump(golem, lv);
//	}
}