package dev.xkmc.modulargolems.compat.maid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.ImmutableMap;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class MaidManageGolemBehavior extends Behavior<EntityMaid> {

	private static final int CD = 40, HCD = 20, MAX_DIST = 35;

	private int summonCooldown = 0;
	private int healCooldown = 0;

	public MaidManageGolemBehavior() {
		super(ImmutableMap.of(
				MaidRegistry.GOLEMS.get(), MemoryStatus.REGISTERED,
				MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
				MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT
		), 1200);
	}

	protected boolean canStillUse(ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn) {
		return entityIn.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && this.checkExtraStartConditions(worldIn, entityIn);
	}

	protected void tick(ServerLevel level, EntityMaid owner, long gameTime) {
		var opt = owner.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
		if (opt.isEmpty()) return;
		var target = opt.get();
		var inv = owner.getAvailableInv(false);
		var list = new ArrayList<>(owner.getBrain().getMemory(MaidRegistry.GOLEMS.get()).orElse(List.of()));
		if (healCooldown > 0) healCooldown--;
		var helper = new GolemHealUtils(owner, inv);
		checkGolemsInLevel(level, owner, helper, inv, list, target);
		helper.tryFixGolem();
		if (summonCooldown > 0) summonCooldown--;
		else if (GolemSummonUtils.findAndSummonGolem(level, owner, inv, list, target))
			summonCooldown = CD;
		owner.getBrain().setMemory(MaidRegistry.GOLEMS.get(), list);
	}

	protected void stop(ServerLevel worldIn, EntityMaid self, long gameTimeIn) {
		GolemSummonUtils.collectAll(self);
	}

	private void checkGolemsInLevel(ServerLevel level, EntityMaid owner, GolemHealUtils helper, IItemHandlerModifiable inv, List<UUID> list, LivingEntity target) {
		var itr = list.iterator();
		while (itr.hasNext()) {
			var id = itr.next();
			if (level.getEntity(id) instanceof AbstractGolemEntity<?, ?> golem) {
				if (!golem.isAlive()) continue;
				if (shouldCollect(owner, golem)) {
					if (GolemSummonUtils.retrieveGolem(owner, inv, golem)) {
						itr.remove();
						continue;
					}
				}
				if (healCooldown <= 0) {
					if (helper.tryHealGolem(owner, golem)) {
						healCooldown = HCD;
					}
				}
				if (golem.getTarget() != target)
					golem.resetTarget(target);
			} else {
				itr.remove();
			}
		}
	}

	private boolean shouldCollect(EntityMaid owner, AbstractGolemEntity<?, ?> golem) {
		return golem.isAlive() && (golem.getGuardedDataImpl() < golem.getMaxHealth() * collectHealth(owner) ||
				golem.distanceTo(owner) > MAX_DIST);
	}

	private float collectHealth(EntityMaid maid) {
		return 0.25f;
	}

}
