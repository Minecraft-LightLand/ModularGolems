package dev.xkmc.modulargolems.content.item;

import dev.xkmc.l2library.serial.NBTObj;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.core.GolemType;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class GolemHolder extends Item {

	private static final String KEY = "golem_materials";

	private final Supplier<GolemType<?>> type;

	public GolemHolder(Properties props, Supplier<GolemType<?>> type) {
		super(props);
		this.type = type;
	}

	public static List<GolemMaterial> getMaterial(ItemStack stack) {
		ArrayList<GolemMaterial> ans = new ArrayList<>();
		Optional.ofNullable(stack.getTag())
				.map(e -> e.contains(KEY) ? e.getCompound(KEY) : null)
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
		obj.getSub(KEY).tag.put(rl.toString(), StringTag.valueOf(material.toString()));
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
