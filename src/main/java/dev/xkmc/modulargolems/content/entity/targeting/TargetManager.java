package dev.xkmc.modulargolems.content.entity.targeting;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.hostile.HostileGolemRegistry;
import dev.xkmc.modulargolems.content.item.card.DefaultFilterCard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static dev.xkmc.modulargolems.content.entity.targeting.TargetingReason.*;

public class TargetManager {

	private static final Map<ServerLevel, Map<UUID, TargetManager>> MAP = new ConcurrentHashMap<>();
	private static final TargetManager DUMMY = new TargetManager();

	public static TargetManager get(AbstractGolemEntity<?, ?> golem) {
		var level = golem.level();
		if (!(level instanceof ServerLevel sl)) return DUMMY;
		var id = golem.getOwnerUUID();
		if (id == null) return DUMMY;
		return MAP.computeIfAbsent(sl, e -> new LinkedHashMap<>())
				.computeIfAbsent(id, e -> new TargetManager());
	}

	@Nullable
	public static TargetingReason predicateTarget(AbstractGolemEntity<?, ?> self, LivingEntity e) {
		if (isFriend(self, e.getLastHurtMob())) return HURT;
		if (e instanceof Mob mob) {
			if (isFriend(self, mob.getTarget())) return MALICE;
		}
		if (wantsToAttack(self, e)) return PREY;
		if (isFriend(self, e.getLastHurtByMob())) return PREVIOUS;
		return null;
	}

	private static boolean isFriend(AbstractGolemEntity<?, ?> self, @Nullable Entity target) {
		if (target == null) return false;
		return target == self.getOwner() || target == self.getLeader() || target.isAlliedTo(self) || self.isAlliedTo(target);
	}

	public static boolean wantsToAttack(AbstractGolemEntity<?, ?> self, LivingEntity e) {
		var config = self.getConfigEntry(null);
		if (config == null) {
			var opt = HostileGolemRegistry.tryGetFaction(self);
			if (opt.isPresent()) return opt.get().hostileGolemAttacks(self, e);
			return DefaultFilterCard.defaultPredicate(e);
		} else {
			return config.targetFilter.aggressiveToward(e);
		}
	}

	@Nullable
	public static LivingEntity findBestTarget(AbstractGolemEntity<?, ?> self, ArrayList<TargetingStatus> list) {
		double best = -100000;
		LivingEntity ans = null;
		for (var e : list) {
			double score = e.eval(self) + self.getRandom().nextDouble();
			if (score > best) {
				best = score;
				ans = e.target();
			}
		}
		return ans;
	}

	private final LinkedHashMap<UUID, TargetEntry> map = new LinkedHashMap<>();
	private long prevTime;

	public void onSetTarget(AbstractGolemEntity<?, ?> self, LivingEntity target) {
		map.computeIfAbsent(target.getUUID(), k -> new TargetEntry()).put(self.level().getGameTime(), self);
	}

	public void tickTarget(AbstractGolemEntity<?, ?> self, LivingEntity target) {
		long time = self.level().getGameTime();
		if (time > prevTime + 5) {
			prevTime = time;
			Set<UUID> toRemove = new HashSet<>();
			for (var ent : map.entrySet()) {
				if (ent.getValue().getLastTick() < time - 5) {
					toRemove.add(ent.getKey());
				}
			}
			for (var e : toRemove) {
				map.remove(e);
			}
		}
		map.computeIfAbsent(target.getUUID(), k -> new TargetEntry()).tick(time, self);
	}

	public int getPrevCount(AbstractGolemEntity<?, ?> self, LivingEntity target) {
		return map.computeIfAbsent(target.getUUID(), k -> new TargetEntry()).get(self.level().getGameTime(), self);
	}

}
