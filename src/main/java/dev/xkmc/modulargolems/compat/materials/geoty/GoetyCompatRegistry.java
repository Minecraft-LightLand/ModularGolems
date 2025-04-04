package dev.xkmc.modulargolems.compat.materials.geoty;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.geoty.modifier.*;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.content.modifier.base.PotionAttackModifier;
import net.minecraft.world.effect.MobEffectInstance;

import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class GoetyCompatRegistry {

	public static final RegistryEntry<PotionAttackModifier> BUSTED;
	public static final RegistryEntry<HauntedModifier> HAUNTED;
	public static final RegistryEntry<SoulRepairModifier> SOUL_REPAIR;
	public static final RegistryEntry<FireBlastModifier> FIRE_BLAST;
	public static final RegistryEntry<FireTornadoModifier> FIRE_TORNADO;
	public static final RegistryEntry<HellCloudModifier> HELL_CLOUD;
	public static final RegistryEntry<ApostleModifier> APOSTLE;


	public static final RegistryEntry<SimpleUpgradeItem> UPGRADE_BLAST, UPGRADE_TORNADO, UPGRADE_CLOUD, UPGRADE_APOSTLE;

	static {
		BUSTED = reg("fallen_attack", () -> new PotionAttackModifier(StatFilterType.ATTACK, 2,
				i -> new MobEffectInstance(GoetyEffects.BUSTED.get(), 100 * i, 0)), null);
		HAUNTED = reg("haunted", HauntedModifier::new,
				"Might summon haunted armor servant when killing enemies. " +
						"Higher chance to summon when killed target is armored");
		SOUL_REPAIR = reg("soul_repair", SoulRepairModifier::new,
				"Repair golem equipments with player's soul energy. " +
						"Also heal golem with soul energy when health is low");
		FIRE_BLAST = reg("fire_blast", FireBlastModifier::new,
				"Summon Fire Blast Trap in the front");
		FIRE_TORNADO = reg("fire_tornado", FireTornadoModifier::new,
				"Summon Fire Tornado Trap toward target position");
		HELL_CLOUD = reg("hell_cloud", HellCloudModifier::new,
				"Summon Hell cloud on target position");
		APOSTLE = reg("apostle", ApostleModifier::new, "Apostle Upgrades will not consume slot");

		UPGRADE_BLAST = regModUpgrade("fire_blast", () -> FIRE_BLAST, GoetyDispatch.MODID)
				.lang("Apostle Upgrade: Fire Blast").register();
		UPGRADE_TORNADO = regModUpgrade("fire_tornado", () -> FIRE_TORNADO, GoetyDispatch.MODID)
				.lang("Apostle Upgrade: Fire Tornado").register();
		UPGRADE_CLOUD = regModUpgrade("hell_cloud", () -> HELL_CLOUD, GoetyDispatch.MODID)
				.lang("Apostle Upgrade: Hell Cloud").register();
		UPGRADE_APOSTLE = regModUpgrade("apostle", () -> APOSTLE, GoetyDispatch.MODID)
				.lang("Apostle Ascension Upgrade").register();

	}

	public static void register() {

	}

}
