package dev.xkmc.modulargolems.content.item;

import dev.xkmc.modulargolems.content.core.GolemMaterial;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AbstractGolemHolder extends Item {

	private static final String KEY = "golem_materials";

	private final GolemType<?> type;

	public AbstractGolemHolder(Properties props, GolemType<?> type) {
		super(props);
		this.type = type;
	}

	public static List<GolemMaterial> getMaterial(ItemStack stack) {
		ArrayList<GolemMaterial> ans = new ArrayList<>();
		Optional.ofNullable(stack.getTag())
				.map(e -> e.contains(KEY) ? e.getList(KEY, Tag.TAG_STRING) : null)
				.ifPresent(e -> {
					for (int i = 0; i < e.size(); i++) {
						GolemMaterial material = GolemTypeRegistry.MATERIALS.get().getValue(new ResourceLocation(e.getString(i)));
						if (material != null) {
							ans.add(material);
						}
					}
				});
		return ans;
	}

	public static ItemStack addMaterial(ItemStack stack, GolemMaterial material) {
		CompoundTag tag = stack.getOrCreateTag();
		ListTag list;
		if (!tag.contains(KEY)) {
			tag.put(KEY, list = new ListTag());
		} else {
			list = tag.getList(KEY, Tag.TAG_STRING);
		}
		list.add(StringTag.valueOf(material.getID()));
		return stack;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		List<GolemMaterial> mats = getMaterial(stack);
		mats.forEach(e -> list.add(e.getDesc()));
		GolemMaterial.collectAttributes(mats).forEach((k, v) -> list.add(k.getAdderTooltip(v)));
	}
}
