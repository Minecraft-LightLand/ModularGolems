package dev.xkmc.modulargolems.content.item.render;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record IsInTag(TagKey<Item> tag) implements ConditionalItemModelProperty {

	public static final MapCodec<IsInTag> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
					Identifier.CODEC.fieldOf("tag").forGetter(e -> e.tag().location())
			).apply(i, IsInTag::new)
	);

	public IsInTag(Identifier tag) {
		this(TagKey.create(Registries.ITEM, tag));
	}

	@Override
	public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
		return stack.is(tag);
	}

	@Override
	public MapCodec<IsInTag> type() {
		return MAP_CODEC;
	}

}
