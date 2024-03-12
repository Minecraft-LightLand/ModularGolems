package dev.xkmc.modulargolems.content.entity.humanoid;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public record SpecialRenderProfile(boolean slim, @Nullable ResourceLocation texture) {

}
