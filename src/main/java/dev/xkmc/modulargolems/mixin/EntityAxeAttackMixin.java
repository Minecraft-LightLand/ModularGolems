package dev.xkmc.modulargolems.mixin;

import com.bobmowzie.mowziesmobs.server.entity.effects.EntityAxeAttack;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "com.bobmowzie.mowziesmobs.server.entity.effects.EntityAxeAttack")
public class EntityAxeAttackMixin {

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lcom/bobmowzie/mowziesmobs/server/entity/effects/EntityAxeAttack;dealDamage(FFFF)V"))
	public void modulargolems$dealDamage(EntityAxeAttack self, float damage, float range, float arc, float applyKnockback, Operation<Void> original) {
		if (self.getCaster() instanceof AbstractGolemEntity<?, ?> golem)
			damage = Math.max(damage, (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE));
		original.call(self, damage, range, arc, applyKnockback);
	}

}
