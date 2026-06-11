package dev.xkmc.modulargolems.compat.materials.goety.revelation;

import com.Polarice3.Goety.utils.SEHelper;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.IAttackListenerWeapon;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemWeaponItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class ApollyonSword extends MetalGolemWeaponItem implements IAttackListenerWeapon {

	public ApollyonSword(Properties properties, int attackDamage, double percentAttack, float range, float sweep) {
		super(properties, attackDamage, percentAttack, range, sweep);
	}

	@Override
	public void onAttack(AttackCache cache, DamageSource source, MetalGolemEntity e, ItemStack stack) {
		var target = cache.getAttackTarget();
		target.invulnerableTime = 0;
		float hp = target.getHealth();
		var id = target.getUUID();
		var old = stack.getOrCreateTag().getUUID("TargetUUID");
		if (old.equals(id))
			hp = Math.min(hp, stack.getOrCreateTag().getFloat("TargetHealth"));
		stack.getOrCreateTag().putFloat("TargetHealth", hp);
	}

	@Override
	public void onDamage(AttackCache cache, DamageSource source, MetalGolemEntity e, ItemStack stack) {
		float diff = cache.getAttackTarget().getHealth() - stack.getOrCreateTag().getFloat("TargetHealth");
		var dmg = Math.max(
				e.getAttributeValue(Attributes.ATTACK_DAMAGE),
				Math.max(cache.getPreDamageOriginal(), cache.getPreDamage()));
		var dmg2 = Math.max(dmg, Math.sqrt(dmg * cache.getAttackTarget().getMaxHealth()));
		cache.addDealtModifier(DamageModifier.nonlinearMiddle(81, x -> {
			var ans = Math.max(x, (float) dmg2) + Math.max(diff, 0);
			if (e.getOwner() != null)
				SEHelper.increaseSouls(e.getOwner(), (int) ans);
			return ans;
		}));

	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		list.add(MGLangData.APOCALYPTIUM_SWORD.get());
	}

	public static ItemEntry<ApollyonSword> buildItem(String id) {
		return REGISTRATE.item(id, p -> new ApollyonSword(p.stacksTo(1).fireResistant(),
						0, 1, 2, 2))
				.model((ctx, pvd) ->
						pvd.getBuilder(ctx.getName())
								.guiLight(BlockModel.GuiLight.FRONT)
								.customLoader(SeparateTransformsModelBuilder::begin)
								.base(new ItemModelBuilder(null, pvd.existingFileHelper)
										.parent(new ModelFile.UncheckedModelFile(ModularGolems.loc("custom/" + id)))
										.texture("tex", GRCompatRegistry.grLoc("item/equipments/" + ctx.getName())))
								.perspective(ItemDisplayContext.GUI, new ItemModelBuilder(null, pvd.existingFileHelper)
										.parent(pvd.getExistingFile(pvd.mcLoc("item/generated")))
										.texture("layer0", GRCompatRegistry.grLoc("item/equipments/" + ctx.getName() + "_icon"))))
				.tag(MGTagGen.SHIELD_BREAKER_WEAPONS)
				.defaultLang().register();
	}

}
