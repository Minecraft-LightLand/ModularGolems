package dev.xkmc.modulargolems.content.item.golem;

import dev.xkmc.modulargolems.content.entity.common.GolemFlags;

import java.util.function.Consumer;

public class FlagTest implements Consumer<GolemFlags> {

	private final GolemFlags flag;
	private boolean match = false;

	public FlagTest(GolemFlags flag) {
		this.flag = flag;
	}

	@Override
	public void accept(GolemFlags flag) {
		match |= flag == this.flag;
	}

	public boolean matched() {
		return match;
	}

}
