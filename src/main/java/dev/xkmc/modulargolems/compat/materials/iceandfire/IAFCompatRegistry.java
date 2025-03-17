package dev.xkmc.modulargolems.compat.materials.iceandfire;

import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.modulargolems.compat.materials.iceandfire.modifiers.FireDragonAttackModifier;
import dev.xkmc.modulargolems.compat.materials.iceandfire.modifiers.IceDragonAttackModifier;
import dev.xkmc.modulargolems.compat.materials.iceandfire.modifiers.IceDragonDefenseModifier;
import dev.xkmc.modulargolems.compat.materials.iceandfire.modifiers.LightningDragonAttackModifier;

import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class IAFCompatRegistry {

	public static final Val<FireDragonAttackModifier> FIRE_ATK;
	public static final Val<IceDragonAttackModifier> ICE_ATK;
	public static final Val<LightningDragonAttackModifier> LIGHTNING_ATK;
	public static final Val<IceDragonDefenseModifier> ICE_DEF;

	static {
		FIRE_ATK = reg("fire_dragonsteel_attack", FireDragonAttackModifier::new, "Ignite and knockback target");
		ICE_ATK = reg("ice_dragonsteel_attack", IceDragonAttackModifier::new, "Freeze, slow, and knockback target");
		LIGHTNING_ATK = reg("lightning_dragonsteel_attack", LightningDragonAttackModifier::new, "Summon lightning bolt to attack target");
		ICE_DEF = reg("ice_dragonsteel_armor", IceDragonDefenseModifier::new, "Freeze, slow, and knockback attacker when attacked");
	}

	public static void register() {

	}

}
