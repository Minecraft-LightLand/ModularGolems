package dev.xkmc.modulargolems.content.item;

import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.GolemType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class GolemPart extends Item {

	private static final String KEY = "golem_material";

	private final Supplier<GolemType<?>> type;

	public final int count;
	public GolemPart(Properties props, Supplier<GolemType<?>> type, int count) {
		super(props);
		this.type = type;
		this.count = count;
	}

	public static Optional<ResourceLocation> getMaterial(ItemStack stack) {
		return Optional.ofNullable(stack.getTag())
				.map(e -> e.contains(KEY) ? new ResourceLocation(e.getString(KEY)) : null);
	}

	public static ItemStack setMaterial(ItemStack stack, ResourceLocation material) {
		stack.getOrCreateTag().putString(KEY, material.toString());
		return stack;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		getMaterial(stack).ifPresent(e -> {
			list.add(GolemMaterial.getDesc(e));
			GolemMaterialConfig.get().stats.get(e.toString()).forEach((k, v) -> list.add(k.getAdderTooltip(v)));
			GolemMaterialConfig.get().modifiers.get(e.toString()).forEach((m, v) -> list.add(m.getTooltip(v)));
		});
	}

}
