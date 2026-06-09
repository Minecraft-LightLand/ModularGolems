package dev.xkmc.modulargolems.compat.materials.cataclysm.armor;

import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataDispatch;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemArmorItem;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.client.Minecraft;
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

import java.util.List;

public class MaledictusArmorItem extends MetalGolemArmorItem {

	public MaledictusArmorItem(Properties properties, ArmorItem.Type type, int defense, float toughness, ResourceLocation model) {
		super(properties, type, defense, toughness, model, e -> {
			var id = ModularGolems.loc("maledictus_armor_" + type.getName());
			e.add(L2DamageTracker.ABSORB, new AttributeModifier(id, 1,
					AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()));
			switch (type.getSlot()) {
				case HEAD -> e.add(GolemTypes.GOLEM_REGEN, new AttributeModifier(
						id, 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()));
				case CHEST -> e.add(GolemTypes.DYNAMIC_REDUCTION, new AttributeModifier(
						id, 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(type.getSlot()));
				case LEGS -> e.add(Attributes.MOVEMENT_SPEED, new AttributeModifier(
						id, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.bySlot(type.getSlot()));
			}
		});
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, ctx, list, flag);
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
			var level = ctx.level();
			if (level != null && level.isClientSide()) {
				long prev = GolemItems.DC_TIMESTAMP.getOrDefault(stack, 0L);
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
		long prev = GolemItems.DC_TIMESTAMP.getOrDefault(stack, 0L);
		long time = user.level().getGameTime();
		return getSlot() == EquipmentSlot.CHEST && prev <= time;
	}

	@Override
	protected String namespace(String def) {
		return CataDispatch.MODID;
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
