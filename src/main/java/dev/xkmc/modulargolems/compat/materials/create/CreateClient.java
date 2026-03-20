package dev.xkmc.modulargolems.compat.materials.create;

import dev.xkmc.modulargolems.compat.materials.common.ClientModDispatch;
import dev.xkmc.modulargolems.compat.materials.create.arm.ArmAttachmentItem;
import dev.xkmc.modulargolems.compat.materials.create.arm.ArmPose;
import dev.xkmc.modulargolems.content.client.pose.GolemShoulderPose;

public class CreateClient extends ClientModDispatch {

	@Override
	public void dispatchClientSetup() {
		GolemShoulderPose.register(ArmAttachmentItem.ID, new ArmPose());
	}

}
