package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.google.common.collect.ImmutableMultimap;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class IgnisArmorItem extends MetalGolemArmorItem {

	public IgnisArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, ResourceLocation model) {
		super(properties, type, defense, toughness, model);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		switch (getSlot()) {
			case HEAD -> list.add(MGLangData.IGNIS_BOOST_FIREBALL
					.get(Math.round(MGConfig.COMMON.fireballArmorBonus.get() * 100) + "%")
					.withStyle(ChatFormatting.GOLD));
			case CHEST -> list.add(MGLangData.IGNIS_BOOST_SOUL.get()
					.withStyle(ChatFormatting.GOLD));
			case LEGS -> list.add(MGLangData.IGNIS_BOOST_STRIKE
					.get(Math.round(MGConfig.COMMON.flameStrikeArmorBonus.get() * 100) + "%")
					.withStyle(ChatFormatting.GOLD));
		}
	}

	@Override
	public boolean emissive() {
		return true;
	}

	@Override
	protected String namespace(String def) {
		return CataDispatch.MODID;
	}

	public ResourceLocation getModelTexture(LivingEntity user) {
		ResourceLocation rl = ForgeRegistries.ITEMS.getKey(this);
		assert rl != null;
		var str = "textures/equipments/" + rl.getPath();
		if (user.getHealth() < 0.5 * user.getMaxHealth())
			str += "_soul";
		return new ResourceLocation(namespace(rl.getNamespace()), str + ".png");
	}

	public ResourceLocation getEmissiveModelTexture(LivingEntity user) {
		ResourceLocation rl = ForgeRegistries.ITEMS.getKey(this);
		assert rl != null;
		var str = "textures/equipments/" + rl.getPath();
		if (user.getHealth() < 0.5 * user.getMaxHealth())
			str += "_soul";
		return new ResourceLocation(namespace(rl.getNamespace()), str + "_emissive.png");
	}

	@Override
	protected void addExtraModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
		super.addExtraModifiers(builder);
		UUID uuid = UUID.get(getSlot());
		builder.put(L2DamageTracker.REDUCTION.get(), new AttributeModifier(uuid, "Ignis Armor", -0.2, AttributeModifier.Operation.MULTIPLY_TOTAL));
		switch (getSlot()) {
			case HEAD -> builder.put(L2DamageTracker.EXPLOSION_FACTOR.get(), new AttributeModifier(uuid,
					"Ignis Armor", 1, AttributeModifier.Operation.ADDITION));
			case CHEST -> builder.put(GolemTypes.GOLEM_SWEEP.get(), new AttributeModifier(uuid,
					"Ignis Armor", 1, AttributeModifier.Operation.ADDITION));
			case LEGS -> builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid,
					"Ignis Armor", 0.5, AttributeModifier.Operation.MULTIPLY_BASE));
		}
	}

}
