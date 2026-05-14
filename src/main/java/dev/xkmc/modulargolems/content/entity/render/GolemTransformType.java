package dev.xkmc.modulargolems.content.entity.render;

import net.minecraft.world.item.ItemDisplayContext;

import java.util.List;

import static net.minecraft.world.item.ItemDisplayContext.*;

public enum GolemTransformType {
	FIRST(GUI, FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND),
	THIRD(THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND),
	ENTITY(GROUND),
	DEF(NONE, HEAD, FIXED),
	OTHER();


	public final List<ItemDisplayContext> ctx;

	GolemTransformType(ItemDisplayContext... ctxs) {
		ctx = List.of(ctxs);
	}


}
