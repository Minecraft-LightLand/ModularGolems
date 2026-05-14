package dev.xkmc.modulargolems.content.entity.metalgolem;

import dev.xkmc.modulargolems.content.client.weapon.GolemModelAnimations;
import dev.xkmc.modulargolems.content.item.ranged.IShoulderWeapon;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record MetalGolemShoulderModelState(
		Identifier model, Identifier tex, @Nullable Identifier emissive,
		Object2FloatOpenHashMap<Identifier> anims
) {

	@Nullable
	public static MetalGolemShoulderModelState of(MetalGolemEntity entity, ItemStack stack, HumanoidArm arm, float pt) {
		if (!(stack.getItem() instanceof IShoulderWeapon item)) return null;
		var model = item.getModelForHand(arm);
		if (model == null) return null;
		var list = item.getAnimationData(entity, stack, arm);
		var tex = item.getModelTexture(entity, stack, arm);
		var emi = item.emissive() ? item.getEmissiveTexture(entity, stack, arm) : null;
		Object2FloatOpenHashMap<Identifier> map = new Object2FloatOpenHashMap<>();
		for (var entry : list) {
			if (GolemModelAnimations.MAP.containsKey(entry.id())) {
				var anim = GolemModelAnimations.MAP.get(entry.id());
				if (anim != null) {
					map.put(entry.id(), entry.tick() + entry.speed() * pt);
				}
			}
		}
		return new MetalGolemShoulderModelState(model, tex, emi, map);
	}

}
