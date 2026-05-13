package dev.xkmc.modulargolems.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

	@ModifyReturnValue(at = @At("RETURN"), method = "findLightningRod")
	public Optional<BlockPos> findLightningRod(Optional<BlockPos> original, @Local(argsOnly = true) BlockPos center) {
		if (original.isPresent()) return original;
		ServerLevel self = (ServerLevel) (Object) this;
		AABB aabb = (AABB.encapsulatingFullBlocks(center, new BlockPos(center.getX(), self.getMaxY(), center.getZ()))).inflate(64);
		List<AbstractGolemEntity> list = self.getEntitiesOfClass(AbstractGolemEntity.class, aabb, (e) -> e != null &&
				e.isAlive() && self.canSeeSky(e.blockPosition()) && e.hasFlag(GolemFlags.THUNDER_IMMUNE));
		if (!list.isEmpty()) {
			return Optional.of(list.get(self.getRandom().nextInt(list.size())).blockPosition());
		}
		return original;
	}

}
