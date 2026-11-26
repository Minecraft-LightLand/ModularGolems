package dev.xkmc.modulargolems.compat.jei;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/**
 * 一条「傀儡装配台」JEI 配方：模板 + 各部件格材料 → 成品（部件或整只傀儡）。
 * <p>
 * 装配台是按 {@code GolemType} 程序化合成的，没有数据包配方，由 {@link GolemJEIPlugin} 枚举。
 * {@code parts} 恒定长度 5，按 {@link dev.xkmc.modulargolems.content.core.GolemSlot} 顺序
 * （UP/LEFT/MIDDLE/RIGHT/DOWN）：
 * <ul>
 *   <li>{@code null} —— 该傀儡类型没有这个部件格，不显示该槽；</li>
 *   <li>{@link Ingredient#EMPTY} —— 有该部件格但不放材料（部件配方里非目标格），显示空槽 + 部位虚影；</li>
 *   <li>非空 —— 该格放的材料。</li>
 * </ul>
 * {@code ghosts} 与 {@code parts} 对齐，给出每个部件格对应的部件物品（用于画虚影 / 成品配方里「材料或部件」轮换）。
 * {@code typeIcon} 是分类里显示的胚料物品；{@code preview} 是左侧 3D 预览用的傀儡物品
 * （整只配方就是成品；部件配方是「该部件有材料、其余部件为胚料」的傀儡）。
 */
public record GolemAssemblyJeiRecipe(Ingredient template, List<Ingredient> parts, List<ItemStack> ghosts,
                                     ItemStack typeIcon, ItemStack preview, ItemStack result) {
}
