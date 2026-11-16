package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud.RootPercModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud.ThunderModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud.CloudChargeModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud.CloudBlockModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud.CloudDropModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud.CloudLaserEntity;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud.CloudLaserRenderer;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud.CycloneModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud.FierceWaveStompModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud.LineCloudModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud.SkyLaserModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud.SkyfallSmashModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorCloneBarrageModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorCloneBurstModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorArmBlockModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorSmallBombModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorStompModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorJumpGroundChargeModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorLargeBombModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator.ObliteratorLaserModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin.PaladinSoulBladeLeapModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin.PaladinRollSlashModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin.PaladinSoulTridentModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin.PaladinSoulBlastModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin.PaladinSoulShieldModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin.PaladinSoulDaggerModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin.PaladinSoulBladeModifier;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.paladin.PaladinSoulBlastWaveModifier;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;

import static dev.xkmc.modulargolems.init.registrate.GolemItems.regModUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class LMCompatRegistry {

	public static final RegistryEntry<AncientAnchorModifier> ANCHOR;
	public static final RegistryEntry<ThunderModifier> THUNDER;
	public static final RegistryEntry<RootPercModifier> PERC;
	public static final RegistryEntry<CloudChargeModifier> CLOUD_CHARGE;
	public static final RegistryEntry<SkyLaserModifier> SKY_LASER;
	public static final RegistryEntry<LineCloudModifier> LINE_CLOUD;
	public static final RegistryEntry<CycloneModifier> CONTROL_CYCLONE;
	public static final RegistryEntry<CloudDropModifier> CLOUD_DROP;
	public static final RegistryEntry<CloudBlockModifier> CLOUD_BLOCK;
	public static final RegistryEntry<SkyfallSmashModifier> SKYFALL_SMASH;
	public static final RegistryEntry<FierceWaveStompModifier> FIERCE_WAVE_STOMP;
	public static final RegistryEntry<ObliteratorLargeBombModifier> OBLITERATOR_LARGE_BOMB;
	public static final RegistryEntry<ObliteratorSmallBombModifier> OBLITERATOR_SMALL_BOMB;
	public static final RegistryEntry<ObliteratorLaserModifier> OBLITERATOR_LASER;
	public static final RegistryEntry<ObliteratorJumpGroundChargeModifier> OBLITERATOR_JUMP;
	public static final RegistryEntry<ObliteratorCloneBarrageModifier> OBLITERATOR_CLONE_BARRAGE;
	public static final RegistryEntry<ObliteratorCloneBurstModifier> OBLITERATOR_CLONE_BURST;
	public static final RegistryEntry<ObliteratorArmBlockModifier> OBLITERATOR_ARM_BLOCK;
	public static final RegistryEntry<ObliteratorStompModifier> OBLITERATOR_STOMP;
	public static final RegistryEntry<PaladinSoulDaggerModifier> PALADIN_SOUL_DAGGER;
	public static final RegistryEntry<PaladinSoulBlastWaveModifier> PALADIN_SOUL_BLAST_WAVE;
	public static final RegistryEntry<PaladinSoulBladeLeapModifier> PALADIN_SOUL_BLADE_LEAP;
	public static final RegistryEntry<PaladinSoulShieldModifier> PALADIN_SOUL_SHIELD;
	public static final RegistryEntry<PaladinRollSlashModifier> PALADIN_ROLL_SLASH;
	public static final RegistryEntry<PaladinSoulTridentModifier> PALADIN_SOUL_TRIDENT;
	public static final RegistryEntry<PaladinSoulBlastModifier> PALADIN_SOUL_BLAST;
	public static final RegistryEntry<PaladinSoulBladeModifier> PALADIN_SOUL_BLADE;
	/** 天穹激光的光束，发射器归 MG 自己所有（见 {@link CloudLaserEntity}）。 */
	public static final EntityEntry<CloudLaserEntity> CLOUD_LASER;
	public static final ItemEntry<Item> CLOUD_CUBE, ANNIHILATION_CUBE, POSESSED_SOUL_CUBE;
	public static final RegistryEntry<SimpleUpgradeItem> UPGRADE_THUNDER,
			UPGRADE_ANNIHILATION_BOMB;

	static {
		ANCHOR = reg("ancient_anchor", () -> new AncientAnchorModifier(StatFilterType.MASS, 4),
				"Ancient Anchor", "Leap and slam the ground, releasing a shockwave that stuns targets and deals area damage. Damage taken is reduced by %s while leaping.");

		THUNDER = reg("thunderstorm", () -> new ThunderModifier(StatFilterType.ATTACK, 2),
				"Thunder Fist", "Summon multiple waves of thunderclouds toward the target.");

		PERC = reg("cloud_forming", () -> new RootPercModifier(StatFilterType.ATTACK, 2),
				"Cloud Forming", "Deal more damage to targets with higher max health.");

		CLOUD_CHARGE = reg("cloud_charge", () -> new CloudChargeModifier(StatFilterType.HEAD, 1),
				"Cloud Charge", "Can charge at distant targets, damaging every target along the path and taking %s less damage while charging. Chains more often at lower health, up to %s times.");

		SKY_LASER = reg("sky_laser", () -> new SkyLaserModifier(StatFilterType.HEAD, 1),
				"Sky Laser", "Fire a laser beam at the target, damaging all targets along the path.");

		LINE_CLOUD = reg("line_cloud", () -> new LineCloudModifier(StatFilterType.ATTACK, 2),
				"Line Cloud", "Summon a line of falling clouds toward the target.");

		CONTROL_CYCLONE = reg("control_cyclone", () -> new CycloneModifier(StatFilterType.ATTACK, 2),
				"Control Cyclone", "Summon a line of cyclones toward the target, sweeping all targets along the path into the air.");

		CLOUD_DROP = reg("cloud_drop", () -> new CloudDropModifier(StatFilterType.HEAD, 1),
				"Cloud Drop Formation", "Summon many clouds overhead around the golem and drop them to the ground.");
		CLOUD_BLOCK = reg("cloud_block", () -> new CloudBlockModifier(StatFilterType.HEAD, 1),
			"Cloud Block", "For %s seconds after being attacked, block damage and cap each incoming hit at %s damage.");

		FIERCE_WAVE_STOMP = reg("fierce_wave_stomp", () -> new FierceWaveStompModifier(StatFilterType.MOVEMENT, 1),
				"Fierce Wave Stomp", "Summon fierce waves in a fan toward the target, up to %s consecutive times.");

		SKYFALL_SMASH = reg("skyfall_smash", () -> new SkyfallSmashModifier(StatFilterType.MOVEMENT, 1),
				"Skyfall Smash", "Leap and smash the ground, dealing area damage. Damage taken is reduced by %s while leaping.");

		OBLITERATOR_LARGE_BOMB = reg("obliterator_large_bomb", () -> new ObliteratorLargeBombModifier(StatFilterType.ATTACK, 3),
				"Obliterator Cluster Bomb", "Launch an annihilation cluster bomb at the target.");

		OBLITERATOR_SMALL_BOMB = reg("obliterator_small_bomb", () -> new ObliteratorSmallBombModifier(StatFilterType.ATTACK, 3),
				"Obliterator Bomb", "Launch small annihilation bombs at multiple targets.");
		OBLITERATOR_LASER = reg("obliterator_laser", () -> new ObliteratorLaserModifier(StatFilterType.HEAD, 1),
				"Obliterator Laser", "Fire an annihilation beam at the target, following the golem's rotation.");
		OBLITERATOR_JUMP = reg("obliterator_jump", () -> new ObliteratorJumpGroundChargeModifier(StatFilterType.MOVEMENT, 1),
				"Obliterator Jump", "Roar to push away nearby targets, teleport above the target and slam down, dealing area damage and summoning plasma orbs. %s chance to cast again. Damage taken is reduced by %s while leaping. Below %s max health, additionally triggers Annihilation Havoc.");
		OBLITERATOR_CLONE_BARRAGE = reg("obliterator_clone_barrage", () -> new ObliteratorCloneBarrageModifier(StatFilterType.HEAD, 1),
				"Clone Barrage", "Summon eight clones around the target, then fire annihilation bombs at the target.");

		OBLITERATOR_CLONE_BURST = reg("obliterator_clone_burst", () -> new ObliteratorCloneBurstModifier(StatFilterType.HEAD, 1),
				"Clone Burst", "Charge at distant targets, damaging every target along the path and taking %s less damage while charging. Three clones dash at the target alongside the charge.");

		OBLITERATOR_ARM_BLOCK = reg("obliterator_arm_block", () -> new ObliteratorArmBlockModifier(StatFilterType.HEAD, 1),
				"Annihilation Guard", "When attacked, %s chance to block the hit and teleport behind the attacker.");

		OBLITERATOR_STOMP = reg("obliterator_stomp", () -> new ObliteratorStompModifier(StatFilterType.MOVEMENT, 1),
				"Annihilation Stomp", "Summon annihilation shockwaves in a fan toward the target.");

		PALADIN_SOUL_DAGGER = reg("paladin_soul_dagger", () -> new PaladinSoulDaggerModifier(StatFilterType.ATTACK, 2),
				"Soul Dagger", "Fire %s homing soul daggers at the target.");

		PALADIN_SOUL_BLAST_WAVE = reg("paladin_soul_blast_wave", () -> new PaladinSoulBlastWaveModifier(StatFilterType.MOVEMENT, 1),
				"Soul Spike", "Push out soul shockwaves in a fan shape toward the target.");

		PALADIN_SOUL_BLADE_LEAP = reg("paladin_soul_blade_leap", () -> new PaladinSoulBladeLeapModifier(StatFilterType.MOVEMENT, 1),
				"Paladin Soul Blade Leap", "Leap at the target and slam down, dealing area damage and summoning fallen soul blades and soul shields. Damage taken is reduced by %s while leaping. Below %s max health, additionally summons falling fallen soul blades.");

		PALADIN_SOUL_SHIELD = reg("paladin_soul_shield", () -> new PaladinSoulShieldModifier(StatFilterType.HEAD, 1),
				"Paladin Block Shield", "For %s seconds after being attacked, block damage, then sweep the front and throw %s soul daggers.");
		PALADIN_ROLL_SLASH = reg("paladin_roll_slash", () -> new PaladinRollSlashModifier(StatFilterType.HEAD, 1),
				"Rolling Slash", "Charge at distant targets, damaging every target along the path and leaving soul spikes, taking %s less damage while charging.");
		PALADIN_SOUL_TRIDENT = reg("paladin_soul_trident", () -> new PaladinSoulTridentModifier(StatFilterType.ATTACK, 2),
				"Soul Trident", "Throw a soul trident at the target, producing a large soul blast on impact.");
		PALADIN_SOUL_BLAST = reg("paladin_soul_blast", () -> new PaladinSoulBlastModifier(StatFilterType.MOVEMENT, 1),
				"Soul Blast", "Release soul shockwaves that expand outward around the golem.");

		PALADIN_SOUL_BLADE = reg("paladin_soul_blade", () -> new PaladinSoulBladeModifier(StatFilterType.ATTACK, 2),
				"Soul Blade", "Stack soul fracture on the attack target. Below %s max health, additionally inflicts bleeding.");

		UPGRADE_ANNIHILATION_BOMB = regModUpgrade("annihilation_bomb", () -> OBLITERATOR_SMALL_BOMB, LMDispatch.MODID)
				.lang("Annihilation Bomb Upgrade").register();

		UPGRADE_THUNDER = regModUpgrade("thunderstorm", () -> THUNDER, LMDispatch.MODID)
				.lang("Thunderstorm Upgrade").register();


		CLOUD_LASER = ModularGolems.REGISTRATE
				.<CloudLaserEntity>entity("cloud_golem_laser", CloudLaserEntity::new, MobCategory.MISC)
				.properties(p -> p.fireImmune().noSave().noSummon().sized(0, 0))
				.renderer(() -> CloudLaserRenderer::new)
				.register();

		CLOUD_CUBE = GolemItems.item(LMDispatch.MODID, "cloud_cube", Item::new);
		ANNIHILATION_CUBE = GolemItems.item(LMDispatch.MODID, "annihilation_cube", Item::new);
		POSESSED_SOUL_CUBE = GolemItems.item(LMDispatch.MODID, "posessed_soul_cube", Item::new);
	}

	public static void register() {
		MGTagGen.OPTIONAL_ITEM.add(e -> e.addTag(MGTagGen.RED_UPGRADES)
				.addOptional(UPGRADE_THUNDER.getId())
				.addOptional(UPGRADE_ANNIHILATION_BOMB.getId()));
	}

}
