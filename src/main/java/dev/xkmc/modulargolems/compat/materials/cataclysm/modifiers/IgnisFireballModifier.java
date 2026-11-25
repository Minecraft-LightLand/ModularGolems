package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import java.util.List;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;
import dev.xkmc.modulargolems.content.entity.common.GolemFormAppearance;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;

public class IgnisFireballModifier extends GolemModifier {

	public static final int COOLDOWN = 200;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 25;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	/**
	 * 腾炎头盔：火球爆炸半径的放大比例。本体 {@code Ignis_Fireball_Entity} 把爆炸半径写死成 1，
	 * 这里由 {@code IgnisExplosionMixin} 在构造 {@code IgnisExplosion} 时按比例放大。
	 */
	public static final float EXPLOSION_RADIUS_BONUS = 1.0F;


	private static final int[] ANGLE = {-5, -2, 0, 2, 5};
	private static final int[] TIMER_3 = {28, 41, 54};
	private static final int[] TIMER_5 = {45, 61, 77, 93, 109};

	public IgnisFireballModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new IgnisFireballAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	public static void addFireball(LivingEntity user, int lv) {
		user.level().playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.EVOKER_PREPARE_SUMMON, user.getSoundSource(), 5.0F, 1.4F + user.getRandom().nextFloat() * 0.1F);
		int index = user.getRandom().nextInt(5);
		lv = Mth.clamp(lv, 0, 2);
		int from = 2 - lv;
		int count = 1 + lv * 2;
		int[] timers = count >= 4 ? TIMER_5 : TIMER_3;
		boolean blue = GolemFormAppearance.visualFinal(user, CataDispatch.ignisBlue(user), CataArmorHelper.TWO_THIRDS);
		int order = 0;
		for (int i = from; i < from + count; i++) {
			if (i == index) continue;
			GolemCataProxy.shootFireball(user, new Vec3(ANGLE[i], 3.0D, 0.0D), timers[order++], false, blue);
		}
		if (index >= from && index < from + count) {
			GolemCataProxy.shootFireball(user, new Vec3(ANGLE[index], 3.0D, 0.0D), timers[order], true, blue);
		}
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		int count = 1 + Mth.clamp(v, 0, 2) * 2;
		return List.of(Component.translatable(getDescriptionId() + ".desc",
				CataArmorHelper.number(count)).withStyle(ChatFormatting.GREEN));
	}

	/**
	 * 腾炎头盔：火球发射后持续追踪目标，保证必定命中。
	 * <p>
	 * 火球本体是灾变的 {@code Ignis_Fireball_Entity}，没有制导能力，所以在傀儡这一侧每 tick
	 * 把它的速度方向掰向目标当前眼睛位置，速度大小保持不变。
	 * <p>
	 * 只对「已经发射」的火球生效：本体用 {@code setUp(timer)} 让火球先悬停一段时间再射出去，
	 * {@code getFired()} 为 false 时还在待发阶段，此时不该转向，否则火球会在原地打转。
	 */
	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		if (golem.level().isClientSide()) return;
		if (!CataArmorHelper.ignisHelmet(golem)) return;
		LivingEntity target = golem.getTarget();
		if (target == null || !target.isAlive()) return;
		steer(golem, target, com.github.L_Ender.cataclysm.entity.projectile.Ignis_Fireball_Entity.class,
				proj -> ((com.github.L_Ender.cataclysm.entity.projectile.Ignis_Fireball_Entity) proj).getFired());
		steer(golem, target, com.github.L_Ender.cataclysm.entity.projectile.Ignis_Abyss_Fireball_Entity.class,
				proj -> ((com.github.L_Ender.cataclysm.entity.projectile.Ignis_Abyss_Fireball_Entity) proj).getFired());
	}

	private static void steer(AbstractGolemEntity<?, ?> golem, LivingEntity target,
						  Class<? extends net.minecraft.world.entity.projectile.Projectile> cls,
						  java.util.function.Predicate<net.minecraft.world.entity.projectile.Projectile> launched) {
		for (var proj : golem.level().getEntitiesOfClass(cls, golem.getBoundingBox().inflate(64))) {
			if (proj.getOwner() != golem) continue;
			if (!launched.test(proj)) continue;
			Vec3 cur = proj.getDeltaMovement();
			double speed = cur.length();
			if (speed < 1.0E-3) continue;
			proj.setDeltaMovement(target.getEyePosition().subtract(proj.position()).normalize().scale(speed));
		}
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, DamageData.Offence cache, int level) {
		var source = cache.getSource();
		var direct = source.getDirectEntity();
		if (direct == null || !GolemCataProxy.isIgnisExplosive(direct)) return;
		cache.addHurtModifier(DamageModifier.multTotal(MGConfig.COMMON.ignisSkillDamageFactor.get().floatValue(), getRegistryName()));
		if (entity.getItemBySlot(EquipmentSlot.HEAD).is(CataCompatRegistry.IGNIS_HELMET.get())) {
			cache.addHurtModifier(DamageModifier.multTotal(1 + MGConfig.COMMON.fireballArmorBonus.get().floatValue(), CataCompatRegistry.IGNIS_HELMET.getId()));
		}
	}

	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, DamageData.Attack event, int level) {
		var source = event.getSource();
		var direct = source.getDirectEntity();
		if (direct != null && GolemCataProxy.isIgnisExplosive(direct)) {
			if (GolemCataProxy.isSoul(direct)) {
				event.getTarget().invulnerableTime = 0;
				GolemCataProxy.inflictStun(entity, event.getTarget(), 20);
			}
		}
	}

	@Override
	public void postHurtTarget(AbstractGolemEntity<?, ?> golem, DamageData.DefenceMax cache, int level) {
		var source = cache.getSource();
		var direct = source.getDirectEntity();
		if (direct == null || !GolemCataProxy.isIgnisExplosive(direct)) return;
		LivingEntity target = cache.getTarget();
		float rate = MGConfig.COMMON.ignitiumHealRate.get().floatValue();
		CataDispatch.stackBlazingBrand(golem, target, rate * cache.getDamageFinal(), GolemCataProxy.isSoul(direct) ? 3 : 1);
	}

}
