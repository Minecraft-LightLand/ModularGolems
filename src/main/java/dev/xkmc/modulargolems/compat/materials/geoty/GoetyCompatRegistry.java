package dev.xkmc.modulargolems.compat.materials.geoty;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.geoty.modifier.*;
import dev.xkmc.modulargolems.compat.materials.geoty.revelation.HellBlastModifier;
import dev.xkmc.modulargolems.compat.materials.geoty.revelation.HellBoltModifier;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.content.modifier.base.PotionAttackModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;

import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class GoetyCompatRegistry {

	public static final RegistryEntry<PotionAttackModifier> BUSTED;
	public static final RegistryEntry<HauntedModifier> HAUNTED;
	public static final RegistryEntry<SoulRepairModifier> SOUL_REPAIR;
	public static final RegistryEntry<FireBlastModifier> FIRE_BLAST;
	public static final RegistryEntry<FireTornadoModifier> FIRE_TORNADO;
	public static final RegistryEntry<HellCloudModifier> HELL_CLOUD;
	public static final RegistryEntry<HellBoltModifier> HELL_BOLT;
	public static final RegistryEntry<HellBlastModifier> HELL_BLAST;
	public static final RegistryEntry<ApostleModifier> APOSTLE;


	public static final RegistryEntry<SimpleUpgradeItem> UPGRADE_BLAST, UPGRADE_TORNADO, UPGRADE_CLOUD,
			UPGRADE_BOLT, UPGRADE_BALL, UPGRADE_APOSTLE;

	public static final TagKey<Item> REV_RING = ItemTags.create(ModularGolems.loc("revelation_ring"));
	public static final TagKey<Item> REV_DOOM = ItemTags.create(ModularGolems.loc("revelation_doom"));

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
		HELL_BOLT = reg("hell_bolt", HellBoltModifier::new,
				"Shoot Hell bolt toward targets");
		HELL_BLAST = reg("hell_blast", HellBlastModifier::new,
				"Shoot Hell blast toward targets");
		APOSTLE = reg("apostle", ApostleModifier::new, "Apostle Upgrades will not consume slot");

		UPGRADE_BLAST = regModUpgrade("fire_blast", () -> FIRE_BLAST, GoetyDispatch.MODID)
				.lang("Apostle Upgrade: Fire Blast").register();
		UPGRADE_TORNADO = regModUpgrade("fire_tornado", () -> FIRE_TORNADO, GoetyDispatch.MODID)
				.lang("Apostle Upgrade: Fire Tornado").register();
		UPGRADE_CLOUD = regModUpgrade("hell_cloud", () -> HELL_CLOUD, GoetyDispatch.MODID)
				.lang("Apostle Upgrade: Hell Cloud").register();
		UPGRADE_BOLT = regModUpgrade("hell_bolt", () -> HELL_BOLT, GoetyDispatch.MODID)
				.lang("Apostle Upgrade: Hell Bolt").register();
		UPGRADE_BALL = regModUpgrade("hell_blast", () -> HELL_BLAST, GoetyDispatch.MODID)
				.lang("Apostle Upgrade: Hell Blast").register();
		UPGRADE_APOSTLE = regModUpgrade("apostle", () -> APOSTLE, GoetyDispatch.MODID)
				.lang("Apostle Ascension Upgrade").register();

	}

	public static void register() {
		MGTagGen.OPTIONAL_ITEM.add(pvd -> {
			pvd.addTag(REV_RING).addOptional(new ResourceLocation("goety_revelation", "ascension_halo"));
			pvd.addTag(REV_DOOM).addOptional(new ResourceLocation("goety_revelation", "doom_medal"));
		});

	}

}
