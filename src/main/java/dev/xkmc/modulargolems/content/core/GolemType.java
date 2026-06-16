package dev.xkmc.modulargolems.content.core;

import com.tterrag.registrate.util.entry.EntityEntry;
import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class GolemType<T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>> extends NamedEntry<GolemType<?, ?>> {

	private static final HashMap<ResourceLocation, GolemType<?, ?>> ENTITY_TYPE_TO_GOLEM_TYPE = new HashMap<>();
	public static final HashMap<ResourceLocation, GolemHolder<?, ?>> GOLEM_TYPE_TO_ITEM = new HashMap<>();
	public static final HashMap<ResourceLocation, Supplier<ModelProvider<?, ?>>> GOLEM_TYPE_TO_MODEL = new HashMap<>();

	public static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>> GolemType<T, P> getGolemType(EntityType<T> type) {
		return Wrappers.cast(ENTITY_TYPE_TO_GOLEM_TYPE.get(ForgeRegistries.ENTITY_TYPES.getKey(type)));
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
		synchronized (ENTITY_TYPE_TO_GOLEM_TYPE) {
			ENTITY_TYPE_TO_GOLEM_TYPE.put(type.getId(), this);
			GOLEM_TYPE_TO_MODEL.put(type.getId(), Wrappers.cast(model));
		}
	}

	public T create(Level level) {
		return Objects.requireNonNull(type.get().create(level));
	}

	public T create(Level level, CompoundTag tag) {
		return Wrappers.cast(EntityType.create(tag, level).get());
	}

	@OnlyIn(Dist.CLIENT)
	@Nullable
	public T createForDisplay(CompoundTag tag) {
		var ans = EntityType.create(tag, Proxy.getClientWorld()).orElse(null);
		if (ans == null) return null;
		T golem = Wrappers.cast(ans);
		golem.addTag("ClientOnly");
		if (tag.contains("Attributes", 9)) {
			golem.getAttributes().load(tag.getList("Attributes", 10));
		}
		if (tag.contains("Health", Tag.TAG_FLOAT)) {
			golem.setGuardedDataImpl(tag.getFloat("Health"));
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

	public boolean mayEdit(ItemStack stack) {
		return true;
	}

	public abstract MenuControl<T> menuControl(EquipmentsMenu menu, T golem);

	public abstract Supplier<Supplier<OverlayControl<T>>> overlayControl(T golem);

	public ItemStack getMenuIcon(T golem) {
		if (golem instanceof MetalGolemEntity) return GolemItems.WINDSPIRIT_CHESTPLATE.asStack();
		else if (golem instanceof DogGolemEntity) return GolemItems.DOG_ARMOR_DIAMOND.asStack();
		else return Items.DIAMOND_CHESTPLATE.getDefaultInstance();//TODO
	}
}
