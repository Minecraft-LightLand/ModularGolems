package dev.xkmc.modulargolems.compat.materials.allthemodium;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.modulargolems.content.item.upgrade.AddSlotTemplate;
import dev.xkmc.modulargolems.content.modifier.common.AddSlotModifier;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.resources.Identifier;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class ATMCompatRegistry {

	public static final Val<AddSlotModifier> ADD_ATM, ADD_VIB, ADD_UNO;
	public static final ItemEntry<AddSlotTemplate> EX_ATM, EX_VIB, EX_UNO;

	static {

		ADD_ATM = reg("add_slot_allthemodium", () -> new AddSlotModifier(1), "Allthemodium Expansion", "Add 1 upgrade slot. Only once per golem.");
		ADD_VIB = reg("add_slot_vibranium", () -> new AddSlotModifier(1), "Vibranium Expansion", "Add 1 upgrade slot. Only once per golem.");
		ADD_UNO = reg("add_slot_unobtainium", () -> new AddSlotModifier(1), "Unobtainium Expansion", "Add 1 upgrade slot. Only once per golem.");

		EX_ATM = adder("allthemodium_expansion_template", ADD_ATM);
		EX_VIB = adder("vibranium_expansion_template", ADD_VIB);
		EX_UNO = adder("unobtainium_expansion_template", ADD_UNO);

	}

	private static ItemEntry<AddSlotTemplate> adder(String id, Val<AddSlotModifier> modifier) {
		return REGISTRATE.item(id, p -> new AddSlotTemplate(p, modifier))
				.model((ctx, pvd) ->
						pvd.generated(ctx, Identifier.fromNamespaceAndPath(ATMDispatch.MODID,
								"item/" + ctx.getName()))).defaultLang().register();
	}

	public static void register() {
		MGTagGen.OPTIONAL_ITEM.add(pvd ->
				pvd.addTag(MGTagGen.EXPANSION)
						.addOptional(EX_ATM.getId())
						.addOptional(EX_VIB.getId())
						.addOptional(EX_UNO.getId())
		);
	}

}
