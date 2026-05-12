package dev.xkmc.modulargolems.events.event;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class GolemDisableShieldEvent extends GolemItemUseEvent {

	private final LivingEntity source;

	private int cd;

	public GolemDisableShieldEvent(HumanoidGolemEntity golem, ItemStack stack, InteractionHand hand, LivingEntity source, int cd) {
		super(golem, stack, hand);
		this.source = source;
		this.cd = cd;
	}

	public LivingEntity getSource() {
		return source;
	}

	public void setCoolDown(int cd) {
		this.cd = cd;
	}

	public int shieldCoolDown() {
		return cd;
	}

}
