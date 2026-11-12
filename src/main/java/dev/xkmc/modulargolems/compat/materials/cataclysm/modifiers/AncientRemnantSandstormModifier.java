package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.List;
import java.util.function.BiConsumer;

public class AncientRemnantSandstormModifier extends GolemModifier {
	public static final int COOLDOWN = 400;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 35;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	public AncientRemnantSandstormModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new AncientRemnantSandstormAttackGoal(entity, lv).withName(this.getDescriptionId()));
		addGoal.accept(6, new AncientRemnantSandstormTrackGoal(entity));
	}

	@Override
	public void modifyDamage(AttackCache cache, AbstractGolemEntity<?, ?> entity, int level) {
		var event = cache.getLivingHurtEvent();
		if (event == null) return;
		int lv = GolemCataProxy.getSandCurseLevel(cache.getAttackTarget());
		if (lv > 0) {
			cache.addHurtModifier(DamageModifier.multTotal(1 + level * MGConfig.COMMON.sandCurseBonus.get().floatValue()));
		}
	}

	/**
	 * 远古金属头盔：遗魂沙暴造成的伤害无视目标无敌帧。
	 */
	@Override
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (!CataArmorHelper.ancientHelmet(entity)) return;
		if (!GolemCataProxy.isSandstorm(event.getSource())) return;
		event.getEntity().invulnerableTime = 0;
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc",
				CataArmorHelper.healthPct(CataArmorHelper.HALF_HEALTH)).withStyle(ChatFormatting.GREEN));
	}

}
