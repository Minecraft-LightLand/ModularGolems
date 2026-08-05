package dev.xkmc.modulargolems.content.item.golem;

import com.tterrag.registrate.util.CreativeModeTabModifier;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class GolemFacade extends Item {

	public GolemFacade(Properties prop) {
		super(prop);
	}

	public static ItemStack setMaterial(ItemStack stack, ResourceLocation id) {
		stack.getOrCreateTag().putString("FacadeSkin", id.toString());
		return stack;
	}

	public static ResourceLocation getMaterial(ItemStack stack) {
		var tag = stack.getTag();
		if (tag != null && tag.contains("FacadeSkin", Tag.TAG_STRING)) {
			String id = tag.getString("FacadeSkin");
			var ans = ResourceLocation.tryParse(id);
			if (ans != null) {
				return ans;
			}
		}
		return ModularGolems.loc("iron");
	}

	@Override
	public Component getName(ItemStack stack) {
		var id = getMaterial(stack);
		return Component.translatable(this.getDescriptionId(stack)).append(": ").append(
				Component.translatable("golem_material." + id.getNamespace() + "." + id.getPath()));
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(GolemBEWLR.EXTENSIONS);
	}

	public void fillItemCategory(CreativeModeTabModifier tab) {
		for (ResourceLocation rl : GolemMaterialConfig.get().getAllMaterials()) {
			if (!GolemMaterialConfig.mayApply(GolemItems.GOLEM_BODY.get(), rl)) continue;
			ItemStack stack = new ItemStack(this);
			setMaterial(stack, rl);
			tab.accept(stack);
		}
	}

}
