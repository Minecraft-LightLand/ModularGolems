package dev.xkmc.modulargolems.content.core;

import com.tterrag.registrate.util.entry.EntityEntry;
import dev.xkmc.l2core.init.reg.registrate.NamedEntry;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;

public class GolemType<T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>> extends NamedEntry<GolemType<?, ?>> {

	private static final HashMap<Identifier, GolemType<?, ?>> ENTITY_TYPE_TO_GOLEM_TYPE = new HashMap<>();
	public static final HashMap<Identifier, GolemHolder<?, ?>> GOLEM_TYPE_TO_ITEM = new HashMap<>();
	public static final HashMap<Identifier, Supplier<ModelProvider<?, ?>>> GOLEM_TYPE_TO_MODEL = new HashMap<>();

	public static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>> GolemType<T, P> getGolemType(EntityType<T> type) {
		return Wrappers.cast(ENTITY_TYPE_TO_GOLEM_TYPE.get(BuiltInRegistries.ENTITY_TYPE.getKey(type)));
	}

	public static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>> GolemHolder<T, P> getGolemHolder(GolemType<T, ?> type) {
		return Wrappers.cast(GOLEM_TYPE_TO_ITEM.get(type.getRegistryName()));
	}

	public static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>> GolemHolder<T, P> getGolemHolder(EntityType<T> type) {
		return getGolemHolder(getGolemType(type));
	}

	private final EntityEntry<T> type;
	private final Supplier<P[]> list;
	private final P body;

	public GolemType(EntityEntry<T> type, Supplier<P[]> list, P body, Supplier<ModelProvider<T, P>> model) {
		super(GolemTypes.TYPES);
		this.type = type;
		this.list = list;
		this.body = body;
		ENTITY_TYPE_TO_GOLEM_TYPE.put(type.getId(), this);
		GOLEM_TYPE_TO_MODEL.put(type.getId(), Wrappers.cast(model));
	}

	public T create(Level level, EntitySpawnReason reason) {
		return Objects.requireNonNull(type.get().create(level, reason));
	}

	@Nullable
	public T create(Level level, CompoundTag tag, EntitySpawnReason reason) {
		var in = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), tag);
		var ans = EntityType.create(in, level, reason);
		return ans.<T>map(Wrappers::cast).orElse(null);
	}

	@Nullable
	public T createForDisplay(Level level, CompoundTag tag) {
		var ans = create(level, tag, EntitySpawnReason.LOAD);
		if (ans == null) return null;
		T golem = Wrappers.cast(ans);
		golem.addTag("ClientOnly");
		if (tag.contains("attributes", 9)) {
			golem.getAttributes().load(tag.getList("attributes", 10));
		}
		if (tag.contains("Health", Tag.TAG_FLOAT)) {
			golem.setHealth(tag.getFloat("Health"));
		}
		golem.yHeadRot = 0;
		golem.yHeadRotO = 0;
		golem.yBodyRot = 0;
		golem.yBodyRotO = 0;
		golem.xRotO = 0;
		golem.setXRot(0);
		return golem;
	}

	public EntityType<?> type() {
		return type.get();
	}

	public P[] values() {
		return list.get();
	}

	public P getBodyPart() {
		return body;
	}
}
