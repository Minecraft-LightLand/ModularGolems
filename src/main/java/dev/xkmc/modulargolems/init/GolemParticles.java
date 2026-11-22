package dev.xkmc.modulargolems.init;

import com.mojang.serialization.MapCodec;
import dev.xkmc.modulargolems.content.client.particle.GolemAfterimageOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GolemParticles {

	public static final DeferredRegister<ParticleType<?>> PARTICLES =
			DeferredRegister.create(Registries.PARTICLE_TYPE, ModularGolems.MODID);

	public static final DeferredHolder<ParticleType<?>, ParticleType<GolemAfterimageOptions>> AFTERIMAGE =
			PARTICLES.register("golem_afterimage", () -> new ParticleType<GolemAfterimageOptions>(false) {
				@Override
				public MapCodec<GolemAfterimageOptions> codec() {
					return GolemAfterimageOptions.CODEC;
				}

				@Override
				public StreamCodec<? super RegistryFriendlyByteBuf, GolemAfterimageOptions> streamCodec() {
					return GolemAfterimageOptions.STREAM_CODEC;
				}
			});

	public static void register(IEventBus bus) {
		PARTICLES.register(bus);
	}

	public static void spawnAfterimage(LivingEntity e) {
		if (e.level() instanceof ServerLevel sl) {
			sl.sendParticles(new GolemAfterimageOptions(e.getId(), 255, 255, 255, 5),
					e.getX(), e.getY(), e.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
		}
	}

}
