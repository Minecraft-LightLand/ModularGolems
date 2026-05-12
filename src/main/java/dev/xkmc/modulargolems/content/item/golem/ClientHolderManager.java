package dev.xkmc.modulargolems.content.item.golem;

import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import io.netty.util.collection.IntObjectHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(value = Dist.CLIENT, modid = ModularGolems.MODID)
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
		T ans;
		var data = GolemItems.ENTITY.get(stack);
		var icon = GolemItems.DC_ICON.get(stack);
		if (data != null) {
			ans = holder.getEntityType().createForDisplay(level, data.copyTag());//FIXME get unsafe
			if (ans != null)
				ans.onCreate(GolemHolder.getMaterial(stack), GolemHolder.getUpgrades(stack), null);
		} else {
			AbstractGolemEntity<?, ?> golem = holder.getEntityType().create(level, EntitySpawnReason.LOAD);
			golem.addTag("ClientOnly");
			golem.onCreate(GolemHolder.getMaterial(stack), GolemHolder.getUpgrades(stack), null);
			GolemEquipUtil.addItemsToGolem(golem, stack, false);
			if (icon != null) {
				for (var e : icon.list()) {
					equipItemIfPossible(golem, e);
				}
			}
			ans = Wrappers.cast(golem);
		}
		if (ans == null) return null;
		ans.hurtTime = 0;
		return ans;
	}

	private static void equipItemIfPossible(Mob le, ItemStack itemStack) {
		EquipmentSlot slot = le.getEquipmentSlotForItem(itemStack);
		if (!le.isEquippableInSlot(itemStack, slot)) return;
		ItemStack current = le.getItemBySlot(slot);
		if (!current.isEmpty()) return;
		le.setItemSlot(slot, itemStack);
	}

}
