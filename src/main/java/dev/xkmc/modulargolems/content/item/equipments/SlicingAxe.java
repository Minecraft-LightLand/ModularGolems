
package dev.xkmc.modulargolems.content.item.equipments;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.data.RecipeGen;
import dev.xkmc.modulargolems.init.material.GolemWeaponType;
import dev.xkmc.modulargolems.init.material.VanillaGolemWeaponMaterial;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class SlicingAxe extends MetalGolemWeaponItem {

	public SlicingAxe(Properties properties, int attackDamage, double percentAttack, float range, float sweep) {
		super(properties, attackDamage, percentAttack, range, sweep);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
		list.add(MGLangData.SLICING_GOLEM.get(Math.round(MGConfig.COMMON.slicingDropUpgradeChance.get() * 100) + "%"));
		list.add(MGLangData.SLICING_ENEMY.get());
		super.appendHoverText(stack, level, list, flag);
	}

	public static ItemEntry<SlicingAxe> buildItem(String id, VanillaGolemWeaponMaterial material) {
		return REGISTRATE.item(id, p -> new SlicingAxe(material.modify(p.stacksTo(1)),
						0, material.getDamage() * 0.05, 0, 2))
				.model((ctx, pvd) -> pvd.getBuilder(ctx.getName())
						.parent(new ModelFile.UncheckedModelFile(pvd.modLoc(GolemWeaponType.AXE.model)))
						.texture("layer0", pvd.modLoc("item/equipments/" + ctx.getName())))
				.recipe((ctx, pvd) -> RecipeGen.unlock(pvd,
						SmithingTransformRecipeBuilder.smithing(
								Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
								Ingredient.of(GolemItems.METALGOLEM_WEAPON[GolemWeaponType.AXE.ordinal()][VanillaGolemWeaponMaterial.DIAMOND.ordinal()]),
								Ingredient.of(Blocks.STONECUTTER), RecipeCategory.COMBAT, ctx.get()
						)::unlocks, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE).save(pvd, ctx.getName()))
				.defaultLang().register();
	}
}
