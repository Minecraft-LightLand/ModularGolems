package dev.xkmc.modulargolems.compat.materials.l2complements;

import dev.xkmc.modulargolems.compat.materials.common.ClientModDispatch;

public class LCClient extends ClientModDispatch {

	@Override
	public void dispatchClientSetup() {
		ForceFieldLayer.registerLayer();
	}

}
