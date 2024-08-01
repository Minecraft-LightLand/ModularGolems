package dev.xkmc.modulargolems.content.item.golem;

import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import io.netty.util.collection.IntObjectHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(value = Dist.CLIENT, modid = ModularGolems.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ClientHolderManager {

	static final class TimedCache {

		@Nullable
		private final AbstractGolemEntity<?, ?> entity;

		private int life;

		TimedCache(int life, @Nullable AbstractGolemEntity<?, ?> entity) {
			this.life = life;
			this.entity = entity;
			if (entity != null) entity.addTag("ClientOnly");
		}

	}

	private static final int LIFE = 200;
	private static final IntObjectHashMap<TimedCache> CACHE = new IntObjectHashMap<>();

	@SubscribeEvent
	public static void tickEvent(ClientTickEvent.Post event) {
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
		var data = GolemItems.ENTITY.get(stack);
		var icon = GolemItems.DC_ICON.get(stack);
		if (data == null && icon == null) return null;
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
		Level level = Minecraft.getInstance().level;
		if (level == null) return null;
		T ans = null;
		var data = GolemItems.ENTITY.get(stack);
		var icon = GolemItems.DC_ICON.get(stack);
		if (data != null) {
			ans = holder.getEntityType().createForDisplay(level, data.getUnsafe());
		} else if (icon != null) {
			AbstractGolemEntity<?, ?> golem = holder.getEntityType().create(level);
			golem.onCreate(GolemHolder.getMaterial(stack), GolemHolder.getUpgrades(stack), null);
			for (var e : icon.list()) {
				golem.equipItemIfPossible(e);
			}
			ans = Wrappers.cast(golem);
		}
		if (ans == null) return null;
		ans.hurtTime = 0;
		return ans;
	}

}
