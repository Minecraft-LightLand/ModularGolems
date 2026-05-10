package dev.xkmc.modulargolems.compat.materials.twilightforest.equipments;

import dev.xkmc.modulargolems.compat.materials.twilightforest.TFCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.twilightforest.TFDispatch;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.IGolemModifierItem;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.content.modifier.base.ModifierInstance;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KnightmetalArmorItem extends MetalGolemArmorItem implements IGolemModifierItem {

	public KnightmetalArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, Identifier model) {
		super(properties, type, defense, toughness, model);
	}

	@Override
	public List<ModifierInstance> getModifier(ItemStack stack, @Nullable AbstractGolemEntity<?, ?> golem) {
		return List.of(
				new ModifierInstance(TFCompatRegistry.TF_DAMAGE.get(), 1),
				new ModifierInstance(GolemModifiers.THORN.get(), 1)
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
	public Identifier getModelTexture(LivingEntity user) {
		return TFCompatRegistry.tfLoc("textures/equipments/knightmetal.png");
	}
}
