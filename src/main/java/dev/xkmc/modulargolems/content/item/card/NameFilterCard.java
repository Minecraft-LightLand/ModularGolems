package dev.xkmc.modulargolems.content.item.card;

import com.mojang.datafixers.util.Either;
import dev.xkmc.l2library.util.nbt.ItemCompoundTag;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class NameFilterCard extends TargetFilterCard {

	private static final String KEY = "filterList";

	private static List<String> getStrings(ItemStack stack) {
		List<String> ans = new ArrayList<>();
		var tag = stack.getTag();
		if (tag == null || !tag.contains(KEY)) {
			return ans;
		}
		for (var e : tag.getList(KEY, Tag.TAG_STRING)) {
			ans.add(e.getAsString());
		}
		return ans;
	}

	private static List<Either<EntityType<?>, TagKey<EntityType<?>>>> getList(List<String> strs) {
		List<Either<EntityType<?>, TagKey<EntityType<?>>>> ans = new ArrayList<>();
		for (var str : strs) {
			if (str.startsWith("#")) {
				ResourceLocation rl = getRL(str.substring(1));
				if (rl == null) continue;
				TagKey<EntityType<?>> key = TagKey.create(Registries.ENTITY_TYPE, rl);
				var manager = ForgeRegistries.ENTITY_TYPES.tags();
				if (manager != null && manager.isKnownTagName(key)) {
					ans.add(Either.right(key));
				}
			} else {
				ResourceLocation rl = getRL(str);
				if (rl == null) continue;
				var type = ForgeRegistries.ENTITY_TYPES.getValue(rl);
				if (type != null) {
					ans.add(Either.left(type));
				}
			}
		}
		return ans;
	}

	@Nullable
	private static ResourceLocation getRL(String str) {
		try {
			return new ResourceLocation(str);
		} catch (Exception e) {
			return null;
		}
	}

	public static void setList(ItemStack stack, List<Either<EntityType<?>, TagKey<EntityType<?>>>> list) {
		var tag = ItemCompoundTag.of(stack).getSubList(KEY, Tag.TAG_STRING).getOrCreate();
		for (var e : list) {
			e.map(l -> Optional.ofNullable(ForgeRegistries.ENTITY_TYPES.getKey(l)).map(ResourceLocation::toString),
					r -> Optional.of("#" + r.location())).map(StringTag::valueOf).ifPresent(tag::add);
		}
	}

	public NameFilterCard(Properties properties) {
		super(properties);
	}

	@Override
	public Predicate<LivingEntity> mayTarget(ItemStack stack) {
		var list = getList(getStrings(stack));
		return e -> {
			for (var x : list) {
				if (x.map(l -> e.getType() == l, r -> e.getType().is(r))) {
					return true;
				}
			}
			return false;
		};
	}

	@Override
	protected InteractionResultHolder<ItemStack> removeLast(ItemStack stack) {
		var list = getList(getStrings(stack));
		if (list.size() == 0) return InteractionResultHolder.fail(stack);
		list.remove(list.size() - 1);
		setList(stack, list);
		return InteractionResultHolder.success(stack);
	}

	@Override
	protected InteractionResultHolder<ItemStack> onUse(ItemStack stack) {
		var strs = getStrings(stack);
		String name = stack.getHoverName().getString();
		if (strs.contains(name)) {
			return InteractionResultHolder.success(stack);
		}
		strs.add(name);
		setList(stack, getList(strs));
		stack.setHoverName(null);
		return InteractionResultHolder.success(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		var strs = getStrings(stack);
		if (strs.size() > 0 && !Screen.hasShiftDown()) {
			for (var e : strs) {
				list.add(Component.literal(e));
			}
			list.add(MGLangData.TARGET_SHIFT.get());
		} else {
			list.add(MGLangData.TARGET_NAME.get());
			list.add(MGLangData.TARGET_REMOVE.get());
		}
	}

}
