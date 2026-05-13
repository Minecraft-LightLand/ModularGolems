package dev.xkmc.modulargolems.events.event;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemRenderState;
import dev.xkmc.modulargolems.content.entity.skin.SpecialRenderSkin;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;

public class HumanoidSkinEvent extends Event {

	private final HumanoidGolemRenderState golem;
	private final ItemStack stack;

	private SpecialRenderSkin skin;

	public HumanoidSkinEvent(HumanoidGolemRenderState golem, ItemStack stack) {
		this.golem = golem;
		this.stack = stack;
	}

	public HumanoidGolemRenderState getGolem() {
		return golem;
	}

	public ItemStack getStack() {
		return stack;
	}

	public void setSkin(@Nullable SpecialRenderSkin skin) {
		this.skin = skin;
	}

	@Nullable
	public SpecialRenderSkin getSkin() {
		return skin;
	}

}
