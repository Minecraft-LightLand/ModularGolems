package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import dev.xkmc.modulargolems.compat.materials.common.ClientModDispatch;
import dev.xkmc.modulargolems.content.client.override.ModelOverride;
import dev.xkmc.modulargolems.content.client.override.ModelOverrides;
import net.minecraft.resources.ResourceLocation;

public class LMClient extends ClientModDispatch {

	@Override
	public void dispatchClientSetup() {
		ModelOverrides.registerOverride(new ResourceLocation(LMDispatch.MODID, "cloud"),
				ModelOverride.texturePredicate((e) -> e.getHealth() <= e.getMaxHealth() / 2 ? "_dark" : ""));
		ModelOverrides.registerOverride(new ResourceLocation(LMDispatch.MODID, "paladin"),
				ModelOverride.texturePredicate((e) -> e.getHealth() < e.getMaxHealth() * 0.65 ? "_posessed" : ""));
	}

}
