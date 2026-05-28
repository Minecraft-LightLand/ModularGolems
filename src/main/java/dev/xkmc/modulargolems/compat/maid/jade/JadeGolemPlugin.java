package dev.xkmc.modulargolems.compat.maid.jade;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeGolemPlugin implements IWailaPlugin {

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerEntityComponent(GolemMaidModelProvider.INSTANCE, HumanoidGolemEntity.class);
		registration.registerEntityComponent(GolemPlayerSkinProvider.INSTANCE, HumanoidGolemEntity.class);
	}

	@Override
	public void register(IWailaCommonRegistration registration) {
	}

}
