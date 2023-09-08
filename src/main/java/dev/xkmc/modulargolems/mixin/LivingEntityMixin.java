package dev.xkmc.modulargolems.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;travel(Lnet/minecraft/world/phys/Vec3;)V"), method = "aiStep")
	public void l2hostility$travelRiddenByGolem(LivingEntity le, Vec3 vec3, Operation<Void> op) {
		if (le.getControllingPassenger() instanceof AbstractGolemEntity<?, ?> &&
				!(le instanceof AbstractGolemEntity<?, ?>)) {
			op.call(le, vec3.normalize());
		} else {
			op.call(le, vec3);
		}
	}

}
