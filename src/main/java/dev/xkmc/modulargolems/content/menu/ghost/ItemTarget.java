package dev.xkmc.modulargolems.content.menu.ghost;

import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public record ItemTarget(int x, int y, int w, int h, Consumer<ItemStack> con) {

}
