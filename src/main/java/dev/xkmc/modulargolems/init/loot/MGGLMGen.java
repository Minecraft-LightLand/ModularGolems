package dev.xkmc.modulargolems.init.loot;

import dev.xkmc.modulargolems.compat.materials.common.CompatManager;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.advancements.criterion.EntityEquipmentPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

public class MGGLMGen extends GlobalLootModifierProvider {

	public MGGLMGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modid) {
		super(output, registries, modid);
	}

	@Override
	protected void start() {
		drop(EntityType.IRON_GOLEM, ModularGolems.loc("iron"));
		drop(EntityType.WARDEN, ModularGolems.loc("sculk"));
		CompatManager.onGLMGen(this);
	}

	public void drop(String modid, EntityType<?> type, String material) {
		drop(modid, type, Identifier.fromNamespaceAndPath(modid, material));
	}

	public void drop(String modid, EntityType<?> type, Identifier material) {
		drop(type, material, new ModLoadedCondition(modid));
	}


	public void drop(EntityType<?> type, Identifier material, ICondition... conditions) {
		var re = registries.lookupOrThrow(Registries.ENTITY_TYPE);
		var ri = registries.lookupOrThrow(Registries.ITEM);
		add("slicing_axe_drop_" + material.getPath(), new DropPartModifier(material,
				LootItemEntityPropertyCondition.hasProperties(
						LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().of(re, type)).build(),
				LootItemEntityPropertyCondition.hasProperties(
						LootContext.EntityTarget.DIRECT_ATTACKER,
						EntityPredicate.Builder.entity().of(re, GolemTypes.ENTITY_GOLEM.get()).equipment(
								EntityEquipmentPredicate.Builder.equipment().mainhand(
										ItemPredicate.Builder.item().of(ri, GolemItems.SLICING_AXE.get())
								).build()
						).build()
				).build()
		), conditions);
	}
}
