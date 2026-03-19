package dev.xkmc.modulargolems.compat.materials.create.arm;

import dev.xkmc.modulargolems.compat.materials.create.CreateCompatRegistry;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.ranged.ShouldWeaponItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ArmAttachmentItem extends ShouldWeaponItem {

	public static final ResourceLocation ID = CreateCompatRegistry.loc("arm");

	public ArmAttachmentItem(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable ResourceLocation getModelForHand(InteractionHand hand) {
		return ID;
	}

	@Override
	public @Nullable ResourceLocation getAnimationId(MetalGolemEntity user, ItemStack stack, InteractionHand hand) {
		return ID;
	}

	@Override
	public float getAnimationSpeed(MetalGolemEntity user, ItemStack stack, InteractionHand hand) {
		return 0;
	}

	@Override
	public float getAnimationTick(MetalGolemEntity user, ItemStack stack, InteractionHand hand) {
		return 0;
	}

	@Override
	public void onTick(MetalGolemEntity e, ItemStack stack, InteractionHand hand) {

	}

}
