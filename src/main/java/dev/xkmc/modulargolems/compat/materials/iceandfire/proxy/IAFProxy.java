package dev.xkmc.modulargolems.compat.materials.iceandfire.proxy;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public interface IAFProxy {

	class Provider {

		private static IAFProxy CACHE;

		private static IAFProxy get() {
			try {
				CACHE = new IAFProxyCE();
				CACHE.ingotLightningSteel();
				return CACHE;
			} catch (Throwable ignore) {
			}
			throw new IllegalStateException("No valid IaF target");
		}

	}

	static IAFProxy get() {
		return Provider.get();
	}

	String modid();

	Supplier<Item> ingotIceSteel();

	Supplier<Item> ingotFireSteel();

	Supplier<Item> ingotLightningSteel();

	void fireHit(LivingEntity target, LivingEntity user, int level);

	void iceHit(LivingEntity target, LivingEntity user, int level);

	void lightningHit(LivingEntity target, LivingEntity user, int level);

}
