package dev.xkmc.modulargolems.content.item.equipments;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.material.GolemWeaponType;
import dev.xkmc.modulargolems.init.material.VanillaGolemWeaponMaterial;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class SlicingAxe extends MetalGolemWeaponItem implements CustomDropGolemWeapon {

	public SlicingAxe(Properties properties, int attackDamage, double percentAttack, float range, float sweep) {
		super(properties, attackDamage, percentAttack, range, sweep, 10);
	}

	@Override
	public boolean dropCustomDeathLoot(ServerLevel sl, AbstractGolemEntity<?, ?> self, MetalGolemEntity attacker, ItemStack stack, DamageSource source) {
		if (attacker.isHostile()) return false;
		double rate = MGConfig.COMMON.slicingDropUpgradeChance.get();
		var random = self.getRandom();
		if (self.isHostile()) {
			var mats = self.getMaterials();
			var mat = mats.get(random.nextInt(mats.size()));
			self.spawnAtLocation(sl, GolemPart.setMaterial(mat.part().getDefaultInstance(), mat.id()));
			var upgrades = self.getUpgrades().upgrades();
			if (!upgrades.isEmpty()) {
				var upgrade = upgrades.get(random.nextInt(upgrades.size()));
				if (random.nextFloat() < rate) {
					self.spawnAtLocation(sl, upgrade.getDefaultInstance());
				}
			}
		} else {
			for (GolemMaterial mat : self.getMaterials()) {
				self.spawnAtLocation(sl, GolemPart.setMaterial(mat.part().getDefaultInstance(), mat.id()));
			}
			for (var e : self.getUpgrades().upgrades()) {
				if (random.nextFloat() < rate) {
					self.spawnAtLocation(sl, e.getDefaultInstance());
				}
			}
		}
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, TooltipDisplay disp, Consumer<Component> list, TooltipFlag flag) {
		list.accept(MGLangData.SLICING_GOLEM.get(Math.round(MGConfig.COMMON.slicingDropUpgradeChance.get() * 100) + "%"));
		list.accept(MGLangData.SLICING_ENEMY.get());
		super.appendHoverText(stack, level, disp, list, flag);
	}

	public static ItemEntry<SlicingAxe> buildItem(String id, VanillaGolemWeaponMaterial material) {
		return REGISTRATE.item(id, p -> new SlicingAxe(material.modify(p.stacksTo(1)),
						0, material.getDamage() * 0.05, 0, 2))
				.model(() -> (ctx, pvd) ->
						pvd.generateFlatItem(ctx.get(), ModelTemplates.createItem(GolemWeaponType.AXE.model, TextureSlot.LAYER0),
								new Material(material.modLoc("item/equipments/" + ctx.getName()))))
				.tag(ItemTags.SWEEPING_ENCHANTABLE, ItemTags.SHARP_WEAPON_ENCHANTABLE)
				.defaultLang().register();
	}
}
