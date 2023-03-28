package dev.xkmc.modulargolems.mixin;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Inject(at = @At("HEAD"), method = "findLightningRod", cancellable = true)
	public void findLightningRod(BlockPos blockpos, CallbackInfoReturnable<Optional<BlockPos>> cir) {
		ServerLevel self = (ServerLevel) (Object) this;
		Optional<BlockPos> optional = self.getPoiManager().findClosest((poi) -> poi.is(PoiTypes.LIGHTNING_ROD),
				(pos) -> pos.getY() == self.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()) - 1,
				blockpos, 128, PoiManager.Occupancy.ANY);
		if (optional.isPresent()) cir.setReturnValue(optional);

		AABB aabb = (new AABB(blockpos, new BlockPos(blockpos.getX(), self.getMaxBuildHeight(), blockpos.getZ()))).inflate(64);
		List<AbstractGolemEntity> list = self.getEntitiesOfClass(AbstractGolemEntity.class, aabb, (e) -> e != null &&
				e.isAlive() && self.canSeeSky(e.blockPosition()) &&
				(Integer) e.getModifiers().getOrDefault(GolemModifiers.THUNDER_IMMUNE.get(), 0) > 0);
		if (list.size() > 0) {
			cir.setReturnValue(Optional.of(list.get(self.random.nextInt(list.size())).blockPosition()));
		}
	}

}
