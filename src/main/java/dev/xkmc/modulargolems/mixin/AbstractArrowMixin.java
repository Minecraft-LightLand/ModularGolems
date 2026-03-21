package dev.xkmc.modulargolems.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractArrow.class)
public class AbstractArrowMixin {

	@WrapWithCondition(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
	public boolean modulargolems$pierce(AbstractArrow instance, Vec3 vec3) {
		return instance.getPierceLevel() == 0 || !(instance.getOwner() instanceof MetalGolemEntity);
	}

}
