package dev.xkmc.modulargolems.compat.materials.create;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;

public class MechBodyModifier extends GolemModifier {

	public MechBodyModifier() {
		super(StatFilterType.HEAD, 1);
	}

	@Override
	public InteractionResult interact(Player player, AbstractGolemEntity<?, ?> golem, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		int time = ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
		if (time <= 0) return InteractionResult.PASS;
		int mobile = golem.getModifiers().getOrDefault(CreateCompatRegistry.MOBILE.get(), 0);
		int force = golem.getModifiers().getOrDefault(CreateCompatRegistry.FORCE.get(), 0);
		if (mobile == 0 && force == 0) return InteractionResult.FAIL;
		if (player.level().isClientSide()) return InteractionResult.SUCCESS;
		var mobileIns = golem.getEffect(CreateCompatRegistry.EFF_MOBILE.get());
		var forceIns = golem.getEffect(CreateCompatRegistry.EFF_FORCE.get());
		int mobileTime = 0;
		int forceTime = 0;
		if (mobileIns != null) mobileTime = mobileIns.getDuration();
		if (forceIns != null) forceTime = forceIns.getDuration();
		int maxFactor = MGConfig.COMMON.mechMaxFuel.get();
		if (mobile > 0 && mobileTime >= time * maxFactor)
			return InteractionResult.FAIL;
		if (force > 0 && forceTime >= time * maxFactor)
			return InteractionResult.FAIL;
		if (mobile > 0) golem.addEffect(new MobEffectInstance(CreateCompatRegistry.EFF_MOBILE.get(),
				mobileTime + time, mobile - 1));
		if (force > 0) golem.addEffect(new MobEffectInstance(CreateCompatRegistry.EFF_FORCE.get(),
				forceTime + time, forceTime - 1));
		if (!player.isCreative()) {
			ItemStack remain = stack.getCraftingRemainingItem();
			stack.shrink(1);
			if (!remain.isEmpty()) {
				player.getInventory().placeItemBackInInventory(remain);
			}
		}
		float f1 = 1 + (golem.getRandom().nextFloat() - golem.getRandom().nextFloat()) * 0.2F;
		golem.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1, f1);
		return InteractionResult.SUCCESS;
	}
}
