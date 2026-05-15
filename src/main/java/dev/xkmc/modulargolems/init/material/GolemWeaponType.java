package dev.xkmc.modulargolems.init.material;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemWeaponItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

import java.util.Locale;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public enum GolemWeaponType {
	SPEAR("long_weapon", (p, i, f) -> f.create(p, i, 0, 2, 0, 5), "TII", " SI", "S T"),
	AXE("battle_axe", (p, i, f) -> f.create(p, 0, i * 0.05, 0, 2, 10), "III", "IS ", "TST"),
	SWORD("sword", (p, i, f) -> f.create(p, i, 0, 1, 2, 0), "TII", "ISI", "SIT"),
	;

	private final IWeaponConstructor factory;
	private final String[] pattern;
	public final String model;

	GolemWeaponType(String model, IWeaponConstructor factory, String... pattern) {
		this.model = ModularGolems.loc(model).toString();
		this.factory = factory;
		this.pattern = pattern;
	}

	public String getName() {
		return "golem_" + name().toLowerCase(Locale.ROOT);
	}

	public ItemEntry<MetalGolemWeaponItem> buildItem(IGolemWeaponMaterial material) {
		return REGISTRATE.item(material.getName() + "_" + getName(), p ->
						factory.create(material.modify(p.stacksTo(1)), material.getDamage(), material.factory()))
				.model(() -> (ctx, pvd) ->
						pvd.generateFlatItem(ctx.get(), ModelTemplates.createItem(model, TextureSlot.LAYER0),
								new Material(material.modLoc("item/equipments/" + ctx.getName()))))
				.asOptional().tag(ItemTags.SWEEPING_ENCHANTABLE, ItemTags.SHARP_WEAPON_ENCHANTABLE)
				.defaultLang().register();
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
