package dev.xkmc.modulargolems.content.entity.render;

import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public interface AbstractGolemRenderState<
		E extends AbstractGolemEntity<E, P>,
		S extends LivingEntityRenderState & AbstractGolemRenderState<E, S, P>,
		P extends IGolemPart<P>
		> {

	ContextKey<ItemStack> SKIN = new ContextKey<>(ModularGolems.loc("golem_skin"));

	default S self() {
		return Wrappers.cast(this);
	}

	ItemStack getSkin();

	List<GolemMaterial> getMaterials();

	int getId();

	boolean isAggressive();

	int getVehicleId();
}
