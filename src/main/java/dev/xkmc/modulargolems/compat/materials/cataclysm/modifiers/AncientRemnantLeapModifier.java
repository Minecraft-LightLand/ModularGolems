package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import dev.xkmc.modulargolems.util.GolemTargetUtil;

public class AncientRemnantLeapModifier extends GolemModifier implements EarthquakeHelper.Modifier {
	public static final int COOLDOWN = 200;
	/** 起跳可及的最大水平距离（格）。 */
	public static final int MAX_RANGE = 25;

	public AncientRemnantLeapModifier(StatFilterType type, int maxLevel) {
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
	 * 远古金属护腿：冲击波造成的伤害无视目标无敌帧（不再额外加伤害）。
	 */
	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (!CataArmorHelper.ancientLegs(entity)) return;
		if (!GolemCataProxy.isEarthQuake(event.getSource())) return;
		event.getEntity().invulnerableTime = 0;
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return (double) MAX_RANGE * MAX_RANGE;
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int lv) {
		AncientRemnantChargeModifier.addChargeReduction(golem);
		var level = golem.level();
		GolemCataProxy.remnantTailSlamSound(golem);
		GolemCataProxy.screenShake(golem, 30.0F);
		float atk = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		var aabb = golem.getBoundingBox().inflate(5, 3, 5);
		List<LivingEntity> list = new ArrayList<>();
		for (var e : level.getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			float dmg = atk + Math.min(atk, e.getMaxHealth() * 0.05f);
			GolemCombatHelper.meleeHit(golem, e, golem.damageSources().mobAttack(golem), dmg);
			EarthquakeHelper.launch(golem, e, 0.6f);
			list.add(e);
		}
		int count = 22 + golem.getRandom().nextInt(8);
		// 远古金属护腿：冲击波改成各自追踪目标，目标均摊到每一道冲击波
		if (CataArmorHelper.ancientLegs(golem)) {
			var targets = collectShockwaveTargets(golem);
			int n = targets.size();
			for (int i = 0; i < count; i++) {
				float angle = golem.getYRot() + 90 + i * 360F / count;
				var dir = new Vec3(Math.cos(Math.toRadians(angle)), 0, Math.sin(Math.toRadians(angle)));
				// 均摊：一个目标则全部追它，多个目标则按 i%n 轮流分配
				LivingEntity target = n == 0 ? null : targets.get(i % n);
				GolemCataProxy.spawnEarthQuakeTracking(golem, dir, target);
			}
			return;
		}
		float yaw = golem.getYRot(), pitch = golem.getXRot();
		for (int i = 0; i < count; i++) {
			float angle = yaw + 90 + i * 360F / count;
			golem.setYRot(angle);
			golem.setXRot(0);
			GolemCataProxy.spawnEarthQuake(golem);
		}
		golem.setYRot(yaw);
		golem.setXRot(pitch);
		golem.yBodyRot = yaw;
		golem.yHeadRot = yaw;
	}

	/**
	 * 冲击波可以追踪的目标：落点附近还活着、可攻击的实体，按离傀儡由近到远排序。
	 * 主目标如果被落点砸死就不在列表里，剩下的就近目标仍会被追。
	 */
	private static List<LivingEntity> collectShockwaveTargets(AbstractGolemEntity<?, ?> golem) {
		var list = golem.level().getEntities(
				net.minecraft.world.level.entity.EntityTypeTest.forClass(LivingEntity.class),
				golem.getBoundingBox().inflate(32),
				e -> GolemTargetUtil.canTarget(golem, e));
		list.sort(java.util.Comparator.comparingDouble(golem::distanceToSqr));
		if (list.isEmpty()) {
			LivingEntity main = golem.getTarget();
			if (main != null && main.isAlive() && !main.isRemoved()) list.add(main);
		}
		return list;
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		// 远古金属胸甲：半血以下（或被胸甲钉在最终形态）时冷却减半
		return CataArmorHelper.ancientFastSkill(golem) ? COOLDOWN / 2 : COOLDOWN;
	}


	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc",
				EarthquakeHelper.leapReduction(), CataArmorHelper.healthPct(CataArmorHelper.HALF_HEALTH)).withStyle(ChatFormatting.GREEN));
	}

}
