package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
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
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;

import java.util.List;
import java.util.function.Consumer;

/**
 * 扑跃砸地（对应堕落圣骑 Boss 的 state 19→20→21 与 22→23→24 / 二阶段 32）：
 * 朝目标真实起跳，落地砸地：180°/3 格 24 伤 + 12 道锯齿剑刃 + 砸地粒子，
 * 并附带灵魂盾两波（Boss state25 的 spawnSoulShieldRing）。
 * 二阶段（生命 &lt;65%）落地后再补一段 32 的追加斩。
 */
public class PaladinSoulBladeLeapModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public PaladinSoulBladeLeapModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, DamageData.Defence event, int level) {
		if (EarthquakeHelper.isLeaping(entity)) event.addDealtModifier(DamageModifier.multTotal(EarthquakeHelper.LEAP_DAMAGE_MULT, getRegistryName()));
	}

	/**
	 * 与其它大跳技能一致：朝目标方向真实起跳（抛物线跃向目标），不瞬移。
	 * 落点由 {@code JumpAttackHelper} 检测落地后照常结算震地。
	 */
	/**
	 * 玩家骑乘时由客户端本地预测起跳（服务端再执行一次会和服务端目标算出不同的水平速度，
	 * 在客户端权威的坐骑上变成回拉/瞬移）。服务端只负责落地结算。
	 */
	@Override
	public boolean useServerJump() {
		return false;
	}

	/** 落地：state24 砸地（含锯齿剑刃）+ state25 灵魂盾；二阶段追加 state32。 */
	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int level) {
		LMProxy.paladinSlamLanding(golem, level);
		if (LMArmorHelper.paladinFinalForm(golem)) {
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
		return List.of(Component.translatable(getDescriptionId() + ".desc", EarthquakeHelper.leapReduction(),
				LMArmorHelper.phaseTwoPct()).withStyle(ChatFormatting.GREEN));
	}

}