package dev.xkmc.modulargolems.init.data;

import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TagGen {

	public static final TagKey<Item> SCULK_MATS = createItemTag("sculk_materials");
	public static final TagKey<Item> GOLEM_PARTS = createItemTag("parts");
	public static final TagKey<Item> GOLEM_HOLDERS = createItemTag("holders");
	public static final TagKey<Item> GOLEM_UPGRADES = createItemTag("upgrades");
	public static final TagKey<Item> BLUE_UPGRADES = createItemTag("blue_upgrades");
	public static final TagKey<Item> POTION_UPGRADES = createItemTag("potion_upgrades");
	public static final TagKey<EntityType<?>> GOLEM_FRIENDLY = createEntityTag("friendly");
	public static final TagKey<Block> POTENTIAL_DST = createBlockTag("potential_destination");

	public static final List<Consumer<RegistrateItemTagsProvider>> OPTIONAL_ITEM = new ArrayList<>();
	public static final List<Consumer<RegistrateTagsProvider<Block>>> OPTIONAL_BLOCK = new ArrayList<>();

	public static void onBlockTagGen(RegistrateTagsProvider<Block> pvd) {
		pvd.addTag(POTENTIAL_DST)
				.addTag(BlockTags.SHULKER_BOXES)
				.addTag(Tags.Blocks.CHESTS)
				.addTag(Tags.Blocks.BARRELS);
		OPTIONAL_BLOCK.forEach(e -> e.accept(pvd));
	}

	public static void onItemTagGen(RegistrateItemTagsProvider pvd) {
		pvd.addTag(SCULK_MATS).add(Items.ECHO_SHARD);
		OPTIONAL_ITEM.forEach(e -> e.accept(pvd));
		pvd.addTag(BLUE_UPGRADES).add(
				GolemItems.BELL.get(),
				GolemItems.ENDER_SIGHT.get(),
				GolemItems.FLOAT.get(),
				GolemItems.PICKUP.get(),
				GolemItems.PICKUP_MENDING.get(),
				GolemItems.PICKUP_NO_DESTROY.get(),
				GolemItems.RECYCLE.get(),
				GolemItems.SWIM.get(),
				GolemItems.FIRE_IMMUNE.get(),
				GolemItems.THUNDER_IMMUNE.get(),
				GolemItems.PLAYER_IMMUNE.get()
		);
		pvd.addTag(POTION_UPGRADES).add(
				GolemItems.WEAK.get(),
				GolemItems.SLOW.get(),
				GolemItems.WITHER.get()
		);
	}

	public static void onEntityTagGen(RegistrateTagsProvider.IntrinsicImpl<EntityType<?>> pvd) {
		pvd.addTag(GOLEM_FRIENDLY).add(EntityType.PLAYER, EntityType.SNOW_GOLEM);
		pvd.addTag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(GolemTypes.ENTITY_GOLEM.get(),
				GolemTypes.ENTITY_HUMANOID.get(), GolemTypes.ENTITY_DOG.get());
	}

	private static TagKey<Item> createItemTag(String id) {
		return ItemTags.create(new ResourceLocation(ModularGolems.MODID, id));
	}

	private static TagKey<EntityType<?>> createEntityTag(String id) {
		return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(ModularGolems.MODID, id));
	}

	private static TagKey<Block> createBlockTag(String id) {
		return TagKey.create(Registries.BLOCK, new ResourceLocation(ModularGolems.MODID, id));
	}

}
