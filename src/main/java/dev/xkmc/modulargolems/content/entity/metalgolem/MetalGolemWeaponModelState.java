package dev.xkmc.modulargolems.content.entity.metalgolem;

import dev.xkmc.modulargolems.content.client.weapon.GolemModelAnimations;
import dev.xkmc.modulargolems.content.client.weapon.IEntityModelWeapon;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record MetalGolemWeaponModelState(
		Identifier model, Identifier tex, @Nullable Identifier emissive,
		boolean playAnim, float animTick
) {

	@Nullable
	public static MetalGolemWeaponModelState of(MetalGolemEntity e, ItemStack stack, HumanoidArm arm, float pt) {
		if (!(stack.getItem() instanceof IEntityModelWeapon item)) return null;
		var id = item.getModelForHand(arm);
		if (id == null) return null;
		boolean playAnim = item.shouldPlayAnimation(e, stack, arm);
		float animTick = 0;
		if (playAnim) {
			var anim = GolemModelAnimations.MAP.get(id);
			if (anim != null) {
				float speed = item.getAnimationSpeed(e, stack, arm);
				float tick = item.getAnimationTick(e, stack, arm);
				animTick = tick + speed * pt;
			}
		}
		Identifier tex = item.getModelTexture(e, stack, arm);
		Identifier emi = null;
		if (item.emissive()) {
			emi = item.getEmissiveTexture(e, stack, arm);
		}
		return new MetalGolemWeaponModelState(id, tex, emi, playAnim, animTick);
	}

}
