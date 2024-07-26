package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record ConfigUpdateToServer(
		UUID id, int color, GolemConfigEntry entry
) implements SerialPacketBase<ConfigUpdateToServer> {

	public static ConfigUpdateToServer of(Level level, GolemConfigEntry entry) {
		entry.clientTick(level, true);
		return new ConfigUpdateToServer(entry.getID(), entry.getColor(), entry);
	}

	@Override
	public void handle(Player player) {
		if (!(player instanceof ServerPlayer sender)) return;
		var data = GolemConfigStorage.get(sender.serverLevel())
				.getOrCreateStorage(id, color, entry.init(id, color).getDisplayName());
		CompoundTag tag = new TagCodec(player.level().registryAccess()).toTag(new CompoundTag(), entry);
		assert tag != null;
		new TagCodec(player.level().registryAccess()).fromTag(tag, GolemConfigEntry.class, data);
		data.sync(sender.serverLevel());
	}

}
