package dev.xkmc.modulargolems.compat.maid;

import com.github.tartaricacid.touhoulittlemaid.api.task.IAttackTask;
import com.github.tartaricacid.touhoulittlemaid.api.task.IRangedAttackTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class MaidSummonerTask implements IRangedAttackTask {

	public static final ResourceLocation UID = ModularGolems.loc("summon_golems");

	private static final int STOP_MOVING_DIST = 24;
	private static final int STOP_ATTACK_DIST = 35;

	public ResourceLocation getUid() {
		return UID;
	}

	public ItemStack getIcon() {
		return GolemItems.HOLDER_GOLEM.asStack();
	}

	@Nullable
	public SoundEvent getAmbientSound(EntityMaid maid) {
		return InitSounds.MAID_FIND_TARGET.get();
	}

	public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid maid) {
		return Lists.newArrayList(
				Pair.of(5, createStartAttack()),
				Pair.of(5, createStopAttack(maid)),
				Pair.of(5, createMoveToTarget(0.6f)),
				Pair.of(5, new MaidSummonerStrafingBehavior(20, 28)),
				Pair.of(5, new MaidManageGolemBehavior())
		);
	}

	public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createRideBrainTasks(EntityMaid maid) {
		return Lists.newArrayList(
				Pair.of(5, createStartAttack()),
				Pair.of(5, createStopAttack(maid)),
				Pair.of(5, new MaidManageGolemBehavior())
		);
	}

	private void stopAttack(EntityMaid self, LivingEntity target) {
		GolemSummonUtils.collectAll(self);
	}

	public List<Pair<String, Predicate<EntityMaid>>> getConditionDescription(EntityMaid maid) {
		return Collections.singletonList(Pair.of("has_golem_wand", MaidSummonerTask::hasGolemWand));
	}

	@Override
	public void performRangedAttack(EntityMaid maid, LivingEntity target, float v) {
	}

	@Override
	public float searchRadius(EntityMaid maid) {
		return 35;
	}

	public static boolean hasGolemWand(EntityMaid maid) {
		return maid.getMainHandItem().is(MGTagGen.GOLEM_OMNI_WAND);
	}

	private boolean farAway(LivingEntity target, EntityMaid maid) {
		return maid.distanceTo(target) > STOP_ATTACK_DIST;
	}

	private BehaviorControl<EntityMaid> createStartAttack() {
		return StartAttacking.create(MaidSummonerTask::hasGolemWand, IAttackTask::findFirstValidAttackTarget);
	}

	private BehaviorControl<EntityMaid> createStopAttack(EntityMaid maid) {
		return StopAttackingIfTargetInvalid.create(
				(target) -> !hasGolemWand(maid) || this.farAway(target, maid),
				this::stopAttack, true
		);
	}

	private BehaviorControl<EntityMaid> createMoveToTarget(float speed) {
		return BehaviorBuilder.create((ins) -> ins.group(
						ins.registered(MemoryModuleType.WALK_TARGET),
						ins.registered(MemoryModuleType.LOOK_TARGET),
						ins.registered(MemoryModuleType.INTERACTION_TARGET),
						ins.present(MemoryModuleType.ATTACK_TARGET),
						ins.registered(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
				).apply(ins, (
						toNavigate,
						toLook,
						interactTarget,
						attackTarget,
						targetList) ->
						(level, self, time) -> {
							var fix = ins.tryGet(interactTarget);
							if (fix.isPresent()) {
								toLook.set(new EntityTracker(fix.get(), true));
								if (self.closerThan(fix.get(), 1.5)) {
									toNavigate.erase();
								} else {
									toNavigate.set(new WalkTarget(new EntityTracker(fix.get(), false), speed, 0));
								}
							} else {
								LivingEntity atk = ins.get(attackTarget);
								Optional<NearestVisibleLivingEntities> optList = ins.tryGet(targetList);
								if (optList.isPresent() && optList.get().contains(atk) && self.closerThan(atk, STOP_MOVING_DIST)) {
									toNavigate.erase();
								} else {
									toLook.set(new EntityTracker(atk, true));
									toNavigate.set(new WalkTarget(new EntityTracker(atk, false), speed, 0));
								}
							}
							return true;
						})
		);
	}

}