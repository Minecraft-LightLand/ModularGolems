package dev.xkmc.modulargolems.compat.materials.iceandfire.proxy;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.ability.BuiltinAbilities;
import com.iafenvoy.iceandfire.registry.IafItems;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class IAFProxyCE implements IAFProxy {

	@Override
	public void fireHit(LivingEntity target, LivingEntity user, int level) {
		try {
			BuiltinAbilities.DRAGONSTEEL_FIRE_TOOL.active(ItemStack.EMPTY, target, user);
		} catch (Throwable e) {
			ModularGolems.LOGGER.throwing(e);
		}

	}

	@Override
	public void iceHit(LivingEntity target, LivingEntity user, int level) {
		try {
			BuiltinAbilities.DRAGONSTEEL_ICE_TOOL.active(ItemStack.EMPTY, target, user);
		} catch (Throwable e) {
			ModularGolems.LOGGER.throwing(e);
		}
	}

	@Override
	public void lightningHit(LivingEntity target, LivingEntity user, int level) {
		try {
			BuiltinAbilities.DRAGONSTEEL_LIGHTNING_TOOL.active(ItemStack.EMPTY, target, user);
		} catch (Throwable e) {
			ModularGolems.LOGGER.throwing(e);
		}

	}

	@Override
	public String modid() {
		return IceAndFire.MOD_ID;
	}

	@Override
	public Supplier<Item> ingotIceSteel() {
		return IafItems.DRAGONSTEEL_ICE_INGOT;
	}

	@Override
	public Supplier<Item> ingotFireSteel() {
		return IafItems.DRAGONSTEEL_FIRE_INGOT;
	}

	@Override
	public Supplier<Item> ingotLightningSteel() {
		return IafItems.DRAGONSTEEL_LIGHTNING_INGOT;
	}


}
