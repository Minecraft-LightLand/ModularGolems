package dev.xkmc.modulargolems.mixin;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.item.equipments.IGolemEquipmentItem;
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
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;travel(Lnet/minecraft/world/phys/Vec3;)V"), method = "aiStep")
	public Vec3 modulargolems$travelRiddenByGolem(Vec3 vec3) {
		LivingEntity le = (LivingEntity) (Object) this;
		if (le.getControllingPassenger() instanceof AbstractGolemEntity<?, ?> &&
				!(le instanceof AbstractGolemEntity<?, ?>)) {
			return vec3.normalize();
		}
		return vec3;
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getAttributeModifiers(Lnet/minecraft/world/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"), method = "collectEquipmentChanges")
	public Multimap<Attribute, AttributeModifier> modulargolems$collectEquipmentChanges$specialEquipment(ItemStack stack, EquipmentSlot slot, Operation<Multimap<Attribute, AttributeModifier>> op) {
		if (stack.getItem() instanceof IGolemEquipmentItem item) {
			return item.getGolemModifiers(stack, this, slot);
		} else {
			return op.call(stack, slot);
		}
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;position()Lnet/minecraft/world/phys/Vec3;"), method = "dropExperience")
	public Vec3 modulargolems$dropExperience$moveToGolem(LivingEntity killed, Operation<Vec3> original) {
		if (killed.getLastHurtByMob() instanceof AbstractGolemEntity<?, ?> e) {
			if (e.hasFlag(GolemFlags.PICKUP)) {
				return e.position();
			}
		}
		return original.call(killed);
	}

}
