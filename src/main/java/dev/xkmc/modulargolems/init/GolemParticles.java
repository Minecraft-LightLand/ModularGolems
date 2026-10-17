package dev.xkmc.modulargolems.init;

import com.mojang.serialization.Codec;
import dev.xkmc.modulargolems.content.client.particle.GolemAfterimageOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GolemParticles {

	public static final DeferredRegister<ParticleType<?>> PARTICLES =
			DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ModularGolems.MODID);

	public static final RegistryObject<ParticleType<GolemAfterimageOptions>> AFTERIMAGE =
			PARTICLES.register("golem_afterimage", () -> new ParticleType<>(false, GolemAfterimageOptions.DESERIALIZER) {
				@Override
				public Codec<GolemAfterimageOptions> codec() {
					return GolemAfterimageOptions.CODEC;
				}
			});

	public static void register() {
		PARTICLES.register(ModularGolems.MOD_BUS);
	}

	public static void spawnAfterimage(LivingEntity e) {
		if (e.level() instanceof ServerLevel sl) {
			sl.sendParticles(new GolemAfterimageOptions(e.getId(), 255, 255, 255, 5),
					e.getX(), e.getY(), e.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
		}
	}

}
