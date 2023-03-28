package dev.xkmc.modulargolems.init.data;

import dev.xkmc.l2library.repack.registrate.providers.RegistrateItemTagsProvider;
import dev.xkmc.l2library.repack.registrate.providers.RegistrateTagsProvider;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TagGen {

	public static final TagKey<Item> SCULK_MATS = createItemTag("sculk_materials");
	public static final TagKey<Item> GOLEM_PARTS = createItemTag("parts");
	public static final TagKey<Item> GOLEM_HOLDERS = createItemTag("holders");
	public static final TagKey<Item> GOLEM_UPGRADES = createItemTag("upgrades");
	public static final TagKey<EntityType<?>> GOLEM_FRIENDLY = createEntityTag("friendly");

	public static final List<Consumer<RegistrateItemTagsProvider>> OPTIONALS = new ArrayList<>();

	public static void onBlockTagGen(RegistrateTagsProvider<Block> pvd) {
	}

	public static void onItemTagGen(RegistrateItemTagsProvider pvd) {
		pvd.tag(SCULK_MATS).add(Items.ECHO_SHARD);
		OPTIONALS.forEach(e -> e.accept(pvd));
	}

	public static void onEntityTagGen(RegistrateTagsProvider<EntityType<?>> pvd) {
		pvd.tag(GOLEM_FRIENDLY).add(EntityType.PLAYER, EntityType.SNOW_GOLEM);
	}

	private static TagKey<Item> createItemTag(String id) {
		return ItemTags.create(new ResourceLocation(ModularGolems.MODID, id));
	}

	private static TagKey<EntityType<?>> createEntityTag(String id) {
		return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(ModularGolems.MODID, id));
	}

}
