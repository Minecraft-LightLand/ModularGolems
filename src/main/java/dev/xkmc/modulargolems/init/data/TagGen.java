package dev.xkmc.modulargolems.init.data;

import dev.xkmc.l2library.repack.registrate.providers.RegistrateTagsProvider;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class TagGen {

	public static final TagKey<Item> SCULK_MATS = ItemTags.create(new ResourceLocation(ModularGolems.MODID, "sculk_materials"));
	public static final TagKey<Item> GOLEM_PARTS = ItemTags.create(new ResourceLocation(ModularGolems.MODID, "parts"));
	public static final TagKey<Item> GOLEM_HOLDERS = ItemTags.create(new ResourceLocation(ModularGolems.MODID, "holders"));
	public static final TagKey<Item> GOLEM_UPGRADES = ItemTags.create(new ResourceLocation(ModularGolems.MODID, "upgrades"));

	public static void onBlockTagGen(RegistrateTagsProvider<Block> pvd) {
	}

	public static void onItemTagGen(RegistrateTagsProvider<Item> pvd) {
		pvd.tag(SCULK_MATS).add(Items.ECHO_SHARD);
	}

}
