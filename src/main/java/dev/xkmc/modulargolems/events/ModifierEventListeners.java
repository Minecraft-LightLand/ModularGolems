package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.ModConfig;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModifierEventListeners {

	@SubscribeEvent
	public static void onGolemSpawn(EntityJoinLevelEvent event) {
		if (event.getLevel().isClientSide()) return;
		if (event.getEntity() instanceof AbstractGolemEntity<?, ?> entity) {
			entity.getModifiers().forEach((k, v) -> k.onGolemSpawn(entity, v));
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onHurtPre(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		if (source.getEntity() instanceof AbstractGolemEntity<?, ?> entity) {
			entity.getModifiers().forEach((k, v) -> k.onHurtTarget(entity, event, v));
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onAttackPre(LivingAttackEvent event) {
		if (event.getSource().getEntity() instanceof AbstractGolemEntity<?, ?> entity) {
			entity.getModifiers().forEach((k, v) -> k.onAttackTarget(entity, event, v));
		}
	}

	@SubscribeEvent
	public static void onAttacked(LivingAttackEvent event) {
		if (event.getEntity() instanceof AbstractGolemEntity<?, ?> entity) {
			entity.getModifiers().forEach((k, v) -> k.onAttacked(entity, event, v));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onHurtPost(LivingHurtEvent event) {
		if (event.getEntity() instanceof AbstractGolemEntity<?, ?> entity) {
			entity.getModifiers().forEach((k, v) -> k.onHurt(entity, event, v));
		}
	}

	@SubscribeEvent
	public static void onDamaged(LivingDamageEvent event) {
		if (event.getEntity() instanceof AbstractGolemEntity<?, ?> entity) {
			entity.getModifiers().forEach((k, v) -> k.onDamaged(entity, event, v));
		}
	}

	@SubscribeEvent
	public static void onExplosion(ExplosionEvent.Detonate event) {
		for (var e : event.getAffectedEntities()) {
			if (e instanceof AbstractGolemEntity<?, ?> golem) {
				if (golem.getModifiers().getOrDefault(GolemModifiers.EXPLOSION_RES.get(), 0) > 0) {
					event.getAffectedBlocks().clear();
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
		if (!ModConfig.COMMON.doEnemyAggro.get()) return;
		if (event.getEntity() instanceof Mob mob && !event.getLevel().isClientSide()) {
			if (mob instanceof Enemy && !(mob instanceof Creeper)) {
				int priority = 0;
				TargetGoal ans = null;
				for (var goal : mob.targetSelector.getAvailableGoals()) {
					if (goal.getGoal() instanceof NearestAttackableTargetGoal<?> target) {
						if (target.targetType == IronGolem.class) {
							priority = goal.getPriority();
							ans = new NearestAttackableTargetGoal<>(mob, AbstractGolemEntity.class,
									target.randomInterval, target.mustSee, target.mustReach, null);
							break;
						}
					}
				}
				if (ans != null) {
					mob.targetSelector.addGoal(priority, ans);
				}
			}
		}
	}

}
