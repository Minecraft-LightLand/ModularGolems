package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import dev.xkmc.modulargolems.compat.materials.common.ClientModDispatch;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud.CloudPhaseHelper;
import dev.xkmc.modulargolems.content.client.override.ModelOverride;
import dev.xkmc.modulargolems.content.client.override.ModelOverrides;
import net.minecraft.resources.ResourceLocation;

public class LMClient extends ClientModDispatch {

	@Override
	public void dispatchClientSetup() {
		// Boss 变 angry 的阈值是 Crackiness.MEDIUM（低于 65%），贴图切换跟着走，不再用半血
		ModelOverrides.registerOverride(new ResourceLocation(LMDispatch.MODID, "cloud"),
				ModelOverride.texturePredicate((e) -> CloudPhaseHelper.inSecondPhase(e) ? "_dark" : ""));
		ModelOverrides.registerOverride(new ResourceLocation(LMDispatch.MODID, "paladin"),
				ModelOverride.texturePredicate((e) -> e.getHealth() < e.getMaxHealth() * 0.65 ? "_posessed" : ""));
	}

}
