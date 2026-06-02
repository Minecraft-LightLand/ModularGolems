package dev.xkmc.modulargolems.init.material;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemWeaponItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.loaders.SeparateTransformsModelBuilder;

import java.util.Locale;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public enum GolemWeaponType {
	SPEAR("item/long_weapon", (p, i, f) -> f.create(p, i, 0, 2, 0), "TII", " SI", "S T"),
	AXE("item/battle_axe", (p, i, f) -> f.create(p, 0, i * 0.05, 0, 2), "III", "IS ", "TST"),
	SWORD("item/sword", (p, i, f) -> f.create(p, i, 0, 1, 2), "TII", "ISI", "SIT"),
	;

	private final IWeaponConstructor factory;
	private final String[] pattern;
	public final String model;

	GolemWeaponType(String model, IWeaponConstructor factory, String... pattern) {
		this.model = model;
		this.factory = factory;
		this.pattern = pattern;
	}

	public String getName() {
		return "golem_" + name().toLowerCase(Locale.ROOT);
	}

	public ItemEntry<MetalGolemWeaponItem> buildItem(IGolemWeaponMaterial material) {
		var builder = REGISTRATE.item(material.getName() + "_" + getName(),
				p -> factory.create(material.modify(p.stacksTo(1)), material.getDamage(), material.factory()));
		if (material.hasIcon(this)) {
			builder.model((ctx, pvd) ->
					pvd.getBuilder(ctx.getName())
							.guiLight(BlockModel.GuiLight.FRONT)
							.customLoader(SeparateTransformsModelBuilder::begin)
							.base(material.model(new ItemModelBuilder(null, pvd.existingFileHelper)
									.parent(new ModelFile.UncheckedModelFile(ModularGolems.loc(model)))
									.texture("layer0", pvd.modLoc("item/equipments/" + ctx.getName()))))
							.perspective(ItemDisplayContext.GUI, material.model(new ItemModelBuilder(null, pvd.existingFileHelper)
									.parent(pvd.getExistingFile(pvd.mcLoc("item/generated")))
									.texture("layer0", pvd.modLoc("item/equipments/" + ctx.getName() + "_icon")))));
		} else {
			builder.model((ctx, pvd) ->
					material.model(pvd.getBuilder(ctx.getName()))
							.parent(new ModelFile.UncheckedModelFile(pvd.modLoc(model)))
							.texture("layer0", material.modLoc("item/equipments/" + ctx.getName())));
		}
		var ans = builder.defaultLang().register();
		if (this != SWORD) {
			MGTagGen.OPTIONAL_ITEM.add(pvd -> pvd.addTag(MGTagGen.SHIELD_BREAKER_WEAPONS)
					.addOptional(ans.getId()));
		}
		return ans;
	}

	public static ItemEntry<MetalGolemWeaponItem>[][] build(IGolemWeaponMaterial[] values) {
		ItemEntry<MetalGolemWeaponItem>[][] ans = new ItemEntry[GolemWeaponType.values().length][values.length];
		for (int i = 0; i < GolemWeaponType.values().length; i++) {
			GolemWeaponType type = GolemWeaponType.values()[i];
			for (int j = 0; j < values.length; j++) {
				IGolemWeaponMaterial mat = values[j];
				ans[i][j] = type.buildItem(mat);
			}
		}
		return ans;
	}

	public ShapedRecipeBuilder pattern(ShapedRecipeBuilder unlock) {
		for (String str : pattern) {
			unlock.pattern(str);
		}
		return unlock;
	}

	interface IWeaponConstructor {

		MetalGolemWeaponItem create(Item.Properties prop, int val, IGolemWeaponFactory factory);

	}

}
