package dev.xkmc.modulargolems.content.item.card;

import com.mojang.datafixers.util.Either;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class NameFilterCard extends TargetFilterCard {

	private static List<String> getStrings(ItemStack stack) {
		return GolemItems.DC_FILTER_NAME.getOrDefault(stack, List.of());
	}

	private static List<Either<EntityType<?>, TagKey<EntityType<?>>>> getList(List<String> strs) {
		List<Either<EntityType<?>, TagKey<EntityType<?>>>> ans = new ArrayList<>();
		for (var s : strs) {
			String str = s.trim();
			if (str.startsWith("#")) {
				Identifier rl = Identifier.tryParse(str.substring(1));
				if (rl == null) continue;
				TagKey<EntityType<?>> key = TagKey.create(Registries.ENTITY_TYPE, rl);
				var ref = BuiltInRegistries.ENTITY_TYPE.get(key);
				if (ref.isPresent()) {
					ans.add(Either.right(key));
				}
			} else {
				Identifier rl = Identifier.tryParse(str);
				if (rl == null) continue;
				if (!BuiltInRegistries.ENTITY_TYPE.containsKey(rl)) continue;
				var type = BuiltInRegistries.ENTITY_TYPE.get(rl);
				if (type.isEmpty()) continue;
				ans.add(Either.left(type.get().value()));
			}
		}
		return ans;
	}

	public static void setList(ItemStack stack, List<Either<EntityType<?>, TagKey<EntityType<?>>>> list) {
		GolemItems.DC_FILTER_NAME.set(stack, list.stream().map(e -> e.map(
				l -> BuiltInRegistries.ENTITY_TYPE.getKey(l).toString(),
				r -> "#" + r.location()
		)).toList());
	}

	public NameFilterCard(Properties properties) {
		super(properties);
	}

	public static ItemStackTemplate getFriendly() {
		var ans = List.of("#" + MGTagGen.GOLEM_FRIENDLY.location());
		var patch = DataComponentPatch.builder()
				.set(GolemItems.DC_FILTER_NAME.get(), ans)
				.build();
		return new ItemStackTemplate(GolemItems.CARD_NAME.get(), patch);
	}

	@Override
	public Predicate<LivingEntity> mayTarget(ItemStack stack) {
		var list = getList(getStrings(stack));
		return e -> {
			for (var x : list) {
				if (x.map(l -> e.getType() == l, r -> e.getType().builtInRegistryHolder().is(r))) {
					return true;
				}
			}
			return false;
		};
	}

	@Override
	protected InteractionResult removeLast(Player player, ItemStack stack) {
		var list = new ArrayList<>(getStrings(stack));
		if (list.isEmpty()) {
			return InteractionResult.FAIL;
		}
		if (!player.level().isClientSide()) {
			String e = list.removeLast();
			setList(stack, getList(list));
			player.sendSystemMessage(MGLangData.TARGET_MSG_REMOVED.get(Component.literal(e)));
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected InteractionResult onUse(Player player, ItemStack stack) {
		var strs = new ArrayList<>(getStrings(stack));
		String name = stack.getHoverName().getString();
		if (strs.contains(name)) {
			return InteractionResult.SUCCESS;
		}
		if (!player.level().isClientSide()) {
			strs.add(name);
			setList(stack, getList(strs));
			stack.remove(DataComponents.CUSTOM_NAME);
			player.sendSystemMessage(MGLangData.TARGET_MSG_ADDED.get(Component.literal(name)));
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, TooltipDisplay display, Consumer<Component> list, TooltipFlag flag) {
		var strs = getStrings(stack);
		if (!strs.isEmpty() && !flag.hasShiftDown()) {
			for (var e : strs) {
				list.accept(Component.literal(e));
			}
			list.accept(MGLangData.TARGET_SHIFT.get());
		} else {
			list.accept(MGLangData.TARGET_NAME.get());
			list.accept(MGLangData.TARGET_REMOVE.get());
		}
	}

}
