package dev.xkmc.modulargolems.compat.materials.twilightforest.equipments;

import dev.xkmc.modulargolems.compat.materials.twilightforest.TFCompatRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.IGolemModifierItem;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemWeaponItem;
import dev.xkmc.modulargolems.content.modifier.base.ModifierInstance;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KnightmetalMetalGolemWeaponItem extends MetalGolemWeaponItem implements IGolemModifierItem {

	public KnightmetalMetalGolemWeaponItem(Properties properties, int attackDamage, double percentAttack, float range, float sweep) {
		super(properties, attackDamage, percentAttack, range, sweep);
	}

	@Override
	public List<ModifierInstance> getModifier(ItemStack stack, @Nullable AbstractGolemEntity<?, ?> golem) {
		return List.of(
				new ModifierInstance(TFCompatRegistry.TF_DAMAGE.get(), 2),
				new ModifierInstance(GolemModifiers.ARMOR_BYPASS.get(), 2)
		);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		appendModifierText(stack, list);
	}

}
