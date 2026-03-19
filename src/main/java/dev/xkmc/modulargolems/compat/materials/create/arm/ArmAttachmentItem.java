package dev.xkmc.modulargolems.compat.materials.create.arm;

import dev.xkmc.modulargolems.compat.materials.create.CreateCompatRegistry;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.item.ranged.ShouldWeaponItem;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
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
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		list.add(MGLangData.MECHANICAL_ARM.get());
		super.appendHoverText(stack, level, list, flag);
	}

	@Override
	public void onTick(MetalGolemEntity e, ItemStack stack, InteractionHand hand) {
		if (e.level().isClientSide()) return;
		var time = e.level().getGameTime();
		long last = stack.getOrCreateTag().getLong("FixAction");
		int takingItem = stack.getOrCreateTag().getInt("TakingItem");
		var speed = Mth.clamp(stack.getOrCreateTag().getFloat("FixSpeed"), 0.25f, 4f);
		if (last > time || last < time - (int) (80 / speed + 20)) {
			if (e.tickCount % 60 == (hand == InteractionHand.MAIN_HAND ? 20 : 40) &&
					(e.getHealth() <= e.getMaxHealth() * 0.75 || e.isReforged())) {
				var take = fetch(e, true);
				if (take.isEmpty()) return;
				stack.getOrCreateTag().putLong("FixAction", time);
				stack.getOrCreateTag().putInt("TakingItem", 2);
				stack.getOrCreateTag().putFloat("FixSpeed", getSpeed(e));

			}
		} else {
			if (takingItem == 2 && last >= time - (int) (40 / speed)) {
				stack.getOrCreateTag().putInt("TakingItem", 1);
				if (e.getHealth() > e.getMaxHealth() * 0.75 && !e.isReforged())
					stack.getOrCreateTag().putLong("FixAction", 0);
			}
			if (takingItem >= 1 && last >= time - (int) (60 / speed)) {
				stack.getOrCreateTag().remove("TakingItem");
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
		float ans = 0.5f;
		if (e.getModifiers().containsKey(CreateCompatRegistry.BODY.get()))
			ans += 0.2f;
		if (e.getPersistentData().getLong("MechEngineLastPoweredUp") > e.level().getGameTime()) {
			ans += 1f;
		}
		var mobile = e.getEffect(CreateCompatRegistry.EFF_MOBILE.get());
		var force = e.getEffect(CreateCompatRegistry.EFF_FORCE.get());
		if (mobile != null) ans += mobile.getAmplifier() * 0.2f;
		if (force != null) ans += force.getAmplifier() * 0.1f;
		return Mth.clamp(ans, 0.5f, 4f);
	}

	private static ItemStack fetch(MetalGolemEntity e, boolean simulate) {
		var opt = e.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
		if (opt.isEmpty()) return ItemStack.EMPTY;
		var mat = e.getMaterials().get(MetalGolemPartType.BODY.ordinal());
		Ingredient ing = GolemMaterialConfig.get().getRepairIngredient(mat.id());
		for (int i = 0; i < opt.get().getSlots(); i++) {
			ItemStack stack = opt.get().getStackInSlot(i);
			if (ing.test(stack)) {
				var take = opt.get().extractItem(i, 1, simulate);
				if (take.getCount() == 1)
					return stack;
			}
		}
		return ItemStack.EMPTY;
	}

}
