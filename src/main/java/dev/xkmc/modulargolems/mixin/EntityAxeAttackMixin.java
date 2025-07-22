package dev.xkmc.modulargolems.mixin;

import com.bobmowzie.mowziesmobs.server.entity.effects.EntityAxeAttack;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(EntityAxeAttack.class)
public class EntityAxeAttackMixin {

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lcom/bobmowzie/mowziesmobs/server/entity/effects/EntityAxeAttack;dealDamage(FFFF)V"))
	public void modulargolems$dealDamage(EntityAxeAttack self, float damage, float range, float arc, float applyKnockback, Operation<Void> original) {
		if (self.getCaster() instanceof AbstractGolemEntity<?, ?> golem)
			damage = Math.max(damage, (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE));
		original.call(self, damage, range, arc, applyKnockback);
	}

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	public boolean modulargolems$hurt(Entity instance, DamageSource source, float amount, Operation<Boolean> original) {
		EntityAxeAttack self = (EntityAxeAttack) (Object) this;
		if (self.getCaster() instanceof AbstractGolemEntity<?, ?> golem)
			amount = Math.max(amount, (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE));
		return original.call(instance, source, amount);
	}

}
