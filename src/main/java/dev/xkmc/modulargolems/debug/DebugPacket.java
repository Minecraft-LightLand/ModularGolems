package dev.xkmc.modulargolems.debug;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.goals.GolemMeleeGoal;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;

@SerialClass
public class DebugPacket extends SerialPacketBase {

	@SerialClass.SerialField
	public int golem;
	@SerialClass.SerialField
	public ArrayList<String> list = new ArrayList<>();

	public DebugPacket() {

	}

	public DebugPacket(int id, ArrayList<String> list) {
		this.golem = id;
		this.list = list;
	}

	public static void fill(AbstractGolemEntity<?, ?> golem, List<String> list) {
		GolemMeleeGoal goal = golem.meleeGoal;
		var running = golem.goalSelector.getRunningGoals().anyMatch(e -> e.getGoal() == goal);
		list.add("Melee: " + running);
		list.add("Attack Delay: " + goal.ticksUntilNextAttack);
		list.add("Walk Delay: " + goal.repathDelay);
		list.add("Pathing: " + !golem.getNavigation().isDone());
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ClientDebugInfo.handle(golem, list);
	}

}
