package dev.xkmc.modulargolems.content.item.wand;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.xkmc.l2library.util.raytrace.IGlowingTarget;
import dev.xkmc.l2library.util.raytrace.RayTraceUtil;
import dev.xkmc.modulargolems.content.capability.GolemTracker;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.hostile.HostileGolemRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class HostileWandItem extends Item implements GolemInteractItem, IGlowingTarget {

	private static final int RANGE = 64;

	private final Multimap<Attribute, AttributeModifier> defaultModifiers;

	public HostileWandItem(Item.Properties properties) {
		super(properties);
		this.defaultModifiers = ImmutableMultimap.of(
				Attributes.ATTACK_DAMAGE,
				new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", WandCombat.WAND_ATTACK_DAMAGE, AttributeModifier.Operation.ADDITION),
				Attributes.ATTACK_SPEED,
				new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", WandCombat.BASE_ATTACK_SPEED, AttributeModifier.Operation.ADDITION));
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public int getEnchantmentValue() {
		return 15;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if (WandCombat.canApply(enchantment)) return true;
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (level.isClientSide() && selected && entity instanceof Player player) {
			RayTraceUtil.clientUpdateTarget(player, RANGE);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			LivingEntity target = RayTraceUtil.serverGetTarget(player);
			if (target != null) {
				interactLivingEntity(stack, player, target, hand);
			}
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public int getDistance(ItemStack itemStack) {
		return RANGE;
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity target, InteractionHand hand) {
		if (target instanceof AbstractGolemEntity<?, ?> golem) {
			if (user instanceof ServerPlayer sp) {
				if (!golem.isHostile()) {
					if (golem.getOwner() == user || sp.getAbilities().instabuild) {
						golem.untrack(GolemTracker.Status.OTHER_RETRIEVED, null);
						golem.setOwnerUUID(HostileGolemRegistry.DEFAULT.uuid);
					}
				} else if (sp.getAbilities().instabuild) {
					golem.setOwnerUUID(sp.getUUID());
				}
			}
		} else {
			target.addTag("HostileGolemTarget");
		}
		return InteractionResult.SUCCESS;
	}

}
