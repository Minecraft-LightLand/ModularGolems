package dev.xkmc.modulargolems.init.registrate;

import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.repack.registrate.util.entry.EntityEntry;
import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.content.core.GolemModifier;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.entity.MetalGolemEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class GolemTypeRegistry {

	public static final L2Registrate.RegistryInstance<GolemModifier> MODIFIERS = REGISTRATE.newRegistry("modifier", GolemModifier.class);
	public static final L2Registrate.RegistryInstance<GolemStatType> STAT_TYPES = REGISTRATE.newRegistry("stat_type", GolemStatType.class);
	public static final L2Registrate.RegistryInstance<GolemType<?>> TYPES = REGISTRATE.newRegistry("golem_type", GolemType.class);

	public static RegistryEntry<Attribute> GOLEM_REGEN = REGISTRATE.simple("golem_regen", ForgeRegistries.ATTRIBUTES.getRegistryKey(),
			() -> new RangedAttribute("attribute.name.golem_regen", 0, 0, 1000).setSyncable(true));
	public static RegistryEntry<Attribute> GOLEM_SWEEP = REGISTRATE.simple("golem_sweep", ForgeRegistries.ATTRIBUTES.getRegistryKey(),
			() -> new RangedAttribute("attribute.name.golem_sweep", 0, 0, 1000).setSyncable(true));

	public static final RegistryEntry<GolemStatType> STAT_HEALTH = regStat("max_health", () -> Attributes.MAX_HEALTH, GolemStatType.Kind.BASE);
	public static final RegistryEntry<GolemStatType> STAT_ATTACK = regStat("attack", () -> Attributes.ATTACK_DAMAGE, GolemStatType.Kind.BASE);
	public static final RegistryEntry<GolemStatType> STAT_REGEN = regStat("regen", GOLEM_REGEN, GolemStatType.Kind.BASE);
	public static final RegistryEntry<GolemStatType> STAT_SWEEP = regStat("sweep", GOLEM_SWEEP, GolemStatType.Kind.BASE);
	public static final RegistryEntry<GolemStatType> STAT_SPEED = regStat("speed", () -> Attributes.MOVEMENT_SPEED, GolemStatType.Kind.PERCENT);

	public static final EntityEntry<MetalGolemEntity> ENTITY_GOLEM = REGISTRATE.entity("metal_golem", MetalGolemEntity::new, MobCategory.MISC)
			.properties(e -> e.sized(1.4F, 2.7F).clientTrackingRange(10))
			.attributes(() -> AttributeSupplier.builder()
					.add(Attributes.MAX_HEALTH, 100.0D)
					.add(Attributes.MOVEMENT_SPEED, 0.25D)
					.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
					.add(Attributes.ATTACK_DAMAGE, 15.0D)
					.add(GOLEM_REGEN.get(), 0.0D)
					.add(GOLEM_SWEEP.get(), 0.0D)
			).register();

	public static final RegistryEntry<GolemType<MetalGolemEntity>> TYPE_GOLEM = REGISTRATE.generic(TYPES, "metal_golem",
			() -> new GolemType<>(ENTITY_GOLEM)).defaultLang().register();

	private static RegistryEntry<GolemStatType> regStat(String id, Supplier<Attribute> sup, GolemStatType.Kind kind) {
		return REGISTRATE.generic(STAT_TYPES, id, () -> new GolemStatType(sup, kind)).register();
	}

	public static void register() {

	}

}
