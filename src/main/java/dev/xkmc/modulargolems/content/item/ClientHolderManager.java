package dev.xkmc.modulargolems.content.item;

import dev.xkmc.l2library.util.code.Wrappers;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import io.netty.util.collection.IntObjectHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

public class ClientHolderManager {

	static final class TimedCache {

		@Nullable
		private final AbstractGolemEntity<?, ?> entity;

		private int life;

		TimedCache(int life, @Nullable AbstractGolemEntity<?, ?> entity) {
			this.life = life;
			this.entity = entity;
		}

	}

	private static final int LIFE = 200;
	private static final IntObjectHashMap<TimedCache> CACHE = new IntObjectHashMap<>();

	@SubscribeEvent
	public static void tickEvent(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			return;
		}
		if (CACHE.size() > 100) {
			ModularGolems.LOGGER.error("Golem cache overflow. Clearing...");
			CACHE.clear();
		} else {
			CACHE.entrySet().removeIf(e -> e.getValue().life-- <= 0);
		}
	}

	@Nullable
	public static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	T getEntityForDisplay(GolemHolder<T, P> holder, ItemStack stack) {
		CompoundTag root = stack.getTag();
		if (root == null) return null;
		if (!root.contains(GolemHolder.KEY_ENTITY)) return null;
		int hash = stack.hashCode();
		if (CACHE.containsKey(hash)) {
			AbstractGolemEntity<?, ?> ans = CACHE.get(stack.hashCode()).entity;
			return ans == null ? null : Wrappers.cast(ans);
		}
		T golem = getEntityForDisplayInternal(holder, stack);
		TimedCache cache = new TimedCache(LIFE, golem);
		CACHE.put(hash, cache);
		return golem;
	}

	@Nullable
	private static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>>
	T getEntityForDisplayInternal(GolemHolder<T, P> holder, ItemStack stack) {
		CompoundTag root = stack.getTag();
		if (root == null) return null;
		if (!root.contains(GolemHolder.KEY_ENTITY)) return null;
		CompoundTag entity = root.getCompound(GolemHolder.KEY_ENTITY);
		T ans = holder.getEntityType().createForDisplay(entity);
		if (ans == null) return null;
		ans.hurtTime = 0;
		return ans;
	}

}
