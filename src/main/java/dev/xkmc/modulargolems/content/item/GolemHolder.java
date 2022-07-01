package dev.xkmc.modulargolems.content.item;

import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2library.serial.NBTObj;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GolemHolder<T extends AbstractGolemEntity<T>> extends Item {

	private static final String KEY_MATERIAL = "golem_materials";
	private static final String KEY_ENTITY = "golem_entity";

	private final RegistryEntry<GolemType<T>> type;

	public GolemHolder(Properties props, RegistryEntry<GolemType<T>> type) {
		super(props);
		this.type = type;
		GolemType.GOLEM_TYPE_TO_ITEM.put(type.getId(), this);
	}

	public static ArrayList<GolemMaterial> getMaterial(ItemStack stack) {
		ArrayList<GolemMaterial> ans = new ArrayList<>();
		CompoundTag tag = stack.getTag();
		Optional.ofNullable(stack.getTag())
				.map(e -> e.contains(KEY_MATERIAL) ? e.getCompound(KEY_MATERIAL) : null)
				.ifPresent(e -> e.getAllKeys().forEach(k -> {
					Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(k));
					if (item instanceof GolemPart part) {
						ans.add(part.parseMaterial(new ResourceLocation(e.getString(k))));
					}
				}));
		return ans;
	}

	public static ItemStack addMaterial(ItemStack stack, GolemPart item, ResourceLocation material) {
		NBTObj obj = new NBTObj(stack.getOrCreateTag());
		ResourceLocation rl = ForgeRegistries.ITEMS.getKey(item);
		assert rl != null;
		obj.getSub(KEY_MATERIAL).tag.put(rl.toString(), StringTag.valueOf(material.toString()));
		return stack;
	}

	public static <T extends AbstractGolemEntity<T>> ItemStack setEntity(T entity) {
		GolemHolder<T> holder = GolemType.getGolemHolder(entity.getType());
		ItemStack stack = new ItemStack(holder);
		NBTObj obj = new NBTObj(stack.getOrCreateTag()).getSub(KEY_MATERIAL);
		for (GolemMaterial mat : entity.getMaterials()) {
			ResourceLocation rl = ForgeRegistries.ITEMS.getKey(mat.part());
			assert rl != null;
			obj.tag.put(rl.toString(), StringTag.valueOf(mat.id().toString()));
		}
		return stack;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		List<GolemMaterial> mats = getMaterial(stack);
		mats.forEach(e -> list.add(e.getDesc()));
		GolemMaterial.collectAttributes(mats).forEach((k, v) -> list.add(k.getAdderTooltip(v)));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		CompoundTag root = stack.getTag();
		if (root == null) {
			return InteractionResult.PASS;
		}
		Level level = context.getLevel();
		if (root.contains(KEY_ENTITY)) {
			if (!level.isClientSide()) {
				AbstractGolemEntity<?> golem = type.get().create((ServerLevel) level, root.getCompound(KEY_ENTITY));
				level.addFreshEntity(golem);
			}
			return InteractionResult.SUCCESS;
		}
		if (root.contains(KEY_MATERIAL)) {
			if (!level.isClientSide()) {
				AbstractGolemEntity<?> golem = type.get().create((ServerLevel) level);
				Player player = context.getPlayer();
				UUID id = player == null ? null : player.getUUID();
				golem.onCreate(getMaterial(stack), id);
				level.addFreshEntity(golem);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
}
