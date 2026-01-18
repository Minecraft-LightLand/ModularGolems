package dev.xkmc.modulargolems.events.event;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class GolemEvent extends LivingEvent {

	// 声明了一个私有的、不可变的成员变量 golem
	private final AbstractGolemEntity<?, ?> golem;

	// 构造函数用于创建一个 GolemEvent 对象
	public GolemEvent(AbstractGolemEntity<?, ?> golem) {
		super(golem);
		this.golem = golem;
	}

	// 公共方法，用于返回触发此事件的具体傀儡实体
	public AbstractGolemEntity<?, ?> getEntity() {
		return golem;
	}

}
