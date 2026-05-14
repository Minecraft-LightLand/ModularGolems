package dev.xkmc.modulargolems.content.entity.misc;

import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class BeaconLaserRenderState extends EntityRenderState {

	public float xrot, yrot, perc, scale, len;


	public void update(BeaconLaserEntity e, float partialTicks) {
		xrot = e.getXRot(partialTicks);
		yrot = e.getYRot(partialTicks);
		perc = Math.max(0, 1 - (e.tickCount + partialTicks) / e.life);
		scale = e.getOwner() instanceof MetalGolemEntity g ? g.getScale() : 1;
		len = e.len;
	}
}
