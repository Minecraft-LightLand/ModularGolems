package dev.xkmc.modulargolems.events;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.item.card.ClickEntityFilterCard;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;

@EventBusSubscriber(modid = ModularGolems.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModifierEventListeners {

	@SubscribeEvent
	public static void onGolemSpawn(EntityJoinLevelEvent event) {
		if (event.getLevel().isClientSide()) return;
		if (event.getEntity() instanceof AbstractGolemEntity<?, ?> entity) {
			entity.getModifiers().forEach((k, v) -> k.onGolemSpawn(entity, v));
		}
	}

	@SubscribeEvent
	public static void onExplosion(ExplosionEvent.Detonate event) {
		var direct = event.getExplosion().getDirectSourceEntity();
		var owner = event.getExplosion().getIndirectSourceEntity();
		if (direct != null && (owner instanceof AbstractGolemEntity<?, ?> golem)) {
			if (!golem.isHostile()) event.getAffectedBlocks().clear();
			event.getAffectedEntities().removeIf(e -> {
				if (e instanceof ItemEntity) return true;
				if (e instanceof LivingEntity le) {
					if (!golem.canAttack(le)) return true;
				}
				if (e instanceof TraceableEntity proj) {
					return proj.getOwner() == golem;
				}
				return false;
			});
		}
		for (var e : event.getAffectedEntities()) {
			if (e instanceof AbstractGolemEntity<?, ?> golem) {
				if (golem.getModifiersExtended().getOrDefault(GolemModifiers.EXPLOSION_RES.get(), 0) > 0) {
					event.getAffectedBlocks().clear();
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
		if (event.getLevel().isClientSide()) return;
		if (!(event.getEntity() instanceof Mob mob)) return;
		if (!MGConfig.COMMON.doEnemyAggro.get()) return;
		if (!(mob instanceof Enemy) || mob instanceof Creeper) return;
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

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingDrop(LivingDropsEvent event) {
		if (event.getSource().getEntity() instanceof AbstractGolemEntity<?, ?> e) {
			if (e.hasFlag(GolemFlags.PICKUP)) {
				event.getDrops().forEach(x -> x.moveTo(e.position()));
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onTargetCardClick(PlayerInteractEvent.EntityInteract event) {
		ItemStack stack = event.getItemStack();
		if (stack.getItem() instanceof ClickEntityFilterCard<?>) {
			if (event.getTarget() instanceof LivingEntity le) {
				event.setCancellationResult(event.getItemStack().interactLivingEntity(event.getEntity(), le, event.getHand()));
				event.setCanceled(true);
			}
		} else if (event.getTarget() instanceof AbstractGolemEntity<?, ?> golem) {
			if (stack.is(MGTagGen.MODIFYING_ITEM) && !golem.canModify(event.getEntity())) {
				event.setCancellationResult(InteractionResult.FAIL);
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onMobGrief(EntityMobGriefingEvent event) {
		var e = event.getEntity();
		if (e instanceof TraceableEntity te) e = te.getOwner();
		if (e instanceof AbstractGolemEntity<?, ?> golem && !golem.isHostile())
			event.setCanGrief(false);
	}

}
