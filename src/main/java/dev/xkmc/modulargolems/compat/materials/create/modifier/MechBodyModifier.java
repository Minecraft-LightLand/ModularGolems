package dev.xkmc.modulargolems.compat.materials.create.modifier;

import dev.xkmc.modulargolems.compat.materials.create.CreateCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.create.arm.ArmAttachmentItem;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;

public class MechBodyModifier extends GolemModifier {

	public MechBodyModifier() {
		super(StatFilterType.HEAD, 1);
	}

	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		if (golem.level().isClientSide()) return;
		int threshold = 200;
		if (golem.tickCount % 20 != 0) return;
		int mobile = golem.getModifiers().getOrDefault(CreateCompatRegistry.MOBILE.get(), 0);
		int force = golem.getModifiers().getOrDefault(CreateCompatRegistry.FORCE.get(), 0);
		int arm = 0;
		if (golem instanceof MetalGolemEntity e && (e.getHealth() < e.getMaxHealth() * 0.75 || e.isReforged())) {
			if (e.getLeftShoulder().getItem().getItem() instanceof ArmAttachmentItem) arm++;
			if (e.getRightShoulder().getItem().getItem() instanceof ArmAttachmentItem) arm++;
		}
		if (mobile == 0 && force == 0 && arm == 0) return;
		var mobileIns = golem.getEffect(CreateCompatRegistry.EFF_MOBILE.get());
		var forceIns = golem.getEffect(CreateCompatRegistry.EFF_FORCE.get());
		int mobileTime = 0;
		int forceTime = 0;
		if (mobileIns != null) mobileTime = mobileIns.getDuration();
		if (forceIns != null) forceTime = forceIns.getDuration();
		if (mobile > 0 && mobileTime < threshold ||
				force > 0 && forceTime < threshold) {
			ItemStack fuel = golem.getProjectile(CreateCompatRegistry.DUMMY.asStack());
			LivingEntity self = golem;
			if (fuel.isEmpty()) {
				var captain = golem.getCaptain();
				if (captain != null) {
					self = captain;
					fuel = captain.getProjectile(CreateCompatRegistry.DUMMY.asStack());
				}
				if (fuel.isEmpty()) return;
			}
			int time = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);
			if (time <= 0) return;
			ItemStack remain = fuel.getCraftingRemainingItem();
			if (!remain.isEmpty()) {
				self.spawnAtLocation(remain);
			}
			fuel.shrink(1);
			if (mobile > 0) golem.addEffect(new MobEffectInstance(CreateCompatRegistry.EFF_MOBILE.get(),
					mobileTime + time, mobile - 1));
			if (force > 0) golem.addEffect(new MobEffectInstance(CreateCompatRegistry.EFF_FORCE.get(),
					forceTime + time, force - 1));
			long last = golem.getPersistentData().getLong("MechEngineLastPoweredUp");
			long next = Math.max(last, golem.level().getGameTime()) + time;
			golem.getPersistentData().putLong("MechEngineLastPoweredUp", next);
		}
	}

	@Override
	public InteractionResult interact(Player player, AbstractGolemEntity<?, ?> golem, InteractionHand hand, int value) {
		ItemStack stack = player.getItemInHand(hand);
		int time = ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
		if (time <= 0) return InteractionResult.PASS;
		int mobile = golem.getModifiers().getOrDefault(CreateCompatRegistry.MOBILE.get(), 0);
		int force = golem.getModifiers().getOrDefault(CreateCompatRegistry.FORCE.get(), 0);
		int arm = 0;
		if (golem instanceof MetalGolemEntity e && (e.getHealth() < e.getMaxHealth() * 0.75 || e.isReforged())) {
			if (e.getLeftShoulder().getItem().getItem() instanceof ArmAttachmentItem) arm++;
			if (e.getRightShoulder().getItem().getItem() instanceof ArmAttachmentItem) arm++;
		}
		if (mobile == 0 && force == 0 && arm == 0) return InteractionResult.FAIL;
		if (player.level().isClientSide()) return InteractionResult.SUCCESS;
		var mobileIns = golem.getEffect(CreateCompatRegistry.EFF_MOBILE.get());
		var forceIns = golem.getEffect(CreateCompatRegistry.EFF_FORCE.get());
		int mobileTime = 0;
		int forceTime = 0;
		if (mobileIns != null) mobileTime = mobileIns.getDuration();
		if (forceIns != null) forceTime = forceIns.getDuration();
		int maxFactor = MGConfig.COMMON.mechMaxFuel.get();
		boolean pass = arm > 0;
		pass |= mobile > 0 && mobileTime < time * maxFactor;
		pass |= force > 0 && forceTime < time * maxFactor;
		if (!pass)
			return InteractionResult.FAIL;
		if (mobile > 0) golem.addEffect(new MobEffectInstance(CreateCompatRegistry.EFF_MOBILE.get(),
				mobileTime + time, mobile - 1));
		if (force > 0) golem.addEffect(new MobEffectInstance(CreateCompatRegistry.EFF_FORCE.get(),
				forceTime + time, force - 1));
		long last = golem.getPersistentData().getLong("MechEngineLastPoweredUp");
		long next = Math.max(last, golem.level().getGameTime()) + time;
		golem.getPersistentData().putLong("MechEngineLastPoweredUp", next);
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
