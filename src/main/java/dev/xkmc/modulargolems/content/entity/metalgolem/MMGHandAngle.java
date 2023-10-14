package dev.xkmc.modulargolems.content.entity.metalgolem;

import dev.xkmc.modulargolems.content.item.equipments.MetalGolemWeaponItem;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;

public class MMGHandAngle {
	public static void HaveWeapon(MetalGolemEntity entity, ModelPart mp1, ModelPart mp2) {
		if (entity.getItemBySlot(EquipmentSlot.MAINHAND).getItem() instanceof MetalGolemWeaponItem) {
			mp1.xRot = 0F + 0F;
			mp2.xRot = -1.5F + 0F;
		}
	}
	public static void NoWeapon(ModelPart mp1, ModelPart mp2) {
			mp1.xRot = 0F + 0F;
			mp2.xRot = 0F + 0F;
	}
	public static void warning(MetalGolemEntity entity,ModelPart mp1) {
		if (entity.getItemBySlot(EquipmentSlot.MAINHAND).getItem() instanceof MetalGolemWeaponItem) {
			mp1.xRot = -4.8F + 0F;
		}
	}
}
