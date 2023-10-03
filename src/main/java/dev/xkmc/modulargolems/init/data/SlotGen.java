package dev.xkmc.modulargolems.init.data;

import dev.xkmc.l2library.compat.curios.CurioEntityBuilder;
import dev.xkmc.l2library.compat.curios.SlotCondition;
import dev.xkmc.l2library.serial.config.RecordDataProvider;
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

		ArrayList<ResourceLocation> entities = new ArrayList<>(List.of(
				GolemTypes.TYPE_GOLEM.getId(),
				GolemTypes.TYPE_HUMANOID.getId(),
				GolemTypes.TYPE_DOG.getId()
		));

		map.accept("curios/curios/entities/golem_curios", new CurioEntityBuilder(entities,
				new ArrayList<>(List.of("head", "back", "ring", "charm")), SlotCondition.of()
		));

		map.accept("curios/curios/entities/golem_artifacts", new CurioEntityBuilder(entities,
				new ArrayList<>(List.of("artifact_head", "artifact_necklace", "artifact_bracelet", "artifact_body", "artifact_belt")),
				SlotCondition.of("l2artifacts")
		));
	}
}
