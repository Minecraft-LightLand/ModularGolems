package dev.xkmc.modulargolems.content.item.golem;

import com.tterrag.registrate.util.CreativeModeTabModifier;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GolemFacade extends Item {

	public GolemFacade(Properties prop) {
		super(prop);
	}

	public static ItemStack setMaterial(ItemStack stack, ResourceLocation id) {
		return GolemItems.DC_PART_MAT.set(stack, id);
	}

	public static ResourceLocation getMaterial(ItemStack stack) {
		return GolemItems.DC_PART_MAT.getOrDefault(stack, ModularGolems.loc("iron"));
	}

	@Override
	public Component getName(ItemStack stack) {
		var id = getMaterial(stack);
		return Component.translatable(this.getDescriptionId(stack)).append(": ").append(
				Component.translatable("golem_material." + id.getNamespace() + "." + id.getPath()));
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
