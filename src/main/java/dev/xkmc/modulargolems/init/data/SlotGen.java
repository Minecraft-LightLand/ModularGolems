package dev.xkmc.modulargolems.init.data;

import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import top.theillusivec4.curios.api.CuriosDataProvider;
import top.theillusivec4.curios.api.type.data.IEntitiesData;
import top.theillusivec4.curios.common.data.SlotData;

import java.util.concurrent.CompletableFuture;

public class SlotGen extends CuriosDataProvider {

	public SlotGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(ModularGolems.MODID, output, registries);
	}

	@Override
	public void generate(HolderLookup.Provider provider) {
		createSlot("golem_skin").icon(ModularGolems.loc("slot/empty_skin_slot")).order(1000);
		createSlot("golem_route").icon(ModularGolems.loc("slot/empty_route_slot")).order(1100);

		var builder = createEntities("golem_curios")
				.addEntities(GolemTypes.TYPE_GOLEM.get().type(),
						GolemTypes.TYPE_HUMANOID.get().type(),
						GolemTypes.TYPE_DOG.get().type());

		addSlots(builder,
				"golem_route", "golem_skin",
				"curio", "back", "belt", "body", "boot", "bracelet", "charm", "head",
				"hands", "necklace", "ring", "feet",
				"spellbook", "halo", "heart_amulet", "hostility_curse", "accessory",
				"artifact_head", "artifact_necklace", "artifact_bracelet", "artifact_body", "artifact_belt"
		);
		/*
		createEntities("maid_curios").addSlots("head", "back", "ring", "charm", "hands")
				.addEntities(InitEntities.MAID.get())
				.addCondition(new ModLoadedCondition(TouhouLittleMaid.MOD_ID));

		createEntities("maid_artifacts").addSlots("artifact_head", "artifact_necklace", "artifact_bracelet", "artifact_body", "artifact_belt")
				.addEntities(InitEntities.MAID.get())
				.addCondition(new ModLoadedCondition(TouhouLittleMaid.MOD_ID))
				.addCondition(new ModLoadedCondition("l2artifacts"));

 		*/
	}

	private static void addSlots(IEntitiesData builder, String... slots) {
		for (var slot : slots) {
			builder.addSlots(false, new SlotData(slot, true));
		}
	}

}
