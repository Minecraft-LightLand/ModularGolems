package dev.xkmc.modulargolems.compat.materials.goety.revelation;

import com.google.common.collect.ImmutableMultimap;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ApollyonArmorItem extends MetalGolemArmorItem {

	public ApollyonArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, ResourceLocation model) {
		super(properties, type, defense, toughness, model);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
	}

	@Override
	protected String namespace(String def) {
		return GRDispatch.MODID;
	}

	@Override
	protected void addExtraModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
		super.addExtraModifiers(builder);
		UUID uuid = UUID.get(getSlot());
		builder.put(L2DamageTracker.ABSORB.get(), new AttributeModifier(uuid, "Apollyon Armor", 1, AttributeModifier.Operation.ADDITION));
		builder.put(L2DamageTracker.REDUCTION.get(), new AttributeModifier(uuid, "Apollyon Armor", -0.2, AttributeModifier.Operation.MULTIPLY_TOTAL));
		builder.put(GolemTypes.GOLEM_REGEN.get(), new AttributeModifier(uuid, "Apollyon Armor", 1, AttributeModifier.Operation.ADDITION));
	}

}
