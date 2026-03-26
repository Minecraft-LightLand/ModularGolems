package dev.xkmc.modulargolems.compat.materials.create.arm;

import dev.xkmc.l2core.util.DCStack;
import dev.xkmc.modulargolems.compat.materials.create.CreateCompatRegistry;
import dev.xkmc.modulargolems.content.client.weapon.ShoulderAnimData;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.item.ranged.ShouldWeaponItem;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
	public List<ShoulderAnimData> getAnimationData(MetalGolemEntity user, ItemStack stack, InteractionHand hand) {
		return List.of();
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
		list.add(MGLangData.MECHANICAL_ARM.get());
		super.appendHoverText(stack, level, list, flag);
	}

	@Override
	public void onTick(MetalGolemEntity e, ItemStack stack, InteractionHand hand) {
		if (e.level().isClientSide()) return;
		var time = e.level().getGameTime();
		long last = stack.getOrDefault(GolemItems.DC_TIMESTAMP, 0L);
		int takingItem = stack.getOrDefault(GolemItems.DC_CHARGE, 0);
		var speed = Mth.clamp(stack.getOrDefault(GolemItems.DC_USE_SPEED, 0f), 0.25f, 4f);
		if (last > time || last < time - (int) (100 / speed)) {
			ItemStack other = hand == InteractionHand.MAIN_HAND ? e.getLeftShoulder().getItem() : e.getRightShoulder().getItem();
			if (other.getItem() instanceof ArmAttachmentItem) {
				long prev = other.getOrDefault(GolemItems.DC_TIMESTAMP, 0L);
				if (prev <= time && prev > time - 20) return;
			}
			if (e.getHealth() > e.getMaxHealth() * 0.75 && !e.isReforged()) return;
			var take = fetch(e, true);
			if (take.isEmpty()) return;
			stack.set(GolemItems.DC_TIMESTAMP, time);
			stack.set(GolemItems.DC_CHARGE, 2);
			stack.set(GolemItems.DC_USE_SPEED, getSpeed(e));
			stack.set(GolemItems.DC_DISPLAY_ITEM, new DCStack(take.copy()));
		} else {
			if (takingItem == 2 && last >= time - (int) (40 / speed)) {
				stack.set(GolemItems.DC_CHARGE, 1);
				if (e.getHealth() > e.getMaxHealth() * 0.75 && !e.isReforged())
					stack.set(GolemItems.DC_TIMESTAMP, 0L);
			}
			if (takingItem >= 1 && last >= time - (int) (60 / speed)) {
				stack.remove(GolemItems.DC_CHARGE);
				if (e.getHealth() <= e.getMaxHealth() * 0.75 || e.isReforged()) {
					var take = fetch(e, false);
					if (!take.isEmpty()) {
						e.repairWithItem();
					}
				}
			}
		}
	}

	public static float getSpeed(MetalGolemEntity e) {
		float ans = MGConfig.COMMON.mechanicalArmSpeed.get().floatValue();
		if (e.getPersistentData().getLong("MechEngineLastPoweredUp") > e.level().getGameTime()) {
			ans += MGConfig.COMMON.mechanicalArmPowerBonus.get().floatValue();
		}
		float bonus = 0;
		if (e.getModifiers().containsKey(CreateCompatRegistry.BODY.get()))
			bonus += 0.2f;
		var mobile = e.getEffect(CreateCompatRegistry.EFF_MOBILE);
		var force = e.getEffect(CreateCompatRegistry.EFF_FORCE);
		if (mobile != null) bonus += mobile.getAmplifier() * 0.2f;
		if (force != null) bonus += force.getAmplifier() * 0.1f;
		ans += MGConfig.COMMON.mechanicalArmMiscBonusFactor.get().floatValue() * bonus;
		return Mth.clamp(ans, 0.5f, 4f);
	}

	private static ItemStack fetch(MetalGolemEntity e, boolean simulate) {
		var opt = e.getCapability(Capabilities.ItemHandler.ENTITY);
		if (opt == null) return ItemStack.EMPTY;
		var mat = e.getMaterials().get(MetalGolemPartType.BODY.ordinal());
		Ingredient ing = GolemMaterialConfig.get().getRepairIngredient(mat.id());
		for (int i = 0; i < opt.getSlots(); i++) {
			ItemStack stack = opt.getStackInSlot(i);
			if (ing.test(stack)) {
				var take = opt.extractItem(i, 1, simulate);
				if (take.getCount() == 1)
					return stack;
			}
		}
		return ItemStack.EMPTY;
	}

}
