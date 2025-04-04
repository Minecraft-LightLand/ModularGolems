package dev.xkmc.modulargolems.mixin;

import dev.xkmc.modulargolems.content.entity.common.GuardedEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

	@Inject(at = @At("HEAD"), method = "setRemoved")
	public void modulargolems$setRemoved(Entity.RemovalReason reason, CallbackInfo ci) {
		Object self = this;
		if (self instanceof GuardedEntity e) {
			e.onRemove(reason);
		}
	}

}
