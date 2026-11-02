package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.function.BiConsumer;

public class HarbingerHomingMissileModifier extends GolemModifier {

	public HarbingerHomingMissileModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new HarbingerHomingMissileAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	/**
	 * 先行者胸甲加成的发射物：凋灵巡导弹、凋灵飞弹、凋灵榴弹。
	 */
	private static boolean isBoosted(DamageSource source) {
		return GolemCataProxy.isMissile(source) || GolemCataProxy.isWitherMissile(source) || GolemCataProxy.isHowitzer(source);
	}

	@Override
	public void modifyDamage(AttackCache cache, AbstractGolemEntity<?, ?> entity, int level) {
		var event = cache.getLivingHurtEvent();
		assert event != null;
		var source = event.getSource();
		if (isBoosted(source) && entity.getItemBySlot(EquipmentSlot.CHEST).is(CataCompatRegistry.HARBINGER_CHESTPLATE.get())) {
			cache.addHurtModifier(DamageModifier.multTotal(1 + MGConfig.COMMON.missileArmorBonus.get().floatValue()));
		}
	}

	/**
	 * 佩戴先行者胸甲时，导弹/飞弹/榴弹无视目标的受击无敌帧。
	 */
	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (!isBoosted(event.getSource())) return;
		if (!entity.getItemBySlot(EquipmentSlot.CHEST).is(CataCompatRegistry.HARBINGER_CHESTPLATE.get())) return;
		event.getEntity().invulnerableTime = 0;
	}

}
