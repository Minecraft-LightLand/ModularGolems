package dev.xkmc.modulargolems.init.data;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import dev.xkmc.l2library.compat.curios.CurioEntityBuilder;
import dev.xkmc.l2library.compat.curios.CurioSlotBuilder;
import dev.xkmc.l2library.compat.curios.SlotCondition;
import dev.xkmc.l2library.serial.config.RecordDataProvider;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class SlotGen extends RecordDataProvider {

	public SlotGen(DataGenerator generator) {
		super(generator, "Curios Generator");
	}

	@Override
	public void add(BiConsumer<String, Record> map) {

		map.accept(ModularGolems.MODID + "/curios/slots/golem_skin", new CurioSlotBuilder(1000,
				ModularGolems.loc("slot/empty_skin_slot").toString()));

		map.accept(ModularGolems.MODID + "/curios/slots/golem_route", new CurioSlotBuilder(1100,
				ModularGolems.loc("slot/empty_route_slot").toString()));

		map.accept(ModularGolems.MODID + "/curios/entities/golem_skin", new CurioEntityBuilder(
				new ArrayList<>(List.of(GolemTypes.TYPE_HUMANOID.getId())),
				new ArrayList<>(List.of("golem_skin")), SlotCondition.of()
		));

		ArrayList<ResourceLocation> entities = new ArrayList<>(List.of(
				GolemTypes.TYPE_GOLEM.getId(),
				GolemTypes.TYPE_HUMANOID.getId(),
				GolemTypes.TYPE_DOG.getId()
		));

		map.accept(ModularGolems.MODID + "/curios/entities/golem_curios", new CurioEntityBuilder(entities,
				new ArrayList<>(List.of("head", "back", "ring", "charm", "hands", "golem_route")), SlotCondition.of()
		));

		map.accept(ModularGolems.MODID + "/curios/entities/golem_artifacts", new CurioEntityBuilder(entities,
				new ArrayList<>(List.of("artifact_head", "artifact_necklace", "artifact_bracelet", "artifact_body", "artifact_belt")),
				SlotCondition.of("l2artifacts")
		));

		map.accept(ModularGolems.MODID + "/curios/entities/maid_artifacts", new CurioEntityBuilder(
				new ArrayList<>(List.of(InitEntities.MAID.getId())),
				new ArrayList<>(List.of("artifact_head", "artifact_necklace", "artifact_bracelet", "artifact_body", "artifact_belt")),
				SlotCondition.of(TouhouLittleMaid.MOD_ID, "l2artifacts")
		));

		map.accept(ModularGolems.MODID + "/curios/entities/maid_curios", new CurioEntityBuilder(
				new ArrayList<>(List.of(InitEntities.MAID.getId())),
				new ArrayList<>(List.of("head", "back", "ring", "charm", "hands")),
				SlotCondition.of(TouhouLittleMaid.MOD_ID)
		));
	}
}
