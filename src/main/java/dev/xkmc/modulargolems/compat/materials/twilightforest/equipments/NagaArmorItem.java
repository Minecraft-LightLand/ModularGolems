package dev.xkmc.modulargolems.compat.materials.twilightforest.equipments;

import com.google.common.collect.ImmutableMultimap;
import dev.xkmc.modulargolems.compat.materials.twilightforest.TFCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.twilightforest.TFDispatch;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.IGolemModifierItem;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.content.modifier.base.ModifierInstance;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class NagaArmorItem extends MetalGolemArmorItem implements IGolemModifierItem {

	public NagaArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, ResourceLocation model) {
		super(properties, type, defense, toughness, model);
	}

	@Override
	public List<ModifierInstance> getModifier(ItemStack stack, @Nullable AbstractGolemEntity<?, ?> golem) {
		return List.of(
				new ModifierInstance(TFCompatRegistry.TF_HEALING.get(), 2),
				new ModifierInstance(TFCompatRegistry.TF_DAMAGE.get(), 2)
		);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		appendModifierText(stack, list);
	}

	@Override
	protected String namespace(String def) {
		return TFDispatch.MODID;
	}

	@Override
	protected void addExtraModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
		super.addExtraModifiers(builder);
		UUID uuid = UUID.get(getSlot());
		builder.put(GolemTypes.GOLEM_REGEN.get(), new AttributeModifier(uuid,
				"Naga Armor", 0.5, AttributeModifier.Operation.ADDITION));
		switch (getSlot()) {
			case CHEST -> builder.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(uuid,
					"Naga Armor", 1, AttributeModifier.Operation.ADDITION));
			case LEGS -> builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid,
					"Naga Armor", 0.2, AttributeModifier.Operation.MULTIPLY_BASE));
		}

	}

	@Override
	public ResourceLocation getModelTexture(LivingEntity user) {
		return new ResourceLocation(TFDispatch.MODID, "textures/equipments/naga.png");
	}

}
