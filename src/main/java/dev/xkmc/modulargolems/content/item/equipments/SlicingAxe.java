package dev.xkmc.modulargolems.content.item.equipments;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.data.RecipeGen;
import dev.xkmc.modulargolems.init.material.GolemWeaponType;
import dev.xkmc.modulargolems.init.material.VanillaGolemWeaponMaterial;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class SlicingAxe extends MetalGolemWeaponItem implements CustomDropGolemWeapon {

	public SlicingAxe(Properties properties, int attackDamage, double percentAttack, float range, float sweep) {
		super(properties, attackDamage, percentAttack, range, sweep);
	}

	@Override
	public boolean dropCustomDeathLoot(AbstractGolemEntity<?, ?> self, MetalGolemEntity attacker, ItemStack stack, DamageSource source) {
		if (attacker.isHostile()) return false;
		var rate = MGConfig.COMMON.slicingDropUpgradeChance.get();
		var random = self.getRandom();
		if (self.isHostile()) {
			var mats = self.getMaterials();
			var mat = mats.get(random.nextInt(mats.size()));
			self.spawnAtLocation(GolemPart.setMaterial(mat.part().getDefaultInstance(), mat.id()));
			var upgrades = self.getUpgrades();
			if (!upgrades.isEmpty()) {
				var upgrade = upgrades.get(random.nextInt(upgrades.size()));
				if (random.nextFloat() < rate) {
					self.spawnAtLocation(upgrade.getDefaultInstance());
				}
			}
		} else {
			for (GolemMaterial mat : self.getMaterials()) {
				self.spawnAtLocation(GolemPart.setMaterial(mat.part().getDefaultInstance(), mat.id()));
			}
			for (var e : self.getUpgrades()) {
				if (random.nextFloat() < rate) {
					self.spawnAtLocation(e.getDefaultInstance());
				}
			}
		}
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		list.add(MGLangData.SLICING_GOLEM.get(Math.round(MGConfig.COMMON.slicingDropUpgradeChance.get() * 100) + "%"));
		list.add(MGLangData.SLICING_ENEMY.get());
		super.appendHoverText(stack, level, list, flag);
	}

	public static ItemEntry<SlicingAxe> buildItem(String id, VanillaGolemWeaponMaterial material) {
		return REGISTRATE.item(id, p -> new SlicingAxe(material.modify(p.stacksTo(1)),
						0, material.getDamage() * 0.05, 0, 2))
				.model((ctx, pvd) ->
						pvd.getBuilder(ctx.getName())
								.guiLight(BlockModel.GuiLight.FRONT)
								.customLoader(SeparateTransformsModelBuilder::begin)
								.base(material.model(new ItemModelBuilder(null, pvd.existingFileHelper)
										.parent(new ModelFile.UncheckedModelFile(ModularGolems.loc(GolemWeaponType.AXE.model)))
										.texture("layer0", pvd.modLoc("item/equipments/" + ctx.getName()))))
								.perspective(ItemDisplayContext.GUI, material.model(new ItemModelBuilder(null, pvd.existingFileHelper)
										.parent(pvd.getExistingFile(pvd.mcLoc("item/generated")))
										.texture("layer0", pvd.modLoc("item/equipments/" + ctx.getName() + "_icon")))))
				.recipe((ctx, pvd) -> RecipeGen.unlock(pvd,
						SmithingTransformRecipeBuilder.smithing(
								Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
								Ingredient.of(GolemItems.METALGOLEM_WEAPON[GolemWeaponType.AXE.ordinal()][VanillaGolemWeaponMaterial.DIAMOND.ordinal()]),
								Ingredient.of(Blocks.STONECUTTER), RecipeCategory.COMBAT, ctx.get()
						)::unlocks, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE).save(pvd, ctx.getName()))
				.tag(MGTagGen.SHIELD_BREAKER_WEAPONS)
				.defaultLang().register();
	}
}
