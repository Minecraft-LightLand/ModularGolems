package dev.xkmc.modulargolems.content.item;

import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.GolemMaterial;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AbstractGolemParts extends Item {

	private static final String KEY = "golem_material";

	private final GolemType<?> type;

	public AbstractGolemParts(Properties props, GolemType<?> type) {
		super(props);
		this.type = type;
	}

	public static Optional<GolemMaterial> getMaterial(ItemStack stack) {
		return Optional.ofNullable(stack.getTag())
				.map(e -> e.contains(KEY) ? e.getString(KEY) : null)
				.map(e -> GolemTypeRegistry.MATERIALS.get().getValue(new ResourceLocation(e)));
	}

	public static ItemStack setMaterial(ItemStack stack, GolemMaterial material) {
		stack.getOrCreateTag().putString(KEY, material.getID());
		return stack;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		getMaterial(stack).ifPresent(e -> {
			list.add(e.getDesc());
			GolemMaterialConfig.get().stats.get(e.getID()).forEach((k, v) -> list.add(k.getAdderTooltip(v)));
			GolemMaterialConfig.get().modifiers.get(e.getID()).forEach(m -> list.add(m.getTooltip()));
		});
	}
}
