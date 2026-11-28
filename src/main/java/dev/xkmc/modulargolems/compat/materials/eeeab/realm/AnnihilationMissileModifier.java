package dev.xkmc.modulargolems.compat.materials.eeeab.realm;

import dev.xkmc.modulargolems.compat.materials.eeeab.EEEABDispatch;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.function.BiConsumer;

/**
 * 界域戍卫 - 后跳齐射导弹。
 * <p>
 * 对应 {@code EntityRealmWarden.spawnAnnihilatorMissiles}：先向后跳跃拉开距离，落地后向两侧各发射 4（二阶段 5）枚
 * {@code EntityAnnihilatorMissile}。弹体虽是 Annihilator 的，但这个招式属于界域戍卫，不是 Annihilator。
 */
public class AnnihilationMissileModifier extends GolemModifier {

	/** 冷却（tick）。 */
	public static final int COOLDOWN = 100;

	/** 施放距离下限（格）。 */
	public static final int MIN_RANGE = 0;

	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 32;

	/** 齐射间隔（tick）。 */
	public static final int BURST_DELAY = 5;

	/** 等级只缩短冷却（每级减半，最低 1），与其它技能一致。 */
	public static int cooldownFor(int lv) {
		return Math.max(1, COOLDOWN >> (lv - 1));
	}

	/**
	 * EEEAB {@code EntityOverloadExplode} 的伤害类型。感电效果到期时爆炸，
	 * 由于 {@code EntityOverloadExplode} 只给 caster 赋值而没有给 {@code EntityExplode.owner} 赋值，
	 * 爆炸不会排除任何实体，近身命中的施加者自己也会吃到伤害。界域傀儡免疫此伤害。
	 */
	private static final ResourceKey<DamageType> OVERLOAD_EXPLODE =
			ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(EEEABDispatch.MODID, "overload_explode"));

	public AnnihilationMissileModifier() {
		super(StatFilterType.ATTACK, 3);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new AnnihilationMissileAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (event.getSource().is(OVERLOAD_EXPLODE)) {
			event.setCanceled(true);
		}
	}

}
