package dev.xkmc.modulargolems.compat.maid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class GolemSummonUtils {

	public static boolean findAndSummonGolem(ServerLevel level, EntityMaid owner, IItemHandlerModifiable inv, List<UUID> list, LivingEntity target) {
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.is(MGTagGen.GOLEM_HOLDERS)) continue;
			if (inv.extractItem(i, 1, true).isEmpty()) continue;
			var hp = GolemHolder.getHealth(stack);
			if (hp != -1 && hp < summonHealth(owner) * GolemHolder.getMaxHealth(stack)) continue;
			if (placeGolem(owner, target, list, stack)) {
				inv.extractItem(i, 1, false);
				return true;
			}
			return false;
		}
		return false;
	}

	public static void collectAll(EntityMaid owner) {
		var level = owner.level();
		if (!(level instanceof ServerLevel sl)) return;
		var opt = owner.getBrain().getMemory(MaidRegistry.GOLEMS.get());
		if (opt.isEmpty()) return;
		var list = opt.get();
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
		owner.getBrain().setMemory(MaidRegistry.GOLEMS.get(), list);
	}

	public static boolean returnToInv(EntityMaid maid, ItemStack stack) {
		var inv = maid.getAvailableInv(false);
		int index = getEmptySlot(inv);
		return inv.insertItem(index, stack, false).isEmpty();
	}

	public static boolean retrieveGolem(EntityMaid owner, IItemHandlerModifiable inv, AbstractGolemEntity<?, ?> golem) {
		int index = getEmptySlot(inv);
		if (index < 0) return false;
		var stack = golem.toItem(owner);
		inv.insertItem(index, stack, false);
		return true;
	}

	@Nullable
	private static BlockPos getRandomPos(Level sl, EntityType<?> type, EntityMaid owner, LivingEntity target, int r, int round) {
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
				if (sl.noCollision(type.getSpawnAABB(p.getX(), p.getY() + y, p.getZ()))) {
					h = y;
					break;
				}
			}
			if (h == -1) continue;
			Vec3 e = Vec3.atBottomCenterOf(p).add(0.0F, (type.getHeight() / 2.0F) + h, 0.0F);
			BlockHitResult thit = sl.clip(new ClipContext(teye, e, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, CollisionContext.empty()));
			BlockHitResult ohit = sl.clip(new ClipContext(oeye, e, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, CollisionContext.empty()));
			if (thit.getType() == HitResult.Type.MISS && ohit.getType() == HitResult.Type.MISS) {
				return p;
			}
		}
		return null;
	}

	private static boolean placeGolem(EntityMaid owner, LivingEntity target, List<UUID> list, ItemStack stack) {
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

	private static int getEmptySlot(IItemHandlerModifiable inv) {
		for (int i = 0; i < inv.getSlots(); i++) {
			if (inv.getStackInSlot(i).isEmpty() && inv.insertItem(i, GolemItems.HOLDER_GOLEM.asStack(), true).isEmpty()) {
				return i;
			}
		}
		return -1;
	}

	private static float summonHealth(EntityMaid maid) {
		return 0.5f;
	}

	private static int getSummonRange(EntityMaid maid) {
		return 10;
	}

}
