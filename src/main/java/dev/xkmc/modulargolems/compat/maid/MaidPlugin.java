package dev.xkmc.modulargolems.compat.maid;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.ExtraMaidBrainManager;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;

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

}
