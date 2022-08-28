package dev.xkmc.modulargolems.content.item;

import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2library.util.nbt.ItemCompoundTag;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.upgrades.UpgradeItem;
import dev.xkmc.modulargolems.init.data.LangData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class GolemHolder<T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>> extends Item {

	public static final String KEY_MATERIAL = "golem_materials",
			KEY_UPGRADES = "golem_upgrades",
			KEY_ENTITY = "golem_entity",
			KEY_DISPLAY = "golem_display";
	public static final String KEY_PART = "part", KEY_MAT = "material";

	public static ArrayList<GolemMaterial> getMaterial(ItemStack stack) {
		ArrayList<GolemMaterial> ans = new ArrayList<>();
		CompoundTag tag = stack.getTag();
		if (tag != null && tag.contains(KEY_MATERIAL, Tag.TAG_LIST)) {
			ListTag list = tag.getList(KEY_MATERIAL, Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				CompoundTag elem = list.getCompound(i);
				GolemPart<?, ?> part = (GolemPart<?, ?>) ForgeRegistries.ITEMS.getValue(new ResourceLocation(elem.getString(KEY_PART)));
				ResourceLocation mat = new ResourceLocation(elem.getString(KEY_MAT));
				if (part != null) {
					ans.add(part.parseMaterial(mat));
				}
			}
		}
		return ans;
	}

	public static ArrayList<UpgradeItem> getUpgrades(ItemStack stack) {
		ArrayList<UpgradeItem> ans = new ArrayList<>();
		CompoundTag tag = stack.getTag();
		if (tag != null && tag.contains(KEY_UPGRADES, Tag.TAG_LIST)) {
			ListTag list = tag.getList(KEY_UPGRADES, Tag.TAG_STRING);
			for (int i = 0; i < list.size(); i++) {
				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(list.getString(i)));
				if (item instanceof UpgradeItem up) {
					ans.add(up);
				}
			}
		}
		return ans;
	}

	public static void addMaterial(ItemStack stack, GolemPart<?, ?> item, ResourceLocation material) {
		ResourceLocation rl = ForgeRegistries.ITEMS.getKey(item);
		assert rl != null;
		ItemCompoundTag tag = ItemCompoundTag.of(stack);
		CompoundTag elem = tag.getSubList(KEY_MATERIAL, Tag.TAG_COMPOUND).addCompound().getOrCreate();
		elem.put(KEY_PART, StringTag.valueOf(rl.toString()));
		elem.put(KEY_MAT, StringTag.valueOf(material.toString()));
	}

	public static ItemStack addUpgrade(ItemStack stack, UpgradeItem item) {
		ResourceLocation rl = ForgeRegistries.ITEMS.getKey(item);
		assert rl != null;
		ItemCompoundTag tag = ItemCompoundTag.of(stack);
		tag.getSubList(KEY_UPGRADES, Tag.TAG_STRING).getOrCreate().add(StringTag.valueOf(rl.toString()));
		return stack;
	}

	public static <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>> ItemStack setEntity(T entity) {
		GolemHolder<T, P> holder = GolemType.getGolemHolder(entity.getType());
		ItemStack stack = new ItemStack(holder);
		ItemCompoundTag tag = ItemCompoundTag.of(stack);
		var matlist = tag.getSubList(KEY_MATERIAL, Tag.TAG_COMPOUND);
		for (GolemMaterial mat : entity.getMaterials()) {
			ResourceLocation rl = ForgeRegistries.ITEMS.getKey(mat.part());
			assert rl != null;
			var elem = matlist.addCompound().getOrCreate();
			elem.put(KEY_PART, StringTag.valueOf(rl.toString()));
			elem.put(KEY_MAT, StringTag.valueOf(mat.id().toString()));
		}
		var uplist = tag.getSubList(KEY_UPGRADES, Tag.TAG_STRING).getOrCreate();
		for (Item item : entity.getUpgrades()) {
			ResourceLocation rl = ForgeRegistries.ITEMS.getKey(item);
			assert rl != null;
			uplist.add(StringTag.valueOf(rl.toString()));
		}
		entity.save(tag.getSubTag(KEY_ENTITY).getOrCreate());
		return stack;
	}

	public static float getHealth(ItemStack stack) {
		return Optional.ofNullable(stack.getTag())
				.filter(e -> e.contains(KEY_ENTITY))
				.map(e -> e.getCompound(KEY_ENTITY))
				.map(e -> e.getFloat("Health")).orElse(-1f);
	}

	public static float getMaxHealth(ItemStack stack) {
		return Optional.ofNullable(stack.getTag())
				.filter(e -> e.contains(KEY_ENTITY))
				.map(e -> e.getCompound(KEY_ENTITY))
				.flatMap(e -> e.getList("Attributes", Tag.TAG_COMPOUND).stream()
						.map(t -> ((CompoundTag) t))
						.filter(t -> t.getString("Name").equals("minecraft:generic.max_health"))
						.findAny()).map(e -> e.getFloat("Base")).orElse(-1f);
	}

	public static void setHealth(ItemStack result, float health) {
		result.getOrCreateTag().getCompound(KEY_ENTITY).putFloat("Health", health);
	}

	private final RegistryEntry<GolemType<T, P>> type;

	public GolemHolder(Properties props, RegistryEntry<GolemType<T, P>> type) {
		super(props.stacksTo(1));
		this.type = type;
		GolemType.GOLEM_TYPE_TO_ITEM.put(type.getId(), this);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		float max = getMaxHealth(stack);
		if (max >= 0) {
			float health = getHealth(stack);
			float f = Mth.clamp(health / max, 0f, 1f);
			int color = Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
			MutableComponent hc = Component.literal("" + Math.round(health)).setStyle(Style.EMPTY.withColor(color));
			list.add(LangData.HEALTH.get(hc, Math.round(max)).withStyle(health <= 0 ? ChatFormatting.RED : ChatFormatting.AQUA));
		}
		var mats = getMaterial(stack);
		var upgrades = getUpgrades(stack);
		var parts = getEntityType().values();
		if (mats.size() == parts.length) {
			for (int i = 0; i < parts.length; i++) {
				list.add(parts[i].getDesc(mats.get(i).getDesc()));
			}
		}
		list.add(LangData.SLOT.get(getRemaining(mats, upgrades)).withStyle(ChatFormatting.AQUA));
		GolemMaterial.collectModifiers(mats, upgrades).forEach((k, v) -> list.add(k.getTooltip(v)));
		GolemMaterial.collectAttributes(mats, upgrades).forEach((k, v) -> list.add(k.getTotalTooltip(v)));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		CompoundTag root = stack.getTag();
		if (root == null) {
			return InteractionResult.PASS;
		}

		Level level = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		Direction direction = context.getClickedFace();
		BlockState blockstate = level.getBlockState(blockpos);
		BlockPos spawnPos;
		if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
			spawnPos = blockpos;
		} else {
			spawnPos = blockpos.relative(direction);
		}
		Vec3 pos = new Vec3(spawnPos.getX() + 0.5, spawnPos.getY() + 0.05, spawnPos.getZ() + 0.5);

		if (root.contains(KEY_ENTITY)) {
			if (getHealth(stack) <= 0)
				return InteractionResult.FAIL;
			if (!level.isClientSide()) {
				AbstractGolemEntity<?, ?> golem = type.get().create((ServerLevel) level, root.getCompound(KEY_ENTITY));
				Player player = context.getPlayer();
				UUID id = player == null ? null : player.getUUID();
				golem.updateAttributes(getMaterial(stack), getUpgrades(stack), id);
				golem.moveTo(pos);
				level.addFreshEntity(golem);
				stack.shrink(1);
				stack.removeTagKey(KEY_ENTITY);
			}
			return InteractionResult.CONSUME;
		}
		if (root.contains(KEY_MATERIAL)) {
			if (!level.isClientSide()) {
				AbstractGolemEntity<?, ?> golem = type.get().create((ServerLevel) level);
				golem.moveTo(pos);
				Player player = context.getPlayer();
				UUID id = player == null ? null : player.getUUID();
				golem.onCreate(getMaterial(stack), getUpgrades(stack), id);
				level.addFreshEntity(golem);
				if (player == null || !player.getAbilities().instabuild) {
					stack.shrink(1);
				}
			}
			return InteractionResult.CONSUME;
		}
		return InteractionResult.PASS;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		if (stack.getTag() != null && stack.getTag().contains(KEY_DISPLAY))
			return true;
		return getMaxHealth(stack) >= 0;
	}

	@Override
	public int getBarColor(ItemStack stack) {
		float f;
		if (stack.getTag() != null && stack.getTag().contains(KEY_DISPLAY)) {
			f = stack.getTag().getFloat(KEY_DISPLAY);
		} else {
			float health = getHealth(stack);
			float maxHealth = getMaxHealth(stack);
			f = Mth.clamp(health / maxHealth, 0f, 1f);
		}
		return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		if (stack.getTag() != null && stack.getTag().contains(KEY_DISPLAY)) {
			float f = stack.getTag().getFloat(KEY_DISPLAY);
			return Math.round(f * 13.0F);
		}
		return Math.round(Mth.clamp(getHealth(stack) / getMaxHealth(stack), 0, 1) * 13.0F);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(GolemBEWLR.EXTENSIONS);
	}

	public GolemType<T, P> getEntityType() {
		return type.get();
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
		if (this.allowedIn(tab)) {
			for (ResourceLocation rl : GolemMaterialConfig.get().getAllMaterials()) {
				ItemStack stack = new ItemStack(this);
				for (P part : getEntityType().values()) {
					addMaterial(stack, part.toItem(), rl);
				}
				list.add(stack);
			}
		}
	}

	public int getRemaining(ArrayList<GolemMaterial> mats, ArrayList<UpgradeItem> upgrades) {
		int base = getEntityType().values().length - upgrades.size();
		var modifiers = GolemMaterial.collectModifiers(mats, upgrades);
		for (var ent : modifiers.entrySet()) {
			base += ent.getKey().addSlot() * ent.getValue();
		}
		return base;
	}
}
