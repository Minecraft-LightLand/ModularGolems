package dev.xkmc.modulargolems.init.loot;

import dev.xkmc.modulargolems.compat.materials.common.CompatManager;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.registries.ForgeRegistries;

public class MGGLMGen extends GlobalLootModifierProvider {

	public MGGLMGen(PackOutput output, String modid) {
		super(output, modid);
	}

	@Override
	protected void start() {
		drop(ModularGolems.MODID, EntityType.IRON_GOLEM, "iron");
		drop(ModularGolems.MODID, EntityType.WARDEN, "sculk");
		CompatManager.onGLMGen(this);
	}

	public void drop(String modid, EntityType<?> type, String material) {
		add("slicing_axe_drop_" + material, new DropPartModifier(
				ForgeRegistries.ENTITY_TYPES.getKey(type),
				new ResourceLocation(modid, material),
				LootItemEntityPropertyCondition.hasProperties(
						LootContext.EntityTarget.DIRECT_KILLER,
						EntityPredicate.Builder.entity().of(GolemTypes.ENTITY_GOLEM.get()).equipment(
								EntityEquipmentPredicate.Builder.equipment().mainhand(
										ItemPredicate.Builder.item().of(GolemItems.SLICING_AXE.get()).build()
								).build()
						).build()
				).build()
		));

	}
}
