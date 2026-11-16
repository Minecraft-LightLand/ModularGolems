package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;
import java.util.function.Consumer;

/**
 * 扑跃砸地（对应堕落圣骑 Boss 的 state 19→20→21 与 22→23→24 / 二阶段 32）：
 * 朝目标真实起跳，落地砸地：180°/3 格 24 伤 + 12 道锯齿剑刃 + 砸地粒子，
 * 并附带灵魂盾两波（Boss state25 的 spawnSoulShieldRing）。
 * 二阶段（生命 &lt;65%）落地后再补一段 32 的追加斩。
 */
public class PaladinSoulBladeLeapModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	/** 二阶段阈值（Boss 的 getPhase() >= 2）。 */
	public static final float PHASE_TWO = 0.65F;

	public PaladinSoulBladeLeapModifier(StatFilterType type, int maxLevel) {
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

	/**
	 * 与其它大跳技能一致：朝目标方向真实起跳（抛物线跃向目标），不瞬移。
	 * 落点由 {@code JumpAttackHelper} 检测落地后照常结算震地。
	 */
	@Override
	public void performJump(AbstractGolemEntity<?, ?> golem, int lv) {
		var target = golem.getTarget();
		if (target != null && target.isAlive()) {
			golem.setDeltaMovement((target.getX() - golem.getX()) * 0.15, 1.3, (target.getZ() - golem.getZ()) * 0.15);
		} else {
			golem.addDeltaMovement(new Vec3(0, 1.3, 0));
		}
		golem.hasImpulse = true;
	}

	@Override
	public boolean useServerJump() {
		return true;
	}

	/** 落地：state24 砸地（含锯齿剑刃）+ state25 灵魂盾；二阶段追加 state32。 */
	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int level) {
		LMProxy.paladinSlamLanding(golem, level);
		if (golem.getHealth() < golem.getMaxHealth() * PHASE_TWO) {
			LMProxy.paladinSlamSecond(golem, level);
		}
	}

	@Override
	public void handleEvent(AbstractGolemEntity<?, ?> golem, int value, byte event) {
		if (event == EarthquakeHelper.FLAG) LMProxy.lmSmashParticles(golem, 1.0f, 0.0f, 0.0f, 7.5f, 3.0f);
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return 25 * 25;
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		return 200;
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", EarthquakeHelper.leapReduction(), Component.literal(Math.round(PHASE_TWO * 100) + "%").withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GREEN));
	}

}
