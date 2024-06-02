package dev.xkmc.modulargolems.compat.materials.botania;

import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.curios.api.CuriosApi;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.mana.ManaItem;

import java.util.ArrayList;
import java.util.List;

public class BotUtils {

	private static List<ManaItem> getManaItems(LivingEntity e) {
		var opt = CuriosApi.getCuriosInventory(e).resolve();
		if (opt.isEmpty()) return List.of();
		var list = opt.get().findCurios(stack -> stack.getCapability(BotaniaForgeCapabilities.MANA_ITEM).resolve().isPresent());
		List<ManaItem> ans = new ArrayList<>();
		for (var item : list) {
			var optMana = item.stack().getCapability(BotaniaForgeCapabilities.MANA_ITEM).resolve();
			if (optMana.isEmpty()) continue;
			ans.add(optMana.get());
		}
		return ans;
	}

	private final List<ManaItem> list;

	public BotUtils(LivingEntity e) {
		list = getManaItems(e);
	}

	public int getMana() {
		int total = 0;
		for (var manaItem : list) {
			total += manaItem.getMana();
		}
		return total;
	}

	public int getMaxMana() {
		int total = 0;
		for (var manaItem : list) {
			total += manaItem.getMaxMana();
		}
		return total;
	}

	/**
	 * returns amount actually consumed
	 * s
	 */
	public int consumeMana(int target) {
		int consumed = 0;
		for (var manaItem : list) {
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
	public int generateMana(int available) {
		for (var manaItem : list) {
			int space = manaItem.getMaxMana() - manaItem.getMana();
			if (space <= 0) continue;
			int toGen = Math.min(available, space);
			available -= toGen;
			manaItem.addMana(toGen);
			if (available == 0) break;
		}
		return available;
	}

	private static String parse(int mana) {
		if (mana < 10000) return mana + "";
		mana /= 1000;
		if (mana < 10000) return mana + "k";
		return mana / 1000 + "M";
	}

	public static Component getDesc(LivingEntity golem) {
		var bot = new BotUtils(golem);
		return MGLangData.BOT_MANA.get(parse(bot.getMana()), parse(bot.getMaxMana()))
				.withStyle(ChatFormatting.LIGHT_PURPLE);
	}

}
