package dev.xkmc.modulargolems.compat.materials.blazegear;

import dev.xkmc.modulargolems.compat.materials.common.ClientModDispatch;

public class BGClient extends ClientModDispatch {

	@Override
	public void dispatchClientSetup() {
		DuplicateBlazeArmsLayer.registerLayer();
	}

}
