package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

@SerialClass
public class ConfigUpdateToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public UUID id;
	@SerialClass.SerialField
	public int color;
	@SerialClass.SerialField
	public GolemConfigEntry entry;

	@Deprecated
	public ConfigUpdateToServer() {

	}

	public ConfigUpdateToServer(Level level, GolemConfigEntry entry) {
		this.entry = entry;
		this.id = entry.getID();
		this.color = entry.getColor();
		entry.clientTick(level, true);
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var sender = context.getSender();
		if (sender == null) return;
		var data = GolemConfigStorage.get(sender.serverLevel())
				.getOrCreateStorage(id, color, entry.init(id, color).getDisplayName());
		CompoundTag tag = TagCodec.toTag(new CompoundTag(), entry);
		assert tag != null;
		TagCodec.fromTag(tag, GolemConfigEntry.class, data, e -> true);
		data.sync(sender.serverLevel());
	}

}
