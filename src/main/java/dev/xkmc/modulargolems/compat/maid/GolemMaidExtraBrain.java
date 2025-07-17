package dev.xkmc.modulargolems.compat.maid;

import com.github.tartaricacid.touhoulittlemaid.api.entity.ai.IExtraMaidBrain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.List;

public class GolemMaidExtraBrain implements IExtraMaidBrain {

	@Override
	public List<MemoryModuleType<?>> getExtraMemoryTypes() {
		return List.of(MaidRegistry.GOLEMS.get());
	}

}
