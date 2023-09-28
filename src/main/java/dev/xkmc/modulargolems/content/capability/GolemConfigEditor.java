package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.modulargolems.content.entity.mode.GolemMode;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.UUID;

public interface GolemConfigEditor {

	Level level();

	GolemConfigEntry entry();

	default Component getDisplayName() {
		return entry().getDisplayName();
	}

	default GolemMode getDefaultMode() {
		return GolemModes.get(entry().defaultMode);
	}

	default void setDefaultMode(GolemMode mode) {
		entry().defaultMode = mode.getID();
		entry().sync(level());
	}

	default boolean summonToPosition() {
		return entry().summonToPosition;
	}

	default void setSummonToPosition(boolean bool) {
		entry().summonToPosition = bool;
		entry().sync(level());
	}

	record Writable(Level level, GolemConfigEntry entry) implements GolemConfigEditor {

	}

	record Readable(Level level, UUID id, int color) implements GolemConfigEditor {

		@Override
		public GolemConfigEntry entry() {
			return GolemConfigStorage.get(level).getOrCreateStorage(id, color, MGLangData.LOADING.get());
		}

	}

}
