package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.capability.GolemConfigCapability;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.item.card.ClickEntityFilterCard;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModularGolems.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModifierEventListeners {

	@SubscribeEvent
	public static void onGolemSpawn(EntityJoinLevelEvent event) {
		// 首先检查事件是否发生在客户端，如果是，则直接返回，不执行任何逻辑
		if (event.getLevel().isClientSide()) return;
		// 检查事件中的实体是否为AbstractGolemEntity的实例
		if (event.getEntity() instanceof AbstractGolemEntity<?, ?> entity) {
			// 遍历该傀儡的所有修饰符（modifiers），并调用每个修饰符的onGolemSpawn方法，传入傀儡实例和修饰符的值，以便对傀儡的属性进行初始化或修改
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
			if (!entity.canAttack(event.getEntity())) {
				event.setCanceled(true);
				return;
			}
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
	public static void onKillTarget(LivingDeathEvent event) {
		if (event.getSource().getEntity() instanceof AbstractGolemEntity<?, ?> golem) {
			golem.getModifiers().forEach((k, v) -> k.onKillTarget(golem, event.getEntity(), event, v));
		}
	}


	@SubscribeEvent
	public static void onExplosion(ExplosionEvent.Detonate event) {
		for (var e : event.getAffectedEntities()) {
			// 如果有傀儡实体
			if (e instanceof AbstractGolemEntity<?, ?> golem) {
				// 且实体有爆炸抗性修饰符,并且其值大于0，则取消爆炸对所有方块的影响
				if (golem.getModifiers().getOrDefault(GolemModifiers.EXPLOSION_RES.get(), 0) > 0) {
					event.getAffectedBlocks().clear();
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
		if (!MGConfig.COMMON.doEnemyAggro.get()) return;
		if (event.getEntity() instanceof Mob mob && !event.getLevel().isClientSide()) {
			if (mob instanceof Enemy && !(mob instanceof Creeper)) {
				int priority = 0;
				TargetGoal ans = null;
				for (var goal : mob.targetSelector.getAvailableGoals()) {
					if (goal.getGoal() instanceof NearestAttackableTargetGoal<?> target) {
						if (target.targetType == IronGolem.class) {
							priority = goal.getPriority();
							ans = new NearestAttackableTargetGoal<>(mob, AbstractGolemEntity.class,
									target.randomInterval, target.mustSee, target.mustReach,
									e -> e instanceof AbstractGolemEntity<?, ?> golem && !golem.isHostile());
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

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingDrop(LivingDropsEvent event) {
		// 检查掉落物品的来源是否为AbstractGolemEntity实例
		if (event.getSource().getEntity() instanceof AbstractGolemEntity<?, ?> e) {
			// 进一步检查该傀儡是否具有拾取掉落物的标志（GolemFlags.PICKUP）
			if (e.hasFlag(GolemFlags.PICKUP)) {
				// 如果具有，则将所有掉落物移动到该傀儡所在的位置
				event.getDrops().forEach(x -> x.moveTo(e.position()));
			}
		}
	}

	@SubscribeEvent
	public static void onAttachLevelCapabilities(AttachCapabilitiesEvent<Level> event) {
		if (event.getObject() instanceof ServerLevel level) {
			if (level.dimension() == Level.OVERWORLD) {
				event.addCapability(new ResourceLocation(ModularGolems.MODID, "command"),
						new GolemConfigCapability(level));
			}
		}
	}

	@SubscribeEvent
	public static void onTargetCardClick(PlayerInteractEvent.EntityInteract event) {
		if (event.getItemStack().getItem() instanceof ClickEntityFilterCard<?>) {
			if (event.getTarget() instanceof LivingEntity le) {
				event.setCancellationResult(event.getItemStack().interactLivingEntity(event.getEntity(),
						le, event.getHand()));
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onEffectApply(MobEffectEvent.Applicable event) {
		if (event.getEntity() instanceof AbstractGolemEntity<?, ?> golem) {

		}
	}

}
