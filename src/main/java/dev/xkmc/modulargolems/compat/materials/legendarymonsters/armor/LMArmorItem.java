package dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor;

import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMDispatch;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFormAppearance;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;
import java.util.function.Predicate;

/**
 * 传奇怪物联动盔甲。与腾炎傀儡盔甲一样支持「形态变化贴图」。
 * 1.21 把额外属性从 getDefaultAttributeModifiers 挪到构造器 Consumer（按 material 拼装）。
 */
public class LMArmorItem extends MetalGolemArmorItem {

	private final Predicate<LivingEntity> secondPhase;
	private final String variant;
	private final String material;

	public LMArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness,
			float knockbackResistance, ResourceLocation model, String material, String variant,
			Predicate<LivingEntity> secondPhase) {
		super(properties, type, defense, toughness, model, e -> {
			addMaterialAttrs(e, type, material);
			MetalGolemArmorItem.addKnockback(e, type, knockbackResistance);
		});
		this.material = material;
		this.variant = variant;
		this.secondPhase = secondPhase;
	}

	private static void addMaterialAttrs(ItemAttributeModifiers.Builder e, ArmorItem.Type type, String material) {
		EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(type.getSlot());
		ResourceLocation id = ModularGolems.loc(type.getName() + "_lm_armor");
		switch (material) {
			case "paladin" -> {
				e.add(L2DamageTracker.ABSORB, new AttributeModifier(id, 1, AttributeModifier.Operation.ADD_VALUE), group);
				switch (type.getSlot()) {
					case HEAD -> e.add(GolemTypes.GOLEM_REGEN, new AttributeModifier(id, 1, AttributeModifier.Operation.ADD_VALUE), group);
					case CHEST -> e.add(GolemTypes.GOLEM_SWEEP, new AttributeModifier(id, 1, AttributeModifier.Operation.ADD_VALUE), group);
					case LEGS -> e.add(Attributes.MOVEMENT_SPEED, new AttributeModifier(id, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), group);
					default -> {
					}
				}
			}
			case "obliterator" -> {
				e.add(L2DamageTracker.REDUCTION, new AttributeModifier(id, -0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), group);
				switch (type.getSlot()) {
					case HEAD -> e.add(GolemTypes.GOLEM_REGEN, new AttributeModifier(id, 1, AttributeModifier.Operation.ADD_VALUE), group);
					case CHEST -> e.add(GolemTypes.GOLEM_SWEEP, new AttributeModifier(id, 1, AttributeModifier.Operation.ADD_VALUE), group);
					case LEGS -> e.add(Attributes.MOVEMENT_SPEED, new AttributeModifier(id, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), group);
					default -> {
					}
				}
			}
			case "cloud" -> {
				e.add(L2DamageTracker.ABSORB, new AttributeModifier(id, 1, AttributeModifier.Operation.ADD_VALUE), group);
				switch (type.getSlot()) {
					case HEAD -> e.add(GolemTypes.GOLEM_REGEN, new AttributeModifier(id, 1, AttributeModifier.Operation.ADD_VALUE), group);
					case CHEST -> e.add(GolemTypes.DYNAMIC_REDUCTION, new AttributeModifier(id, 1, AttributeModifier.Operation.ADD_VALUE), group);
					case LEGS -> e.add(Attributes.MOVEMENT_SPEED, new AttributeModifier(id, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), group);
					default -> {
					}
				}
			}
			default -> {
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		if (getSlot() == EquipmentSlot.CHEST) {
			list.add(MGLangData.SKILL_IGNORE_HEALTH.get(LMArmorHelper.materialName(material))
					.withStyle(ChatFormatting.GOLD));
		}
		addTraitTooltip(list);
	}

	private void addTraitTooltip(List<Component> list) {
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
		ResourceLocation rl = BuiltInRegistries.ITEM.getKey(this);
		assert rl != null;
		String suffix = "";
		if (variant != null && secondPhase != null && user != null && secondPhase.test(user)) {
			suffix = variant;
		}
		return ResourceLocation.fromNamespaceAndPath(namespace(rl.getNamespace()),
				"textures/equipments/" + rl.getPath() + suffix + emissive + ".png");
	}

	public static Predicate<LivingEntity> cloudPhase() {
		return e -> e instanceof AbstractGolemEntity<?, ?> g
				&& GolemFormAppearance.visualFinal(g, LMArmorHelper.cloudFinalForm(g), LMArmorHelper.PHASE_TWO);
	}

	public static Predicate<LivingEntity> paladinPhase() {
		return e -> e instanceof AbstractGolemEntity<?, ?> g
				&& GolemFormAppearance.visualFinal(g, LMArmorHelper.paladinFinalForm(g), LMArmorHelper.PHASE_TWO);
	}

}
