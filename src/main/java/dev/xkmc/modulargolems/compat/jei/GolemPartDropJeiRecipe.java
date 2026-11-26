package dev.xkmc.modulargolems.compat.jei;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 一条「傀儡部件掉落」JEI 配方：金属傀儡用斩铁巨斧击杀 {@code mob} 后，随机掉落 {@code drops} 中的一种部件。
 */
public record GolemPartDropJeiRecipe(EntityType<?> mob, List<ItemStack> drops) {
}
