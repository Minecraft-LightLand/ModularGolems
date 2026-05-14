package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.l2core.init.reg.ench.EnchHelper;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.client.weapon.IEntityModelWeapon;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.IGolemEquipmentItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MetalGolemBowItem extends BowItem implements IGolemEquipmentItem, IEntityModelWeapon {

	private final int baseline;

	public MetalGolemBowItem(Properties properties, int baseline, Consumer<ItemAttributeModifiers.Builder> attr) {
		super(properties.stacksTo(1).enchantable(15).attributes(Util.make(ItemAttributeModifiers.builder(), attr).build()));
		this.baseline = baseline;
	}

	public MetalGolemBowItem(Properties properties, int baseline, int atk, Consumer<ItemAttributeModifiers.Builder> attr) {
		this(properties, baseline, b -> {
			if (atk > 0)
				b.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ModularGolems.loc("bow_melee"),
						atk, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
			attr.accept(b);
		});
	}

	public MetalGolemBowItem(Properties properties, int baseline, int atk) {
		this(properties, baseline, atk, b -> {
		});
	}

	public float getPower(LivingEntity user, int time) {
		return Math.min(1, 1f * time / getPullTime(user));
	}

	public int getPullTime(LivingEntity user) {
		var val = user.getAttributeValue(Attributes.ATTACK_DAMAGE);
		val = Mth.clamp(0.5, val / baseline, 4);
		return (int) (40 / val);
	}

	protected int getPiercing(ItemStack stack, @Nullable MetalGolemEntity e) {
		return baseline / 10 + EnchHelper.getLv(stack, Enchantments.PIERCING);
	}

	@Override
	public AbstractArrow customArrow(AbstractArrow arrow, ItemStack ammo, ItemStack bow) {
		var ans = super.customArrow(arrow, ammo, bow);
		if (ans.getOwner() instanceof MetalGolemEntity e) {
			var p = BowPoseUtil.getOrigin(e);
			ans.setPos(p);
			ans.setBaseDamage((ans.baseDamage + 3) * baseline / 15f);
			if (e.getMainHandItem().getItem() == this) {
				ans.setPierceLevel((byte) getPiercing(e.getMainHandItem(), e));
			}
		}
		return ans;
	}

	@Override
	public @Nullable Identifier getModelForHand(HumanoidArm hand) {
		return hand == HumanoidArm.RIGHT ? GolemModelPaths.BOW_MAINHAND : GolemModelPaths.BOW_OFFHAND;
	}

	@Override
	public Identifier getModelTexture(MetalGolemEntity entity, ItemStack stack, HumanoidArm hand) {
		var id = BuiltInRegistries.ITEM.getKey(this);
		String suffix = shouldPlayAnimation(entity, stack, hand) ? "_pulling.png" : ".png";
		return id.withPath(e -> "textures/equipments/" + e + suffix);
	}

	@Override
	public boolean shouldPlayAnimation(MetalGolemEntity user, ItemStack stack, HumanoidArm arm) {
		if (user.isLeftHanded()) arm = arm.getOpposite();
		var hand = arm == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		return user.isUsingItem() && user.getUsedItemHand() == hand;
	}

	@Override
	public float getAnimationSpeed(MetalGolemEntity user, ItemStack stack, HumanoidArm hand) {
		return 10f / getPullTime(user);
	}

	@Override
	public float getAnimationTick(MetalGolemEntity user, ItemStack stack, HumanoidArm hand) {
		return 10f * user.getTicksUsingItem() / getPullTime(user);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> list, TooltipFlag tooltipFlag) {
		list.accept(MGLangData.GOLEM_EQUIPMENT.get(GolemTypes.ENTITY_GOLEM.get().getDescription().copy().withStyle(ChatFormatting.GOLD))
				.withStyle(ChatFormatting.UNDERLINE));
		list.accept(MGLangData.BOW_STIFFNESS.get(baseline + "").withStyle(ChatFormatting.BLUE));
		int pierce = getPiercing(stack, null);
		list.accept(MGLangData.BOW_PIERCE.get(pierce + "").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		return InteractionResult.PASS;
	}

	@Override
	public boolean releaseUsing(ItemStack stack, Level level, LivingEntity user, int time) {
		return false;
	}

	@Override
	public boolean isFor(EntityType<?> type) {
		return type == GolemTypes.ENTITY_GOLEM.get();
	}

	@Override
	public EquipmentSlot getSlot() {
		return EquipmentSlot.MAINHAND;
	}

	public void forEachModifier(ItemStack stack, Entity entity, EquipmentSlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> action) {
		if (slot != getSlot()) return;
		if (!isFor(entity.getType())) return;
		stack.getAttributeModifiers().forEach(slot, action);
	}

	@Override
	public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
		if (enchantment.is(Enchantments.PIERCING))
			return true;
		return super.isPrimaryItemFor(stack, enchantment);
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		if (enchantment.is(Enchantments.PIERCING))
			return true;
		return super.supportsEnchantment(stack, enchantment);
	}

	@Override
	public boolean canBeHurtBy(ItemStack stack, DamageSource source) {
		return !getDefaultInstance().is(MGTagGen.TOUGH_ITEM) || !(
				source.is(DamageTypeTags.IS_FIRE) ||
						source.is(DamageTypeTags.IS_EXPLOSION) ||
						source.is(DamageTypeTags.IS_LIGHTNING));
	}

}
