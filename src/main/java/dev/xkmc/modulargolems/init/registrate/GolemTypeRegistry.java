package dev.xkmc.modulargolems.init.registrate;

import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.repack.registrate.util.entry.EntityEntry;
import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.humanoid.HumaniodGolemPartType;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemModel;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemRenderer;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModel;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemRenderer;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class GolemTypeRegistry {

	public static final L2Registrate.RegistryInstance<GolemModifier> MODIFIERS = REGISTRATE.newRegistry("modifier", GolemModifier.class);
	public static final L2Registrate.RegistryInstance<GolemStatType> STAT_TYPES = REGISTRATE.newRegistry("stat_type", GolemStatType.class);
	public static final L2Registrate.RegistryInstance<GolemType<?, ?>> TYPES = REGISTRATE.newRegistry("golem_type", GolemType.class);

	public static RegistryEntry<Attribute> GOLEM_REGEN = REGISTRATE.simple("golem_regen", ForgeRegistries.ATTRIBUTES.getRegistryKey(),
			() -> new RangedAttribute("attribute.name.golem_regen", 0, 0, 1000).setSyncable(true));
	public static RegistryEntry<Attribute> GOLEM_SWEEP = REGISTRATE.simple("golem_sweep", ForgeRegistries.ATTRIBUTES.getRegistryKey(),
			() -> new RangedAttribute("attribute.name.golem_sweep", 0, 0, 1000).setSyncable(true));

	public static final RegistryEntry<GolemStatType> STAT_HEALTH = regStat("max_health", () -> Attributes.MAX_HEALTH, GolemStatType.Kind.BASE, StatFilterType.HEALTH);
	public static final RegistryEntry<GolemStatType> STAT_ATTACK = regStat("attack", () -> Attributes.ATTACK_DAMAGE, GolemStatType.Kind.BASE, StatFilterType.ATTACK);
	public static final RegistryEntry<GolemStatType> STAT_REGEN = regStat("regen", GOLEM_REGEN, GolemStatType.Kind.ADD, StatFilterType.HEALTH);
	public static final RegistryEntry<GolemStatType> STAT_SWEEP = regStat("sweep", GOLEM_SWEEP, GolemStatType.Kind.ADD, StatFilterType.ATTACK);
	public static final RegistryEntry<GolemStatType> STAT_ARMOR = regStat("armor", () -> Attributes.ARMOR, GolemStatType.Kind.ADD, StatFilterType.HEALTH);
	public static final RegistryEntry<GolemStatType> STAT_TOUGH = regStat("tough", () -> Attributes.ARMOR_TOUGHNESS, GolemStatType.Kind.ADD, StatFilterType.HEALTH);
	public static final RegistryEntry<GolemStatType> STAT_KBRES = regStat("knockback_resistance", () -> Attributes.KNOCKBACK_RESISTANCE, GolemStatType.Kind.ADD, StatFilterType.HEALTH);
	public static final RegistryEntry<GolemStatType> STAT_ATKKB = regStat("attack_knockback", () -> Attributes.ATTACK_KNOCKBACK, GolemStatType.Kind.ADD, StatFilterType.ATTACK);
	public static final RegistryEntry<GolemStatType> STAT_WEIGHT = regStat("weight", () -> Attributes.MOVEMENT_SPEED, GolemStatType.Kind.PERCENT, StatFilterType.MASS);
	public static final RegistryEntry<GolemStatType> STAT_SPEED = regStat("speed", () -> Attributes.MOVEMENT_SPEED, GolemStatType.Kind.PERCENT, StatFilterType.MOVEMENT);

	public static final EntityEntry<MetalGolemEntity> ENTITY_GOLEM = REGISTRATE.entity("metal_golem", MetalGolemEntity::new, MobCategory.MISC)
			.properties(e -> e.sized(1.4F, 2.7F).clientTrackingRange(10))
			.renderer(() -> MetalGolemRenderer::new)
			.attributes(() -> Mob.createMobAttributes()
					.add(Attributes.MAX_HEALTH, 100.0D)
					.add(Attributes.ATTACK_DAMAGE, 15.0D)
					.add(Attributes.MOVEMENT_SPEED, 0.25D)
					.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
					.add(Attributes.ATTACK_KNOCKBACK, 0.4D)
					.add(Attributes.FOLLOW_RANGE, 16.0D)
					.add(GOLEM_REGEN.get())
					.add(GOLEM_SWEEP.get())
			).register();

	public static final RegistryEntry<GolemType<MetalGolemEntity, MetalGolemPartType>> TYPE_GOLEM = REGISTRATE.generic(TYPES, "metal_golem",
					() -> new GolemType<>(ENTITY_GOLEM, MetalGolemPartType::values, MetalGolemPartType.BODY, () -> MetalGolemModel::new))
			.defaultLang().register();

	public static final EntityEntry<HumanoidGolemEntity> ENTITY_HUMANOID = REGISTRATE.entity("humanoid_golem", HumanoidGolemEntity::new, MobCategory.MISC)
			.properties(e -> e.sized(0.6F, 1.95F).clientTrackingRange(10))
			.renderer(() -> HumanoidGolemRenderer::new)
			.attributes(() -> Mob.createMobAttributes()
					.add(Attributes.MAX_HEALTH, 20.0D)
					.add(Attributes.ATTACK_DAMAGE, 4.0D)
					.add(Attributes.MOVEMENT_SPEED, 0.25D)
					.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
					.add(Attributes.ATTACK_KNOCKBACK, 0.4D)
					.add(Attributes.FOLLOW_RANGE, 35.0D)
					.add(GOLEM_REGEN.get())
					.add(GOLEM_SWEEP.get(), 1)
			).register();

	public static final RegistryEntry<GolemType<HumanoidGolemEntity, HumaniodGolemPartType>> TYPE_HUMANOID = REGISTRATE.generic(TYPES, "humanoid_golem",
					() -> new GolemType<>(ENTITY_HUMANOID, HumaniodGolemPartType::values, HumaniodGolemPartType.BODY, () -> HumanoidGolemModel::new))
			.defaultLang().register();

	private static RegistryEntry<GolemStatType> regStat(String id, Supplier<Attribute> sup, GolemStatType.Kind kind, StatFilterType type) {
		return REGISTRATE.generic(STAT_TYPES, id, () -> new GolemStatType(sup, kind, type)).register();
	}

	public static void register() {

	}

}
