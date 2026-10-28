package dev.xkmc.modulargolems.compat.materials.create.arm;

import dev.xkmc.modulargolems.compat.materials.create.CreateCompatRegistry;
import dev.xkmc.modulargolems.content.client.weapon.ShoulderAnimData;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.ranged.IGenericShoulderWeapon;
import dev.xkmc.modulargolems.content.item.ranged.ShouldWeaponItem;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArmAttachmentItem extends ShouldWeaponItem implements IGenericShoulderWeapon {

	public static final ResourceLocation ID = CreateCompatRegistry.loc("arm");

	public ArmAttachmentItem(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable ResourceLocation getModelForHand(InteractionHand hand) {
		return ID;
	}

	@Override
	public @Nullable ResourceLocation getModel(int slot) {
		return ID;
	}

	@Override
	public List<ShoulderAnimData> getAnimationData(MetalGolemEntity user, ItemStack stack, InteractionHand hand) {
		return List.of();
	}

	@Override
	public List<ShoulderAnimData> getAnimationData(AbstractGolemEntity<?, ?> user, ItemStack stack, int slot) {
		return List.of();
	}

	@Override
	public ResourceLocation getModelTexture(AbstractGolemEntity<?, ?> entity, ItemStack stack, int slot) {
		var id = ForgeRegistries.ITEMS.getKey(this);
		assert id != null;
		return id.withPath(e -> "textures/equipments/" + e + ".png");
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		list.add(MGLangData.MECHANICAL_ARM.get());
		super.appendHoverText(stack, level, list, flag);
	}

	@Override
	public void onTick(MetalGolemEntity e, ItemStack stack, InteractionHand hand) {
		onTick(e, stack, hand == InteractionHand.MAIN_HAND ? 1 : -1);
	}

	@Override
	public void onTick(AbstractGolemEntity<?, ?> e, ItemStack stack, int slot) {
		var hand = slot < 0 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
		if (e.level().isClientSide()) return;
		var time = e.level().getGameTime();
		var tag = stack.getOrCreateTag();
		long last = tag.getLong("FixAction");
		int takingItem = tag.getInt("TakingItem");
		var speed = Mth.clamp(tag.getFloat("FixSpeed"), 0.25f, 4f);
		if (last > time || last < time - (int) (100 / speed)) {
			ItemStack other = ItemStack.EMPTY;
			// only the two shouldered golem type has an opposite shoulder to trade with
			if (e instanceof MetalGolemEntity metal)
				other = hand == InteractionHand.MAIN_HAND ? metal.getLeftShoulder().getItem() : metal.getRightShoulder().getItem();
			if (other.getItem() instanceof ArmAttachmentItem) {
				long prev = other.getOrCreateTag().getLong("FixAction");
				if (prev <= time && prev > time - 20) return;
			}
			if (e.getGuardedDataImpl() > e.getMaxHealth() * 0.75 && !e.isReforged()) return;
			var take = fetch(e, true);
			if (take.isEmpty()) return;
			tag.putLong("FixAction", time);
			tag.putInt("TakingItem", 2);
			tag.putFloat("FixSpeed", getSpeed(e));
			tag.put("DisplayItem", take.save(new CompoundTag()));
		} else {
			if (takingItem == 2 && last >= time - (int) (40 / speed)) {
				tag.putInt("TakingItem", 1);
				if (e.getGuardedDataImpl() > e.getMaxHealth() * 0.75 && !e.isReforged())
					tag.putLong("FixAction", 0);
			}
			if (takingItem >= 1 && last >= time - (int) (60 / speed)) {
				tag.remove("TakingItem");
				if (e.getGuardedDataImpl() <= e.getMaxHealth() * 0.75 || e.isReforged()) {
					var take = fetch(e, false);
					if (!take.isEmpty()) {
						e.repairWithItem();
					}
				}
			}
		}
	}

	public static float getSpeed(AbstractGolemEntity<?, ?> e) {
		float ans = MGConfig.COMMON.mechanicalArmSpeed.get().floatValue();
		if (e.getEffect(CreateCompatRegistry.EFF_MOBILE.get()) != null || e.getEffect(CreateCompatRegistry.EFF_FORCE.get()) != null) {
			ans += MGConfig.COMMON.mechanicalArmPowerBonus.get().floatValue();
		}
		float bonus = 0;
		if (e.getModifiers().containsKey(CreateCompatRegistry.BODY.get()))
			bonus += 0.2f;
		var mobile = e.getEffect(CreateCompatRegistry.EFF_MOBILE.get());
		var force = e.getEffect(CreateCompatRegistry.EFF_FORCE.get());
		if (mobile != null) bonus += mobile.getAmplifier() * 0.2f;
		if (force != null) bonus += force.getAmplifier() * 0.1f;
		ans += MGConfig.COMMON.mechanicalArmMiscBonusFactor.get().floatValue() * bonus;
		return Mth.clamp(ans, 0.5f, 4f);
	}

	private static ItemStack fetch(AbstractGolemEntity<?, ?> e, boolean simulate) {
		var opt = e.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
		if (opt.isEmpty()) return ItemStack.EMPTY;
		GolemType<?, ?> type = GolemType.getGolemType(e.getType());
		if (type == null) return ItemStack.EMPTY;
		var mat = e.getMaterials().get(type.getBodyPart().ordinal());
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
