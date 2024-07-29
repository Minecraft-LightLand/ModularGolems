package dev.xkmc.modulargolems.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.item.equipments.GolemEquipmentItem;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BiConsumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;travel(Lnet/minecraft/world/phys/Vec3;)V"), method = "aiStep")
	public void modulargolems$travelRiddenByGolem(LivingEntity le, Vec3 vec3, Operation<Void> op) {
		if (le.getControllingPassenger() instanceof AbstractGolemEntity<?, ?> &&
				!(le instanceof AbstractGolemEntity<?, ?>)) {
			op.call(le, vec3.normalize());
		} else {
			op.call(le, vec3);
		}
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V"), method = "collectEquipmentChanges")
	public void modulargolems$collectEquipmentChanges$specialEquipment(ItemStack stack, EquipmentSlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> action, Operation<Void> op) {
		if (stack.getItem() instanceof GolemEquipmentItem item) {
			item.forEachModifier(stack, this, slot, action);
		} else {
			op.call(stack, slot, action);
		}
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;position()Lnet/minecraft/world/phys/Vec3;"), method = "dropExperience")
	public Vec3 modulargolems$dropExperience$moveToGolem(LivingEntity killed, Operation<Vec3> original) {
		if (killed.getLastHurtMob() instanceof AbstractGolemEntity<?, ?> e) {
			if (e.hasFlag(GolemFlags.PICKUP)) {
				return e.position();
			}
		}
		return original.call(killed);
	}

}
