package dev.xkmc.modulargolems.content.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record GolemAfterimageOptions(int entityId, int r, int g, int b, int lifeTicks) implements ParticleOptions {

	public static final MapCodec<GolemAfterimageOptions> CODEC = RecordCodecBuilder.mapCodec(ins ->
			ins.group(
					Codec.INT.fieldOf("entity_id").forGetter(GolemAfterimageOptions::entityId),
					Codec.INT.fieldOf("r").forGetter(GolemAfterimageOptions::r),
					Codec.INT.fieldOf("g").forGetter(GolemAfterimageOptions::g),
					Codec.INT.fieldOf("b").forGetter(GolemAfterimageOptions::b),
					Codec.INT.fieldOf("life_ticks").forGetter(GolemAfterimageOptions::lifeTicks)
			).apply(ins, GolemAfterimageOptions::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, GolemAfterimageOptions> STREAM_CODEC = StreamCodec.of(
			(buf, o) -> {
				buf.writeInt(o.entityId);
				buf.writeInt(o.r);
				buf.writeInt(o.g);
				buf.writeInt(o.b);
				buf.writeInt(o.lifeTicks);
			},
			buf -> new GolemAfterimageOptions(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt()));

	@Override
	public ParticleType<GolemAfterimageOptions> getType() {
		return (ParticleType<GolemAfterimageOptions>) (ParticleType<?>) dev.xkmc.modulargolems.init.GolemParticles.AFTERIMAGE.get();
	}

}
