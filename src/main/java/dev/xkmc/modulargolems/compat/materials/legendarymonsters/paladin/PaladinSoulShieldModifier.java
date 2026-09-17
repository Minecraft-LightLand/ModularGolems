package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.cataclysm_mux.MWCataProxy;
import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
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
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.function.Consumer;

/**
 * 堕落魂盾阵：跃落后以自身为中心召唤护盾。
 * 参考 PossessedPaladinEntity 的护盾技能。
 * 红色变色：当 golem 血量低于最大值 65% 时变红。
 * 推荐大型傀儡 (TYPE_GOLEM) 与下肢 (MOVEMENT) 部件。
 */
public class PaladinSoulShieldModifier extends GolemModifier {

	public PaladinSoulShieldModifier() {
		super(StatFilterType.HEALTH, 1);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> golem, LivingDamageEvent event, int level) {
		var attacker = event.getSource().getEntity();
		if (!(attacker instanceof LivingEntity le)) return;
		// 此处仿ScyllaWaveAttackModifier
		long time = golem.level().getGameTime();
		var last = golem.getPersistentData().getLong("SoulShieldTime");
		if (last < time && last > time - 40) return;
		golem.getPersistentData().putLong("SoulShieldTime", time);
		LMProxy.spawnPaladinSoulShield(golem,level);
	}

	@Override
	public boolean canExistOn(GolemPart<?, ?> part) {
		return part.getEntityType() == GolemTypes.TYPE_GOLEM.get() && super.canExistOn(part);
    }

}