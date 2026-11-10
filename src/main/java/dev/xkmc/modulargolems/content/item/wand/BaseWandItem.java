package dev.xkmc.modulargolems.content.item.wand;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2itemselector.select.item.CustomDisplaySelectItem;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BaseWandItem extends Item implements CustomDisplaySelectItem {

	@Nullable
	private final ItemEntry<? extends BaseWandItem> base;
	@Nullable
	private final MGLangData right, shift;
	/**
	 * 主手时生效的属性修饰器，在构造期算好。万能手杖是基础手杖加上模式，基础手杖传 null 的 base。
	 */
	private final Multimap<Attribute, AttributeModifier> defaultModifiers;

	public BaseWandItem(Properties properties, @Nullable MGLangData right, @Nullable MGLangData shift, @Nullable ItemEntry<? extends BaseWandItem> base) {
		super(properties);
		this.base = base;
		this.right = right;
		this.shift = shift;
		double damage = base == null ? WandCombat.WAND_ATTACK_DAMAGE : WandCombat.OMNI_ATTACK_DAMAGE;
		this.defaultModifiers = ImmutableMultimap.of(
				Attributes.ATTACK_DAMAGE,
				new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", damage, AttributeModifier.Operation.ADDITION),
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
	public ItemStack getDisplay(ResourceLocation id, ItemStack stack) {
		return base == null ? stack : base.asStack();
	}

	/**
	 * Entry point for the omnipotent wand keys, which mirror the wand's right click. Wands whose action needs
	 * an entity target resolve it here instead of in {@code use}, so that right clicking empty space keeps
	 * doing nothing exactly as before.
	 */
	public void useFromKey(Level level, Player user, InteractionHand hand) {
		use(level, user, hand);
	}

	@Override
	public final void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		if (base != null) {
			list.add(MGLangData.WAND_MODE.get(base.asStack().getHoverName()));
			list.add(MGLangData.WAND_SWITCH.get());
		}
		if (right != null) {
			list.add(MGLangData.WAND_RIGHT.get());
			list.add(right.get());
		}
		if (shift != null) {
			list.add(MGLangData.WAND_SHIFT.get());
			list.add(shift.get());
		}
	}
}
