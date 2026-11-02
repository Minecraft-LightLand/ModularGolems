package dev.xkmc.modulargolems.content.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public record GolemAfterimageOptions(int entityId, int r, int g, int b, int lifeTicks) implements ParticleOptions {

	public static final Codec<GolemAfterimageOptions> CODEC = RecordCodecBuilder.create(ins ->
			ins.group(
					Codec.INT.fieldOf("entity_id").forGetter(GolemAfterimageOptions::entityId),
					Codec.INT.fieldOf("r").forGetter(GolemAfterimageOptions::r),
					Codec.INT.fieldOf("g").forGetter(GolemAfterimageOptions::g),
					Codec.INT.fieldOf("b").forGetter(GolemAfterimageOptions::b),
					Codec.INT.fieldOf("life_ticks").forGetter(GolemAfterimageOptions::lifeTicks)
			).apply(ins, GolemAfterimageOptions::new));

	public static final ParticleOptions.Deserializer<GolemAfterimageOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {

		@Override
		public @NotNull GolemAfterimageOptions fromCommand(@NotNull ParticleType<GolemAfterimageOptions> type, @NotNull StringReader reader) throws CommandSyntaxException {
			reader.expect(' ');
			int entityId = reader.readInt();
			reader.expect(' ');
			int r = reader.readInt();
			reader.expect(' ');
			int g = reader.readInt();
			reader.expect(' ');
			int b = reader.readInt();
			reader.expect(' ');
			int lifeTicks = reader.readInt();
			return new GolemAfterimageOptions(entityId, r, g, b, lifeTicks);
		}

		@Override
		public @NotNull GolemAfterimageOptions fromNetwork(@NotNull ParticleType<GolemAfterimageOptions> type, @NotNull FriendlyByteBuf buf) {
			return new GolemAfterimageOptions(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
		}
	};

	@Override
	public void writeToNetwork(@NotNull FriendlyByteBuf buf) {
		buf.writeInt(entityId);
		buf.writeInt(r);
		buf.writeInt(g);
		buf.writeInt(b);
		buf.writeInt(lifeTicks);
	}

	@Override
	public @NotNull String writeToString() {
		return BuiltInRegistries.PARTICLE_TYPE.getKey(getType()) + " " + entityId + " " + r + " " + g + " " + b + " " + lifeTicks;
	}

	@Override
	public @NotNull ParticleType<GolemAfterimageOptions> getType() {
		return dev.xkmc.modulargolems.init.GolemParticles.AFTERIMAGE.get();
	}

}
