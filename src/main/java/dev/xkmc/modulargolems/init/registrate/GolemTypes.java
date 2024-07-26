package dev.xkmc.modulargolems.init.registrate;

import com.tterrag.registrate.util.entry.EntityEntry;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.init.reg.registrate.SimpleEntry;
import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemModel;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemPartType;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemRenderer;
import dev.xkmc.modulargolems.content.entity.humanoid.HumaniodGolemPartType;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemModel;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemRenderer;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModel;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemRenderer;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import java.util.function.Supplier;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class GolemTypes {

	public static final L2Registrate.RegistryInstance<GolemModifier> MODIFIERS = REGISTRATE.newRegistry("modifier", GolemModifier.class);
	public static final L2Registrate.RegistryInstance<GolemStatType> STAT_TYPES = REGISTRATE.newRegistry("stat_type", GolemStatType.class);
	public static final L2Registrate.RegistryInstance<GolemType<?, ?>> TYPES = REGISTRATE.newRegistry("golem_type", GolemType.class);

	public static SimpleEntry<Attribute> GOLEM_REGEN = new SimpleEntry<>(REGISTRATE.simple("golem_regen", Registries.ATTRIBUTE,
			() -> new RangedAttribute("attribute.name.golem_regen", 0, 0, 1000).setSyncable(true)));
	public static SimpleEntry<Attribute> GOLEM_SWEEP = new SimpleEntry<>(REGISTRATE.simple("golem_sweep", Registries.ATTRIBUTE,
			() -> new RangedAttribute("attribute.name.golem_sweep", 0, 0, 1000).setSyncable(true)));
	public static SimpleEntry<Attribute> GOLEM_SIZE = new SimpleEntry<>(REGISTRATE.simple("golem_size", Registries.ATTRIBUTE,
			() -> new RangedAttribute("attribute.name.golem_size", 1, 0, 1000).setSyncable(true)));
	public static SimpleEntry<Attribute> GOLEM_JUMP = new SimpleEntry<>(REGISTRATE.simple("golem_jump", Registries.ATTRIBUTE,
			() -> new RangedAttribute("attribute.name.golem_jump", 0.5, 0, 1000).setSyncable(true)));

	public static final SimpleEntry<GolemStatType> STAT_HEALTH = regStat("max_health", () -> Attributes.MAX_HEALTH, GolemStatType.Kind.BASE, StatFilterType.HEALTH);
	public static final SimpleEntry<GolemStatType> STAT_ATTACK = regStat("attack", () -> Attributes.ATTACK_DAMAGE, GolemStatType.Kind.BASE, StatFilterType.ATTACK);
	public static final SimpleEntry<GolemStatType> STAT_REGEN = regStat("regen", GOLEM_REGEN::holder, GolemStatType.Kind.ADD, StatFilterType.HEALTH);
	public static final SimpleEntry<GolemStatType> STAT_SWEEP = regStat("sweep", GOLEM_SWEEP::holder, GolemStatType.Kind.ADD, StatFilterType.ATTACK);
	public static final SimpleEntry<GolemStatType> STAT_ARMOR = regStat("armor", () -> Attributes.ARMOR, GolemStatType.Kind.ADD, StatFilterType.HEALTH);
	public static final SimpleEntry<GolemStatType> STAT_TOUGH = regStat("tough", () -> Attributes.ARMOR_TOUGHNESS, GolemStatType.Kind.ADD, StatFilterType.HEALTH);
	public static final SimpleEntry<GolemStatType> STAT_KBRES = regStat("knockback_resistance", () -> Attributes.KNOCKBACK_RESISTANCE, GolemStatType.Kind.ADD, StatFilterType.HEALTH);
	public static final SimpleEntry<GolemStatType> STAT_ATKKB = regStat("attack_knockback", () -> Attributes.ATTACK_KNOCKBACK, GolemStatType.Kind.ADD, StatFilterType.ATTACK);
	public static final SimpleEntry<GolemStatType> STAT_WEIGHT = regStat("weight", () -> Attributes.MOVEMENT_SPEED, GolemStatType.Kind.PERCENT, StatFilterType.MASS);
	public static final SimpleEntry<GolemStatType> STAT_SPEED = regStat("speed", () -> Attributes.MOVEMENT_SPEED, GolemStatType.Kind.PERCENT, StatFilterType.MOVEMENT);
	public static final SimpleEntry<GolemStatType> STAT_JUMP = regStat("jump_strength", GOLEM_JUMP::holder, GolemStatType.Kind.PERCENT, StatFilterType.MOVEMENT);
	public static final SimpleEntry<GolemStatType> STAT_HEALTH_P = regStat("max_health_percent", () -> Attributes.MAX_HEALTH, GolemStatType.Kind.PERCENT, StatFilterType.HEALTH);
	public static final SimpleEntry<GolemStatType> STAT_SIZE = regStat("max_size", GOLEM_SIZE::holder, GolemStatType.Kind.ADD, StatFilterType.HEALTH);
	public static final SimpleEntry<GolemStatType> STAT_RANGE = regStat("range", () -> Attributes.ENTITY_INTERACTION_RANGE, GolemStatType.Kind.ADD, StatFilterType.ATTACK);

	public static final EntityEntry<MetalGolemEntity> ENTITY_GOLEM;
	public static final EntityEntry<HumanoidGolemEntity> ENTITY_HUMANOID;
	public static final EntityEntry<DogGolemEntity> ENTITY_DOG;

	public static final Val<GolemType<MetalGolemEntity, MetalGolemPartType>> TYPE_GOLEM;
	public static final Val<GolemType<HumanoidGolemEntity, HumaniodGolemPartType>> TYPE_HUMANOID;
	public static final Val<GolemType<DogGolemEntity, DogGolemPartType>> TYPE_DOG;

	static {
		ENTITY_GOLEM = REGISTRATE.entity("metal_golem", MetalGolemEntity::new, MobCategory.MISC)
				.properties(e -> e.sized(1.4F, 2.7F).clientTrackingRange(10))
				.renderer(() -> MetalGolemRenderer::new)
				.attributes(() -> Mob.createMobAttributes()
						.add(Attributes.MAX_HEALTH, 100.0D)
						.add(Attributes.ATTACK_DAMAGE, 15.0D)
						.add(Attributes.MOVEMENT_SPEED, 0.25D)
						.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
						.add(Attributes.ATTACK_SPEED, 4.0D)
						.add(Attributes.ATTACK_KNOCKBACK, 1.0D)
						.add(Attributes.ENTITY_INTERACTION_RANGE, 2)
						.add(Attributes.FOLLOW_RANGE, 35)
						.add(GOLEM_REGEN.holder())
						.add(GOLEM_SWEEP.holder())
						.add(GOLEM_SIZE.holder(), 3)
				).tag(MGTagGen.GOLEM_FRIENDLY).register();

		ENTITY_HUMANOID = REGISTRATE.entity("humanoid_golem", HumanoidGolemEntity::new, MobCategory.MISC)
				.properties(e -> e.sized(0.6F, 1.95F).clientTrackingRange(10))
				.renderer(() -> HumanoidGolemRenderer::new)
				.attributes(() -> Mob.createMobAttributes()
						.add(Attributes.MAX_HEALTH, 20.0D)
						.add(Attributes.ATTACK_DAMAGE, 4.0D)
						.add(Attributes.MOVEMENT_SPEED, 0.25D)
						.add(Attributes.KNOCKBACK_RESISTANCE, 0)
						.add(Attributes.ATTACK_SPEED, 4.0D)
						.add(Attributes.ATTACK_KNOCKBACK, 0.4D)
						.add(Attributes.ENTITY_INTERACTION_RANGE, 1)
						.add(Attributes.FOLLOW_RANGE, 35.0D)
						.add(GOLEM_REGEN.holder())
						.add(GOLEM_SWEEP.holder(), 1)
						.add(GOLEM_SIZE.holder(), 2.5)
				).tag(MGTagGen.GOLEM_FRIENDLY).register();

		ENTITY_DOG = REGISTRATE.entity("dog_golem", DogGolemEntity::new, MobCategory.MISC)
				.properties(e -> e.sized(0.85F, 0.6F).clientTrackingRange(10))
				.renderer(() -> DogGolemRenderer::new)
				.attributes(() -> Mob.createMobAttributes()
						.add(Attributes.MAX_HEALTH, 8.0D)
						.add(Attributes.ATTACK_DAMAGE, 4.0D)
						.add(Attributes.MOVEMENT_SPEED, 0.3D)
						.add(Attributes.KNOCKBACK_RESISTANCE, 0)
						.add(Attributes.ATTACK_SPEED, 4.0D)
						.add(Attributes.ATTACK_KNOCKBACK, 0.4D)
						.add(Attributes.ENTITY_INTERACTION_RANGE, 1.5)
						.add(Attributes.FOLLOW_RANGE, 35.0D)
						.add(GOLEM_JUMP.holder(), 0.5D)
						.add(GOLEM_REGEN.holder())
						.add(GOLEM_SIZE.holder(), 1)
				).tag(MGTagGen.GOLEM_FRIENDLY).register();

		TYPE_GOLEM = new Val.Registrate<>(REGISTRATE.generic(TYPES, "metal_golem",
						() -> new GolemType<>(ENTITY_GOLEM, MetalGolemPartType::values, MetalGolemPartType.BODY, () -> MetalGolemModel::new))
				.defaultLang().register());

		TYPE_HUMANOID = new Val.Registrate<>(REGISTRATE.generic(TYPES, "humanoid_golem",
						() -> new GolemType<>(ENTITY_HUMANOID, HumaniodGolemPartType::values, HumaniodGolemPartType.BODY, () -> HumanoidGolemModel::new))
				.defaultLang().register());

		TYPE_DOG = new Val.Registrate<>(REGISTRATE.generic(TYPES, "dog_golem",
						() -> new GolemType<>(ENTITY_DOG, DogGolemPartType::values, DogGolemPartType.BODY, () -> DogGolemModel::new))
				.defaultLang().register());
	}

	private static SimpleEntry<GolemStatType> regStat(String id, Supplier<Holder<Attribute>> sup, GolemStatType.Kind kind, StatFilterType type) {
		return new SimpleEntry<>(REGISTRATE.generic(STAT_TYPES, id, () -> new GolemStatType(sup, kind, type)).register());
	}

	public static void register() {

	}

}
