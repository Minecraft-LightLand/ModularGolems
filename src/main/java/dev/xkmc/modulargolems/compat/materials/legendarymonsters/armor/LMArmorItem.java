package dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMDispatch;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFormAppearance;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * 传奇怪物联动盔甲。与腾炎傀儡盔甲一样支持「形态变化贴图」：圣骑与浮云在二阶段换成另一套
 * 配色，湮灭没有二阶段变体，{@code variant} 传 {@code null} 即可。
 * <p>
 * 贴图按部位拆开存放（{@code textures/equipments/<id>.png}），与灾变盔甲一致，
 * 这样每件盔甲只加载自己那一片，不会把整张合并图都读进来。
 */
public class LMArmorItem extends MetalGolemArmorItem {

	/** 二阶段判定；{@code null} 表示该套盔甲没有形态变化。 */
	private final Predicate<LivingEntity> secondPhase;
	/** 二阶段贴图后缀，如 {@code _posessed} / {@code _dark}。 */
	private final String variant;
	/** 该套装在 tooltip 里展示的材质名（如 {@code paladin}）。 */
	private final String material;

	public LMArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness,
			float knockbackResistance, ResourceLocation model, String material, String variant,
			Predicate<LivingEntity> secondPhase) {
		super(properties, type, defense, toughness, knockbackResistance, model);
		this.material = material;
		this.variant = variant;
		this.secondPhase = secondPhase;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		// 胸甲无视生命值要求：把该套装的阶段技能全部解放
		if (getSlot() == EquipmentSlot.CHEST) {
			list.add(MGLangData.SKILL_IGNORE_HEALTH.get(LMArmorHelper.materialName(material))
					.withStyle(ChatFormatting.GOLD));
		}
		addTraitTooltip(list);
	}

	/** 头盔 / 胸甲 / 护腿各自的专属特性说明（每套不同，按材质与槽位区分）。 */
	private void addTraitTooltip(List<Component> list) {
		// 数值不加颜色，跟随整行的金色
		String pct = Math.round(LMArmorHelper.LEGS_DAMAGE_BONUS * 100) + "%";
		switch (material) {
			case "obliterator" -> {
				if (getSlot() == EquipmentSlot.HEAD) {
					list.add(MGLangData.LM_OBL_HEAD.get().withStyle(ChatFormatting.GOLD));
				} else if (getSlot() == EquipmentSlot.LEGS) {
					list.add(MGLangData.SKILL_DAMAGE_BOOST.get(pct,
							LMArmorHelper.skillList(LMCompatRegistry.OBLITERATOR_JUMP.get(),
									LMCompatRegistry.OBLITERATOR_CLONE_BURST.get()))
							.withStyle(ChatFormatting.GOLD));
				}
			}
			case "cloud" -> {
				if (getSlot() == EquipmentSlot.HEAD) {
					// 浮云头盔：落云/雷电拳/天穹激光 伤害 +50%
					list.add(MGLangData.SKILL_DAMAGE_BOOST.get(pct,
							LMArmorHelper.skillList(LMCompatRegistry.LINE_CLOUD.get(),
									LMCompatRegistry.CLOUD_DROP.get(),
									LMCompatRegistry.THUNDER.get(),
									LMCompatRegistry.SKY_LASER.get()))
							.withStyle(ChatFormatting.GOLD));
				} else if (getSlot() == EquipmentSlot.CHEST) {
					list.add(MGLangData.LM_CLOUD_CHEST.get(
							LMArmorHelper.skillName(LMCompatRegistry.CLOUD_BLOCK.get()))
							.withStyle(ChatFormatting.GOLD));
				} else if (getSlot() == EquipmentSlot.LEGS) {
					list.add(MGLangData.SKILL_DAMAGE_BOOST.get(pct,
							LMArmorHelper.skillList(LMCompatRegistry.SKYFALL_SMASH.get(),
									LMCompatRegistry.CLOUD_CHARGE.get()))
							.withStyle(ChatFormatting.GOLD));
				}
			}
			case "paladin" -> {
				if (getSlot() == EquipmentSlot.HEAD) {
					list.add(MGLangData.LM_PAL_HEAD.get(
							LMArmorHelper.skillName(LMCompatRegistry.PALADIN_SOUL_DAGGER.get()))
							.withStyle(ChatFormatting.GOLD));
				} else if (getSlot() == EquipmentSlot.LEGS) {
					list.add(MGLangData.SKILL_DAMAGE_BOOST.get(pct,
							LMArmorHelper.skillList(LMCompatRegistry.PALADIN_SOUL_BLADE_LEAP.get(),
									LMCompatRegistry.PALADIN_ROLL_SLASH.get()))
							.withStyle(ChatFormatting.GOLD));
				}
			}
			default -> {
			}
		}
	}

	/**
	 * 额外属性，照灾变各套盔甲的 {@code addExtraModifiers}：
	 * <ul>
	 *   <li>堕落圣骑 → 风暴盔甲</li>
	 *   <li>湮灭 → 末影守卫盔甲</li>
	 *   <li>浮云 → 咒魂盔甲</li>
	 * </ul>
	 * <p>
	 * 不能写在 {@code addExtraModifiers} 里：它在父类构造器中就会被调用，那时子类字段 {@code material}
	 * 还没赋值，{@code switch (material)} 会 NPE。改在 {@code getDefaultAttributeModifiers} 运行时拼装。
	 */
	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		Multimap<Attribute, AttributeModifier> base = super.getDefaultAttributeModifiers(slot);
		if (slot != getSlot()) return base;
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.putAll(base);
		UUID uuid = UUID.get(slot);
		switch (material) {
			case "paladin" -> {
				// 风暴盔甲：伤害吸收 +1；头盔回血 +1 / 胸甲范围攻击 +1 / 护腿移速 +50%
				builder.put(L2DamageTracker.ABSORB.get(), new AttributeModifier(uuid, "Paladin Armor", 1, AttributeModifier.Operation.ADDITION));
				switch (slot) {
					case HEAD -> builder.put(GolemTypes.GOLEM_REGEN.get(), new AttributeModifier(uuid,
							"Paladin Armor", 1, AttributeModifier.Operation.ADDITION));
					case CHEST -> builder.put(GolemTypes.GOLEM_SWEEP.get(), new AttributeModifier(uuid,
							"Paladin Armor", 1, AttributeModifier.Operation.ADDITION));
					case LEGS -> builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid,
							"Paladin Armor", 0.5, AttributeModifier.Operation.MULTIPLY_BASE));
					default -> {
					}
				}
			}
			case "obliterator" -> {
				// 末影守卫盔甲：受伤减免 -20%；头盔回血 +1 / 胸甲范围攻击 +1 / 护腿移速 +50%
				builder.put(L2DamageTracker.REDUCTION.get(), new AttributeModifier(uuid, "Obliterator Armor", -0.2, AttributeModifier.Operation.MULTIPLY_TOTAL));
				switch (slot) {
					case HEAD -> builder.put(GolemTypes.GOLEM_REGEN.get(), new AttributeModifier(uuid,
							"Obliterator Armor", 1, AttributeModifier.Operation.ADDITION));
					case CHEST -> builder.put(GolemTypes.GOLEM_SWEEP.get(), new AttributeModifier(uuid,
							"Obliterator Armor", 1, AttributeModifier.Operation.ADDITION));
					case LEGS -> builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid,
							"Obliterator Armor", 0.5, AttributeModifier.Operation.MULTIPLY_BASE));
					default -> {
					}
				}
			}
			case "cloud" -> {
				// 咒魂盔甲：伤害吸收 +1；头盔回血 +1 / 胸甲动态减伤 +1 / 护腿移速 +50%
				builder.put(L2DamageTracker.ABSORB.get(), new AttributeModifier(uuid, "Cloud Armor", 1, AttributeModifier.Operation.ADDITION));
				switch (slot) {
					case HEAD -> builder.put(GolemTypes.GOLEM_REGEN.get(), new AttributeModifier(uuid,
							"Cloud Armor", 1, AttributeModifier.Operation.ADDITION));
					case CHEST -> builder.put(GolemTypes.DYNAMIC_REDUCTION.get(), new AttributeModifier(uuid,
							"Cloud Armor", 1, AttributeModifier.Operation.ADDITION));
					case LEGS -> builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid,
							"Cloud Armor", 0.5, AttributeModifier.Operation.MULTIPLY_BASE));
					default -> {
					}
				}
			}
			default -> {
			}
		}
		return builder.build();
	}

	@Override
	public boolean emissive(LivingEntity user, ItemStack stack) {
		return true;
	}

	@Override
	protected String namespace(String def) {
		return LMDispatch.MODID;
	}

	@Override
	public ResourceLocation getModelTexture(LivingEntity user) {
		return texture(user, "");
	}

	@Override
	public ResourceLocation getEmissiveModelTexture(LivingEntity user) {
		return texture(user, "_emissive");
	}

	private ResourceLocation texture(LivingEntity user, String emissive) {
		ResourceLocation rl = ForgeRegistries.ITEMS.getKey(this);
		assert rl != null;
		String suffix = "";
		if (variant != null && secondPhase != null && user != null && secondPhase.test(user)) {
			suffix = variant;
		}
		return new ResourceLocation(namespace(rl.getNamespace()),
				"textures/equipments/" + rl.getPath() + suffix + emissive + ".png");
	}

	/** 浮云：二阶段换成暗色形态。穿了浮云胸甲时恒为二阶段。 */
	public static Predicate<LivingEntity> cloudPhase() {
		return e -> e instanceof AbstractGolemEntity<?, ?> g
				&& GolemFormAppearance.visualFinal(g, LMArmorHelper.cloudFinalForm(g), LMArmorHelper.PHASE_TWO);
	}

	/** 圣骑：二阶段换成被侵蚀形态。穿了灵魂骑士胸甲时恒为二阶段。 */
	public static Predicate<LivingEntity> paladinPhase() {
		return e -> e instanceof AbstractGolemEntity<?, ?> g
				&& GolemFormAppearance.visualFinal(g, LMArmorHelper.paladinFinalForm(g), LMArmorHelper.PHASE_TWO);
	}

}