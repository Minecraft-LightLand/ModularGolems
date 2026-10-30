package dev.xkmc.modulargolems.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "com.github.alexmodguy.alexscaves.server.entity.item.GumballEntity")
public abstract class GumballEntityMixin {

	@Redirect(method = "onHitEntity", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"),
			require = 0)
	private Entity modulargolems$inheritSplitOwner(EntityType<?> type, Level level) {
		Entity spawned = type.create(level);
		Entity owner = ((Object) this) instanceof Projectile self ? self.getOwner() : null;
		if (owner != null && spawned instanceof Projectile projectile) {
			projectile.setOwner(owner);
		}
		return spawned;
	}

}
