package dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.ThrownPhantomDaggerEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Paladin head modifier: throws three phantom daggers that home onto a single target.
 * Reference: PosessedPaladinEntity::throwPhantomDaggers (ThrownPhantomDaggerEntity).
 */
public class PaladinSoulDaggerModifier extends GolemModifier {

	/** 冷却（tick）。 */
	public static final int COOLDOWN = 200;

	/** 每级减半冷却（参考灾变鸣雷槊）。 */
	public static int cooldownFor(int lv) {
		return Math.max(1, COOLDOWN >> (lv - 1));
	}

	public PaladinSoulDaggerModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new PaladinSoulDaggerAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		retargetDaggers(golem);
	}

	/**
	 * 堕魂头盔：幻影匕首锁定的目标死亡/失效后，自动改锁一个新的可用目标
	 * （尽量不与其它匕首重复，照遗魂沙暴的分配方式）。
	 * <p>
	 * 做成静态入口，供「幻影匕首」和「格挡反击」两个 Modifier 都调用；扫描的是傀儡名下的全部幻影匕首，
	 * 所以格挡反击召唤的匕首也会被改锁。
	 */
	public static void retargetDaggers(AbstractGolemEntity<?, ?> golem) {
		if (golem.level().isClientSide()) return;
		if (!LMArmorHelper.paladinHelmet(golem)) return;
		List<ThrownPhantomDaggerEntity> daggers = golem.level().getEntitiesOfClass(
				ThrownPhantomDaggerEntity.class, golem.getBoundingBox().inflate(64),
				d -> d.getOwner() == golem);
		if (daggers.isEmpty()) return;
		List<LivingEntity> taken = new ArrayList<>();
		boolean needs = false;
		for (var d : daggers) {
			LivingEntity t = d.returnEntity();
			if (t != null && t.isAlive() && GolemTargetUtil.canTarget(golem, t)) {
				taken.add(t);
			} else {
				needs = true;
			}
		}
		if (!needs) return;
		List<LivingEntity> pool = golem.level().getEntitiesOfClass(LivingEntity.class,
				golem.getBoundingBox().inflate(48),
				e -> e != golem && GolemTargetUtil.canTarget(golem, e));
		pool.sort(Comparator.comparingDouble(golem::distanceToSqr));
		for (var d : daggers) {
			LivingEntity t = d.returnEntity();
			if (t != null && t.isAlive() && GolemTargetUtil.canTarget(golem, t)) continue;
			for (LivingEntity cand : pool) {
				if (taken.contains(cand)) continue;
				d.setReturnEntity(cand);
				taken.add(cand);
				break;
			}
		}
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", Component.literal("3").withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GREEN));
	}

}
