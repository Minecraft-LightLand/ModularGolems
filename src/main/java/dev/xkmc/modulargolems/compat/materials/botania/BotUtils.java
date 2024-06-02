package dev.xkmc.modulargolems.compat.materials.botania;

import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.curios.api.CuriosApi;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.mana.ManaItem;

public class BotUtils {

	public static int getMana(LivingEntity e) {
		var opt = CuriosApi.getCuriosInventory(e).resolve();
		if (opt.isEmpty()) return 0;
		var list = opt.get().findCurios(stack -> stack.getCapability(BotaniaForgeCapabilities.MANA_ITEM).resolve().isPresent());
		int total = 0;
		for (var item : list) {
			var optMana = item.stack().getCapability(BotaniaForgeCapabilities.MANA_ITEM).resolve();
			if (optMana.isEmpty()) continue;
			ManaItem manaItem = optMana.get();
			total += manaItem.getMana();
		}
		return total;
	}

	/**
	 * returns amount actually consumed
	 * s
	 */
	public static int consumeMana(LivingEntity e, int target) {
		var opt = CuriosApi.getCuriosInventory(e).resolve();
		if (opt.isEmpty()) return 0;
		var list = opt.get().findCurios(stack -> stack.getCapability(BotaniaForgeCapabilities.MANA_ITEM).resolve().isPresent());
		int consumed = 0;
		for (var item : list) {
			var optMana = item.stack().getCapability(BotaniaForgeCapabilities.MANA_ITEM).resolve();
			if (optMana.isEmpty()) continue;
			ManaItem manaItem = optMana.get();
			int remainMana = manaItem.getMana();
			if (remainMana <= 0) continue;
			int cost = Math.min(remainMana, target);
			consumed += cost;
			target -= cost;
			manaItem.addMana(-cost);
			if (target == 0) break;
		}
		return consumed;
	}

	/**
	 * return amount remains
	 */
	public static int generateMana(LivingEntity e, int available) {
		var opt = CuriosApi.getCuriosInventory(e).resolve();
		if (opt.isEmpty()) return 0;
		var list = opt.get().findCurios(stack -> stack.getCapability(BotaniaForgeCapabilities.MANA_ITEM).resolve().isPresent());
		int consumed = 0;
		for (var item : list) {
			var optMana = item.stack().getCapability(BotaniaForgeCapabilities.MANA_ITEM).resolve();
			if (optMana.isEmpty()) continue;
			ManaItem manaItem = optMana.get();
			int space = manaItem.getMaxMana() - manaItem.getMana();
			if (space <= 0) continue;
			int toGen = Math.min(available, space);
			available -= toGen;
			manaItem.addMana(toGen);
			if (available == 0) break;
		}
		return available;
	}



}
