package dev.xkmc.modulargolems.content.item.ranged;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.xkmc.l2library.util.math.MathHelper;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.client.weapon.IEntityModelWeapon;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.IGolemEquipmentItem;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class MetalGolemBowItem extends BowItem implements IGolemEquipmentItem, IEntityModelWeapon {

	private final Multimap<Attribute, AttributeModifier> defaultModifiers;
	private final int baseline;

	public MetalGolemBowItem(Properties properties, int baseline, Consumer<ImmutableMultimap.Builder<Attribute, AttributeModifier>> attr) {
		super(properties);
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		attr.accept(builder);
		this.defaultModifiers = builder.build();
		this.baseline = baseline;
	}

	public MetalGolemBowItem(Properties properties, int baseline, int atk, Consumer<ImmutableMultimap.Builder<Attribute, AttributeModifier>> attr) {
		this(properties.stacksTo(1), baseline, b -> {
			if (atk > 0)
				b.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(MathHelper.getUUIDFromString("bow_melee"),
					"bow_melee", atk, AttributeModifier.Operation.ADDITION));
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

	@Override
	public AbstractArrow customArrow(AbstractArrow arrow) {
		var ans = super.customArrow(arrow);
		ans.setBaseDamage((ans.getBaseDamage() + 3) * baseline / 15f);
		ans.setPierceLevel((byte) (baseline / 10));
		return ans;
	}

	@Override
	public @Nullable ResourceLocation getModelForHand(InteractionHand hand) {
		return hand == InteractionHand.MAIN_HAND ? GolemModelPaths.BOW_MAINHAND : null;
	}

	@Override
	public ResourceLocation getModelTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		var id = ForgeRegistries.ITEMS.getKey(this);
		assert id != null;
		String suffix = shouldPlayAnimation(entity, stack, hand) ? "_pulling.png" : ".png";
		return id.withPath(e -> "textures/equipments/" + e + suffix);
	}

	@Override
	public boolean shouldPlayAnimation(LivingEntity user, ItemStack stack, InteractionHand hand) {
		return user.isUsingItem() && user.getUsedItemHand() == hand;
	}

	@Override
	public float getAnimationSpeed(LivingEntity user, ItemStack stack, InteractionHand hand) {
		return 10f / getPullTime(user);
	}

	@Override
	public float getAnimationTick(LivingEntity user, ItemStack stack, InteractionHand hand) {
		return 10f * user.getTicksUsingItem() / getPullTime(user);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		list.add(MGLangData.GOLEM_EQUIPMENT.get(GolemTypes.ENTITY_GOLEM.get().getDescription().copy().withStyle(ChatFormatting.GOLD))
				.withStyle(ChatFormatting.UNDERLINE));
		list.add(MGLangData.BOW_STIFFNESS.get(baseline + "").withStyle(ChatFormatting.BLUE));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		return InteractionResultHolder.pass(player.getItemInHand(hand));
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int time) {
	}

	@Override
	public boolean isFor(EntityType<?> type) {
		return type == GolemTypes.ENTITY_GOLEM.get();
	}

	@Override
	public EquipmentSlot getSlot() {
		return EquipmentSlot.MAINHAND;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getGolemModifiers(ItemStack stack, @Nullable Entity user, EquipmentSlot slot) {
		if (user != null && !isFor(user.getType()))
			return ImmutableMultimap.of();
		return stack.getAttributeModifiers(slot);
	}

	@Deprecated
	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		return slot == getSlot() ? defaultModifiers : super.getDefaultAttributeModifiers(slot);
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
		if (enchantment.category == EnchantmentCategory.BOW) {
			return true;
		}
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

}
