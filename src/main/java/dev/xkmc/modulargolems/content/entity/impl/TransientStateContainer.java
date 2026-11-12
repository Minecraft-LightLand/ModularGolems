package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TransientStateContainer {

	@Nullable
	UUID persistentAngerTarget;

	/**
	 * Lightweight boolean flags shared by skills that need a global busy lock across golem types
	 * (for example the leap / breath skills). Not serialised, so never saved or synced.
	 */
	private final java.util.Set<String> transientFlags = ConcurrentHashMap.newKeySet();

	private final ConcurrentHashMap<String, Object> transientState = new ConcurrentHashMap<>();

	public interface View extends GolemImplBase {

		default TransientStateContainer container() {
			return asGolem().transientManager;
		}

		default boolean isTransientFlag(String key) {
			return asGolem().transientManager.transientFlags.contains(key);
		}

		default void setTransientFlag(String key, boolean on) {
			if (on) {
				container().transientFlags.add(key);
			} else {
				container().transientFlags.remove(key);
			}
		}

		@SuppressWarnings("unchecked")
		default <T> T getTransientState(String key) {
			return (T) container().transientState.get(key);
		}

		default void setTransientState(String key, @Nullable Object value) {
			if (value == null) {
				container().transientState.remove(key);
			} else {
				container().transientState.put(key, value);
			}
		}

	}

}
