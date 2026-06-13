package dev.xkmc.modulargolems.compat.jade;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.neoforged.fml.ModList;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeGolemPlugin implements IWailaPlugin {

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerEntityComponent(GolemPlayerSkinProvider.INSTANCE, HumanoidGolemEntity.class);
		if (ModList.get().isLoaded(TouhouLittleMaid.MOD_ID)) {
			registration.registerEntityComponent(GolemMaidModelProvider.INSTANCE, HumanoidGolemEntity.class);
		}
	}

	@Override
	public void register(IWailaCommonRegistration registration) {
	}

}
