package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.wand.RetrievalWandItem;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

@SerialClass
public class TrackerRecallToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public UUID owner, golem;

	@Deprecated
	public TrackerRecallToServer() {

	}

	public TrackerRecallToServer(UUID owner, UUID golem) {
		this.owner = owner;
		this.golem = golem;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var sp = context.getSender();
		if (sp == null || !sp.getUUID().equals(owner)) return;
		AbstractGolemEntity<?, ?> target = null;
		for (ServerLevel lv : sp.getServer().getAllLevels()) {
			Entity e = lv.getEntities().get(golem);
			if (e instanceof AbstractGolemEntity<?, ?> g) {
				target = g;
				break;
			}
		}
		if (target == null) {
			sp.sendSystemMessage(MGLangData.RECALL_FAIL.get(), false);
			return;
		}
		if (!target.canWandModify(sp)) return;
		RetrievalWandItem.attemptRetrieve(sp.level(), sp, target);
	}

}