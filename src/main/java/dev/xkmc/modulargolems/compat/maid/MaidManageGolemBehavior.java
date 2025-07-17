package dev.xkmc.modulargolems.compat.maid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.ImmutableMap;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class MaidManageGolemBehavior extends Behavior<EntityMaid> {

	public static void collectAll(EntityMaid owner, List<UUID> list) {
		var level = owner.level();
		if (!(level instanceof ServerLevel sl)) return;
		var inv = owner.getAvailableInv(false);
		var itr = list.iterator();
		while (itr.hasNext()) {
			var id = itr.next();
			if (sl.getEntity(id) instanceof AbstractGolemEntity<?, ?> golem && golem.isAlive()) {
				int index = getEmptySlot(inv);
				if (index < 0) break;
				var stack = golem.toItem(owner);
				inv.insertItem(index, stack, false);
			}
			itr.remove();
		}
	}

	public static boolean returnToInv(EntityMaid maid, ItemStack stack) {
		var inv = maid.getAvailableInv(false);
		int index = getEmptySlot(inv);
		return inv.insertItem(index, stack, false).isEmpty();
	}

	private static int getEmptySlot(IItemHandlerModifiable inv) {
		for (int i = 0; i < inv.getSlots(); i++) {
			if (inv.getStackInSlot(i).isEmpty() && inv.insertItem(i, GolemItems.HOLDER_GOLEM.asStack(), true).isEmpty()) {
				return i;
			}
		}
		return -1;
	}

	@Nullable
	public static BlockPos getRandomPos(Level sl, EntityType<?> type, EntityMaid owner, LivingEntity target, int r, int round) {
		BlockPos tpos = target.blockPosition();
		BlockPos opos = owner.blockPosition();
		Vec3 teye = target.getEyePosition();
		Vec3 oeye = target.getEyePosition();
		RandomSource rand = owner.getRandom();
		var diff = tpos.getCenter().subtract(opos.getCenter());
		int minDist = 4;
		int maxDist = getSummonRange(owner);
		var len = diff.length();
		double dist;
		if (len < maxDist - minDist) {
			dist = len / 2;
		} else if (len < maxDist + minDist) {
			dist = len - minDist;
		} else dist = maxDist;
		BlockPos pos = BlockPos.containing(opos.getCenter().add(diff.normalize().scale(dist)));
		for (int i = 0; i < round; ++i) {
			BlockPos p = pos.offset(rand.nextInt(0, r * 2 + 1) - r, 0,
					rand.nextInt(0, r * 2 + 1) - r
			);
			int h = -1;
			for (int y = 0; y < 3; y++) {
				if (sl.noCollision(type.getAABB(p.getX(), p.getY() + y, p.getZ()))) {
					h = y;
					break;
				}
			}
			if (h == -1) continue;
			Vec3 e = Vec3.atBottomCenterOf(p).add(0.0F, (type.getHeight() / 2.0F) + h, 0.0F);
			BlockHitResult thit = sl.clip(new ClipContext(teye, e, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, null));
			BlockHitResult ohit = sl.clip(new ClipContext(oeye, e, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, null));
			if (thit.getType() == HitResult.Type.MISS && ohit.getType() == HitResult.Type.MISS) {
				return p;
			}
		}
		return null;
	}

	private static boolean trySummon(EntityMaid owner, LivingEntity target, ArrayList<UUID> list, ItemStack stack) {
		if (!(stack.getItem() instanceof GolemHolder<?, ?> holder)) return false;
		var player = owner.getOwner() instanceof Player pl ? pl : null;
		var pos = getRandomPos(owner.level(), holder.getEntityType().type(), owner, target, 4, 6);
		if (pos == null) return false;
		return holder.summon(stack, owner.level(), Vec3.atBottomCenterOf(pos), player, e -> {
			e.setLeader(owner);
			e.setMode(GolemModes.FOLLOW.getID(), BlockPos.ZERO);
			list.add(e.getUUID());
			e.resetTarget(target);
		});
	}

	private int cooldown = 0;

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
		var itr = list.iterator();
		while (itr.hasNext()) {
			var id = itr.next();
			if (level.getEntity(id) instanceof AbstractGolemEntity<?, ?> golem) {
				int index = getEmptySlot(inv);
				if (index < 0) break;
				if (shouldCollect(owner, golem)) {
					var stack = golem.toItem(owner);
					inv.insertItem(index, stack, false);
					itr.remove();
				} else {
					if (golem.getTarget() != target)
						golem.resetTarget(target);
				}
			} else {
				itr.remove();
			}
		}
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.is(MGTagGen.GOLEM_HOLDERS)) continue;
			stack.inventoryTick(level, owner, 0, false);
		}
		if (cooldown > 0) cooldown--;
		else {
			for (int i = 0; i < inv.getSlots(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if (!stack.is(MGTagGen.GOLEM_HOLDERS)) continue;
				if (inv.extractItem(i, 1, true).isEmpty()) continue;
				var hp = GolemHolder.getHealth(stack);
				if (hp != -1 && hp < summonHealth(owner) * GolemHolder.getMaxHealth(stack)) continue;
				if (trySummon(owner, target, list, stack)) {
					inv.extractItem(i, 1, false);
					cooldown = 40;
				}
				break;
			}
		}
		owner.getBrain().setMemory(MaidRegistry.GOLEMS.get(), list);

	}

	protected void stop(ServerLevel worldIn, EntityMaid self, long gameTimeIn) {
		var opt = self.getBrain().getMemory(MaidRegistry.GOLEMS.get());
		if (opt.isPresent()) {
			var list = opt.get();
			MaidManageGolemBehavior.collectAll(self, list);
			self.getBrain().setMemory(MaidRegistry.GOLEMS.get(), list);
		}
	}

	private boolean shouldCollect(EntityMaid owner, AbstractGolemEntity<?, ?> golem) {
		return golem.isAlive() && (golem.getHealth() < golem.getMaxHealth() * collectHealth(owner) ||
				golem.distanceTo(owner) > 35);
	}

	private float collectHealth(EntityMaid maid) {
		return 0.2f;
	}


	private float summonHealth(EntityMaid maid) {
		return 0.4f;
	}

	private static int getSummonRange(EntityMaid maid) {
		return 10;
	}

}
