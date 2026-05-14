package dev.xkmc.modulargolems.content.entity.render;

import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.Entity;

public interface AbstractGolemRenderState<
		E extends AbstractGolemEntity<E, P>,
		S extends LivingEntityRenderState & AbstractGolemRenderState<E, S, P>,
		P extends IGolemPart<P>
		> {

	default S self() {
		return Wrappers.cast(this);
	}

	CommonGolemRenderState common();

	default boolean isPassengerOfSameVehicle(Entity cam) {
		var veh = cam.getVehicle();
		if (veh == null) return false;
		return veh.getId() == common().id() || veh.getId() == common().getVehicleId();
	}

	void update(E entity, float pt, ItemModelResolver imr);

}
