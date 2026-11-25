package dev.xkmc.modulargolems.compat.materials.l2complements;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2complements.init.data.LCConfig;
import dev.xkmc.l2complements.init.registrate.LCEnchantments;
import dev.xkmc.l2complements.init.registrate.LCItems;
import dev.xkmc.l2core.serial.config.ConfigDataProvider;
import dev.xkmc.l2core.serial.ingredients.EnchantmentIngredient;
import dev.xkmc.l2core.serial.recipe.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.events.event.GolemCollectInventoryEvent;
import dev.xkmc.modulargolems.events.event.GolemSweepEvent;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class LCDispatch extends ModDispatch {

	public static final String MODID = "l2complements";

	public LCDispatch() {
		super(() -> LCClient::new);
		LCCompatRegistry.register();
		NeoForge.EVENT_BUS.register(LCDispatch.class);
	}

	public void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".totemic_gold", "Totemic Gold");
		pvd.add("golem_material." + MODID + ".poseidite", "Poseidite");
		pvd.add("golem_material." + MODID + ".shulkerate", "Shulkerate");
		pvd.add("golem_material." + MODID + ".eternium", "Eternium");
	}

	/**
	 * 末影迁移：傀儡主手武器附魔了 ender_transport 时，击杀掉落优先塞进傀儡能访问的容器
	 * （背包/空间背包等通过 {@link GolemCollectInventoryEvent} 提供），塞不下就留在原地。
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onGolemEnderReach(LivingDropsEvent event) {
		if (!(event.getSource().getEntity() instanceof AbstractGolemEntity<?, ?> golem)) return;
		ItemStack weapon = golem.getMainHandItem();
		if (LCEnchantments.ENDER_TRANSPORT.getLv(weapon) <= 0) return;

		ArrayList<IItemHandlerModifiable> inventories = new ArrayList<>();
		NeoForge.EVENT_BUS.post(new GolemCollectInventoryEvent(golem, inventories));

		for (ItemEntity drop : event.getDrops()) {
			ItemStack remain = drop.getItem();
			for (IItemHandlerModifiable inv : inventories) {
				remain = ItemHandlerHelper.insertItem(inv, remain, false);
				if (remain.isEmpty()) break;
			}
			if (remain.isEmpty()) {
				drop.setItem(ItemStack.EMPTY);
			} else {
				// 没有容器 / 容器放不下：掉在傀儡脚下，而不是死亡地点
				drop.setItem(remain);
				drop.moveTo(golem.getX(), golem.getY(), golem.getZ());
			}
		}
		event.getDrops().removeIf(e -> e.getItem().isEmpty());
	}

	/** 末影迁移也把经验掉落转移到傀儡位置。 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onGolemEnderReachExp(LivingExperienceDropEvent event) {
		if (!(event.getEntity().getLastHurtByMob() instanceof AbstractGolemEntity<?, ?> golem)) return;
		ItemStack weapon = golem.getMainHandItem();
		if (LCEnchantments.ENDER_TRANSPORT.getLv(weapon) <= 0) return;
		int exp = event.getDroppedExperience();
		if (exp <= 0) return;
		event.setDroppedExperience(0);
		ExperienceOrb.award((ServerLevel) golem.level(), golem.position(), exp);
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {

		safeUpgrade(pvd, new ShapelessRecipeBuilder(RecipeCategory.MISC, LCCompatRegistry.FORCE_FIELD.get(), 1)::unlockedBy, LCItems.FORCE_FIELD.get())
				.requires(GolemItems.EMPTY_UPGRADE.get())
				.requires(LCItems.FORCE_FIELD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LCCompatRegistry.FREEZE_UP.get())::unlockedBy, LCItems.HARD_ICE.get())
				.pattern("CAC").pattern("1B2").pattern("CAC")
				.define('C', Items.GOLD_INGOT)
				.define('A', LCItems.HARD_ICE.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.define('1', EnchantmentIngredient.of(pvd.getProvider(), LCEnchantments.FREEZING_THORN.id(), 1))
				.define('2', EnchantmentIngredient.of(pvd.getProvider(), LCEnchantments.FREEZING_BLADE.id(), 1))
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LCCompatRegistry.FLAME_UP.get())::unlockedBy, LCItems.SOUL_FLAME.get())
				.pattern("CAC").pattern("1B2").pattern("CAC")
				.define('C', Items.GOLD_INGOT)
				.define('A', LCItems.SOUL_FLAME.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.define('1', EnchantmentIngredient.of(pvd.getProvider(), LCEnchantments.HELLFIRE_THORN.id(), 1))
				.define('2', EnchantmentIngredient.of(pvd.getProvider(), LCEnchantments.HELLFIRE_BLADE.id(), 1))
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, new ShapelessRecipeBuilder(RecipeCategory.MISC, LCCompatRegistry.TELEPORT_UP.get(), 1)::unlockedBy, LCItems.VOID_EYE.get())
				.requires(GolemItems.EMPTY_UPGRADE.get())
				.requires(LCItems.VOID_EYE.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, new ShapelessRecipeBuilder(RecipeCategory.MISC, LCCompatRegistry.ATK_UP.get(), 1)::unlockedBy, LCItems.EXPLOSION_SHARD.get())
				.requires(GolemItems.QUARTZ.get())
				.requires(LCItems.EXPLOSION_SHARD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, new ShapelessRecipeBuilder(RecipeCategory.MISC, LCCompatRegistry.SPEED_UP.get(), 1)::unlockedBy, LCItems.CAPTURED_WIND.get())
				.requires(GolemItems.SPEED.get())
				.requires(LCItems.CAPTURED_WIND.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LCCompatRegistry.UPGRADE_CURSE.get())::unlockedBy, LCItems.CURSED_DROPLET.get())
				.pattern("A1A").pattern("CBC").pattern("A2A")
				.define('C', Items.DRAGON_BREATH)
				.define('A', LCItems.CURSED_DROPLET.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.define('1', EnchantmentIngredient.of(pvd.getProvider(), LCEnchantments.CURSE_BLADE.id(), 1))
				.define('2', Items.NETHER_STAR)
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LCCompatRegistry.UPGRADE_INCARCERATE.get())::unlockedBy, LCItems.BLACKSTONE_CORE.get())
				.pattern("A1A").pattern("CBC").pattern("A2A")
				.define('C', Items.DRAGON_BREATH)
				.define('A', LCItems.BLACKSTONE_CORE.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.define('1', EnchantmentIngredient.of(pvd.getProvider(), Enchantments.BINDING_CURSE, 1))
				.define('2', Items.NETHER_STAR)
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LCCompatRegistry.UPGRADE_CLEANSE.get())::unlockedBy, LCItems.LIFE_ESSENCE.get())
				.pattern("A1A").pattern("CBC").pattern("A2A")
				.define('C', Items.DRAGON_BREATH)
				.define('A', LCItems.LIFE_ESSENCE.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.define('1', EnchantmentIngredient.of(pvd.getProvider(), LCEnchantments.CURSE_BLADE.id(), 1))
				.define('2', Items.HEART_OF_THE_SEA)
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));
	}

	@Override
	public ConfigDataProvider getDataGen(DataGenerator gen, CompletableFuture<HolderLookup.Provider> pvd) {
		return new LCConfigGen(gen, pvd);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onGolemSweep(GolemSweepEvent event) {
		int lv = event.getStack().getEnchantmentLevel(LCEnchantments.WIND_SWEEP.holder());
		if (lv > 0) {
			double amount = LCConfig.SERVER.windSweepIncrement.get();
			event.setBox(event.getBox().inflate(amount * lv));
		}
	}

}
