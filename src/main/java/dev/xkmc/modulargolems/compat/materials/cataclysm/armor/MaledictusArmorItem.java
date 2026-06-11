package dev.xkmc.modulargolems.compat.materials.cataclysm.armor;

import com.google.common.collect.ImmutableMultimap;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.client.Minecraft;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class MaledictusArmorItem extends MetalGolemArmorItem {

	public MaledictusArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, ResourceLocation model) {
		super(properties, type, defense, toughness, model);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		if (getSlot() == EquipmentSlot.HEAD) {
			list.add(MGLangData.MALEDICTUS_BYPASS_CD.get());
		} else if (getSlot() == EquipmentSlot.LEGS) {
			list.add(MGLangData.MALEDICTUS_FAST_SKILL.get());
		} else if (getSlot() == EquipmentSlot.CHEST) {
			int cd = MGConfig.COMMON.maledictusReviveCD.get();
			int red = MGConfig.COMMON.maledictusReviveCDPartReduction.get();
			double php = MGConfig.COMMON.maledictusRevivePHP.get();
			double bonus = MGConfig.COMMON.maledictusRevivePHPPartBonus.get();
			list.add(MGLangData.MALEDICTUS_REVIVE.get((int) (php * 100), cd));
			list.add(MGLangData.MALEDICTUS_REVIVE_CD.get(red, bonus * 100));
			if (level != null && level.isClientSide()) {
				long prev = stack.getOrCreateTag().getLong("NextAvailableTime");
				long time = level.getGameTime();
				if (prev > time) {
					list.add(MGLangData.MALEDICTUS_REVIVE_IN_CD.get((prev - time) / 20));
				} else {
					int count = ClientHandler.counter();
					if (count > 0) {
						list.add(MGLangData.MALEDICTUS_REVIVE_ACTUAL_CD.get(cd - red * count));
					}
				}
			}
		}
	}

	@Override
	public boolean emissive(LivingEntity user, ItemStack stack) {
		long prev = stack.getOrCreateTag().getLong("NextAvailableTime");
		long time = user.level().getGameTime();
		return getSlot() == EquipmentSlot.CHEST && prev <= time;
	}

	@Override
	protected String namespace(String def) {
		return CataDispatch.MODID;
	}

	@Override
	protected void addExtraModifiers(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
		super.addExtraModifiers(builder);
		UUID uuid = UUID.get(getSlot());
		builder.put(L2DamageTracker.ABSORB.get(), new AttributeModifier(uuid, "Maledictus Armor", 1, AttributeModifier.Operation.ADDITION));
		switch (getSlot()) {
			case HEAD -> builder.put(GolemTypes.GOLEM_REGEN.get(), new AttributeModifier(uuid,
					"Maledictus Armor", 1, AttributeModifier.Operation.ADDITION));
			case CHEST -> builder.put(GolemTypes.DYNAMIC_REDUCTION.get(), new AttributeModifier(uuid,
					"Maledictus Armor", 1, AttributeModifier.Operation.ADDITION));
			case LEGS -> builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid,
					"Maledictus Armor", 0.5, AttributeModifier.Operation.MULTIPLY_BASE));
		}
	}

	public static int getCount(MetalGolemEntity golem) {
		int count = 0;
		var mat = CataCompatRegistry.cataLoc("cursium");
		for (var e : golem.getMaterials())
			if (e.id().equals(mat)) count++;
		if (golem.getItemBySlot(EquipmentSlot.HEAD).is(CataCompatRegistry.MALEDICTUS_HELMET.get())) count++;
		if (golem.getItemBySlot(EquipmentSlot.LEGS).is(CataCompatRegistry.MALEDICTUS_SHINGUARD.get())) count++;
		return count;
	}

	public static class ClientHandler {

		public static int counter() {
			var player = Minecraft.getInstance().player;
			if (player == null) return -1;
			if (player.containerMenu instanceof EquipmentsMenu menu) {
				if (menu.golem instanceof MetalGolemEntity e) {
					return getCount(e);
				}
			}
			return -1;
		}

	}

}
