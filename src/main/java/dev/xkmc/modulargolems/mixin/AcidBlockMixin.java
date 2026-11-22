package dev.xkmc.modulargolems.mixin;

import com.github.alexmodguy.alexscaves.server.block.AcidBlock;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 毒化洞穴酸液：给拥有 {@link GolemFlags#ACID_IMMUNE} 的傀儡完全免疫酸液。
 * <p>
 * AC 的 {@code AcidBlock#entityInside} 在造成伤害前还会遍历盔甲并随机损耗，
 * 只取消伤害事件挡不住这部分，所以直接在入口跳过。
 */
@Pseudo
@Mixin(AcidBlock.class)
public class AcidBlockMixin {

	@Inject(method = "entityInside", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
	private void mg$acidImmune(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
		if (entity instanceof AbstractGolemEntity<?, ?> golem && golem.hasFlag(GolemFlags.ACID_IMMUNE)) {
			ci.cancel();
		}
	}

}
