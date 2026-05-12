package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

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

public class IgnisFireballModifier extends GolemModifier {

	private static final int[] ANGLE = {-5, -2, 0, 2, 5};

	public IgnisFireballModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new IgnisFireballAttackGoal(entity, lv));
	}

	public static void addFireball(LivingEntity user, int lv) {
		user.level().playLocalSound(user.getX(), user.getY(), user.getZ(), SoundEvents.EVOKER_PREPARE_SUMMON,
				user.getSoundSource(), 5.0F, 1.4F + user.getRandom().nextFloat() * 0.1F, false);
		int index = user.getRandom().nextInt(5);
		lv = Mth.clamp(lv, 0, 2);
		for (int i = 2 - lv; i < 3 + lv; i++) {
			GolemCataProxy.shootFireball(user, new Vec3(ANGLE[i], 3.0D, 0.0D), 15 + i * 10, index == i, CataDispatch.ignisBlue(user));
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
