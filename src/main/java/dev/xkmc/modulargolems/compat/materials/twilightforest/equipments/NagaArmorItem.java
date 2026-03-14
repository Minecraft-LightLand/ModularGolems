package dev.xkmc.modulargolems.compat.materials.twilightforest.equipments;

import dev.xkmc.modulargolems.compat.materials.twilightforest.TFCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.twilightforest.TFDispatch;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.IGolemModifierItem;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.content.modifier.base.ModifierInstance;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NagaArmorItem extends MetalGolemArmorItem implements IGolemModifierItem {

	public NagaArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, ResourceLocation model) {
		super(properties, type, defense, toughness, model, e -> {
			var uuid = TFCompatRegistry.tfLoc("naga_armor_" + type.getName());
			e.add(GolemTypes.GOLEM_REGEN, new AttributeModifier(uuid, 0.5,
					AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()));
			switch (type.getSlot()) {
				case CHEST -> e.add(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(uuid, 1,
						AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()));
				case LEGS -> e.add(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, 0.2,
						AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.bySlot(type.getSlot()));
			}
		});
	}

	@Override
	public List<ModifierInstance> getModifier(ItemStack stack, @Nullable AbstractGolemEntity<?, ?> golem) {
		return List.of(
				new ModifierInstance(TFCompatRegistry.TF_HEALING.get(), 2),
				new ModifierInstance(TFCompatRegistry.TF_DAMAGE.get(), 2)
		);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		appendModifierText(stack, list);
	}

	@Override
	protected String namespace(String def) {
		return TFDispatch.MODID;
	}

	@Override
	public ResourceLocation getModelTexture(LivingEntity user) {
		return TFCompatRegistry.tfLoc("textures/equipments/naga.png");
	}

}
