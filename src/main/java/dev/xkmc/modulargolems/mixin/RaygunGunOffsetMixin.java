package dev.xkmc.modulargolems.mixin;

import com.github.alexmodguy.alexscaves.client.render.item.RaygunRenderHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修正傀儡射线枪的枪口高度。
 * <p>
 * AC 的 {@code RaygunRenderHelper#getGunOffset} 第三人称分支把抬升写死为
 * {@code getBbHeight() * 0.8F}，那个 0.8 是按玩家 1.8 格高配的；傀儡更高，
 * 按同一比例算出来的枪口偏低。傀儡用 1.05 才对齐模型里实际握枪的位置。
 * <p>
 * 只改返回值、不改渲染流程：发光后处理、贴图、双 pass 仍全部走 AC 原版。
 */
@Pseudo
@Mixin(RaygunRenderHelper.class)
public class RaygunGunOffsetMixin {

	@Inject(method = "getGunOffset", at = @At("RETURN"), remap = false, require = 0, cancellable = true)
	private static void mg$golemGunOffset(LivingEntity entity, float partialTicks, boolean firstPerson, boolean left,
			CallbackInfoReturnable<Vec3> ci) {
		if (firstPerson) return;
		if (!(entity instanceof AbstractGolemEntity<?, ?>)) return;
		int i = left ? -1 : 1;
		float yBodyRot = Mth.lerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
		Vec3 offset = new Vec3(entity.getBbWidth() * -0.5F * i, entity.getBbHeight() * 1.05F, 0)
				.yRot((float) Math.toRadians(-yBodyRot));
		Vec3 armViewExtra = entity.getViewVector(partialTicks).normalize().scale(1.5F);
		ci.setReturnValue(offset.add(armViewExtra));
	}

}
