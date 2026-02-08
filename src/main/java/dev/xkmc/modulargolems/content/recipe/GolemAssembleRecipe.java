package dev.xkmc.modulargolems.content.recipe;

import dev.xkmc.l2library.serial.recipe.AbstractShapedRecipe;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.registrate.GolemMiscs;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public class GolemAssembleRecipe extends AbstractShapedRecipe<GolemAssembleRecipe> {

	public GolemAssembleRecipe(ResourceLocation id, String group, int w, int h, NonNullList<Ingredient> ings, ItemStack result) {
		super(id, group, w, h, ings, result);
	}

	@Override
	public boolean matches(CraftingContainer cont, Level level) {
		if (!super.matches(cont, level)) return false;
		for (int i = 0; i < cont.getContainerSize(); i++) {
			ItemStack input = cont.getItem(i);
			// 如果物品堆非空且物品是GolemPart的实例
			if (!input.isEmpty() && input.getItem() instanceof GolemPart part) {
				// 检查该傀儡部件对应材料是否为空
				if (GolemPart.getMaterial(input).isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public ItemStack assemble(CraftingContainer cont, RegistryAccess access) {
		ItemStack stack = super.assemble(cont, access);
		for (int i = 0; i < cont.getContainerSize(); i++) {
			ItemStack input = cont.getItem(i);
			if (!input.isEmpty() && input.getItem() instanceof GolemPart part) {
				GolemPart.getMaterial(input).ifPresent(mat -> GolemHolder.addMaterial(stack, part, mat));
			}
		}
		return stack;
	}

	@Override
	// getSerializer方法重写了父类的方法，用于返回该配方类型的序列化器（Serializer），以便将配方数据写入或从数据包中读取
	public Serializer<GolemAssembleRecipe> getSerializer() {
		return GolemMiscs.ASSEMBLE.get();
	}

}
