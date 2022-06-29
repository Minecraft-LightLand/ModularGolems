package dev.xkmc.modulargolems.init.registrate;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.l2library.serial.handler.RLClassHandler;
import net.minecraft.core.Registry;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;

import java.util.function.Supplier;

public class GolemRegistrate extends L2Registrate {

	public GolemRegistrate(String modid) {
		super(modid);
	}

	public <E extends NamedEntry<E>> L2Registrate.RegistryInstance<E> newDatapackRegistry(String id, Class<E> cls, Supplier<E> sup) {
		ResourceKey<Registry<E>> key = this.makeRegistry(id, () ->
				new RegistryBuilder<E>().dataPackRegistry(Codec.unit(sup))
						.onCreate((r, s) -> new RLClassHandler<StringTag, E>(cls, () -> r)));
		return new RegistryInstance<>(Suppliers.memoize(() -> RegistryManager.ACTIVE.getRegistry(key)), key);
	}

}
