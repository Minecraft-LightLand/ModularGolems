package dev.xkmc.modulargolems.compat.materials.cataclysm.armor;

import com.google.common.collect.ImmutableMultimap;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class StormArmorItem extends MetalGolemArmorItem {

	/**
	 * Serpents summoned at 2/3 health; 1/3 doubles it. Kept beside the tooltip that quotes it so the
	 * numbers cannot drift away from the skill.
	 */
	public static final int SERPENT_PAIR = 2;

	public StormArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness,
			float knockbackResistance, ResourceLocation model) {
		super(properties, type, defense, toughness, knockbackResistance, model);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		switch (getSlot()) {
			case HEAD -> list.add(MGLangData.SCYLLA_BYPASS_INVULNERABLE.get().withStyle(ChatFormatting.GOLD));
			case CHEST -> {
				list.add(MGLangData.SKILL_IGNORE_HEALTH.get(CataArmorHelper.materialName("storm"))
						.withStyle(ChatFormatting.GOLD));
				list.add(MGLangData.SCYLLA_SERPENT
						.get(CataArmorHelper.percentOf(CataArmorHelper.TWO_THIRDS) + "%",
								CataArmorHelper.skillName(CataCompatRegistry.SCYLLA_STORM.get()),
								SERPENT_PAIR, 
								CataArmorHelper.percentOf(CataArmorHelper.ONE_THIRD) + "%",
								SERPENT_PAIR * 2)
						.withStyle(ChatFormatting.GOLD));
			}
			case LEGS -> {
				list.add(MGLangData.SCYLLA_SPARK_BOOST
						.get(Math.round(MGConfig.COMMON.sparkArmorBonus.get() * 100) + "%")
						.withStyle(ChatFormatting.GOLD));
				list.add(MGLangData.SCYLLA_SMASH
						.get(CataArmorHelper.percentOf(CataArmorHelper.TWO_THIRDS) + "%",
								CataArmorHelper.skillName(CataCompatRegistry.SCYLLA_ANCHOR_SLAM.get()))
						.withStyle(ChatFormatting.GOLD));
			}
		}
	}

	@Override
	public boolean emissive(LivingEntity user, ItemStack stack) {
		// 只有胸甲贴图带发光像素；头盔/护腿没有 emissive 贴图，跳过以免绑定缺失纹理
		return getSlot() == net.minecraft.world.entity.EquipmentSlot.CHEST;
	}

	@Override
	protected String namespace(String def) {
		return CataDispatch.MODID;
	}

	@Override
	protected void addExtraModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
		super.addExtraModifiers(builder);
		UUID uuid = UUID.get(getSlot());
		switch (getSlot()) {
			case HEAD -> builder.put(GolemTypes.GOLEM_REGEN.get(), new AttributeModifier(uuid,
					"Storm Armor", 1, AttributeModifier.Operation.ADDITION));
			case CHEST -> builder.put(GolemTypes.GOLEM_SWEEP.get(), new AttributeModifier(uuid,
					"Storm Armor", 1, AttributeModifier.Operation.ADDITION));
			case LEGS -> builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid,
					"Storm Armor", 0.5, AttributeModifier.Operation.MULTIPLY_BASE));
		}
	}

}
