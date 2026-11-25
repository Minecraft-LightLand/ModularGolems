package dev.xkmc.modulargolems.compat.maid;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.GeckoEntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.ExtraMaidBrainManager;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Mob;

@LittleMaidExtension
public class MaidPlugin implements ILittleMaid {

	@Override
	public void addMaidTask(TaskManager manager) {
		manager.add(new MaidSummonerTask());
	}

	@Override
	public void addExtraMaidBrain(ExtraMaidBrainManager manager) {
		manager.addExtraMaidBrain(new GolemMaidExtraBrain());
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void addAdditionGeckoMaidLayer(GeckoEntityMaidRenderer<? extends Mob> renderer, EntityRendererProvider.Context context) {
		((GeckoEntityMaidRenderer) renderer).addGeoLayerRenderer(new GolemMaidBannerGeoLayer(renderer, context.getItemInHandRenderer()));
	}

}
