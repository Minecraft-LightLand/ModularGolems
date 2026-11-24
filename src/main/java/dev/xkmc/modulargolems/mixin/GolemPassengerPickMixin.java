package dev.xkmc.modulargolems.mixin;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.wand.GolemInteractItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

/**
 * 手持手杖时，若射线先命中了傀儡本体，但本体的碰撞箱里还坐着别的傀儡（肩鞍乘客），
 * 改判为那个乘客。
 * <p>
 * 泰坦体型的大型傀儡碰撞箱很大，会把肩上坐着的人形傀儡整个包住；射线永远先命中本体，
 * 于是手杖指不到里面那只，完全无法交互。这里在拾取结果返回前，把乘客的碰撞箱再裁剪一遍，
 * 命中就换成乘客（取最近的那个）。
 * <p>
 * 只在手持 {@link GolemInteractItem}（手杖）时生效，普通攻击/其它拾取行为不受影响。
 */
@Pseudo
@Mixin(ProjectileUtil.class)
public class GolemPassengerPickMixin {

	@Inject(method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;",
			at = @At("RETURN"), require = 0, cancellable = true)
	private static void mg$pickGolemPassenger(Entity picker, Vec3 from, Vec3 to, AABB box, Predicate<Entity> filter,
											  double maxDist, CallbackInfoReturnable<EntityHitResult> ci) {
		EntityHitResult res = ci.getReturnValue();
		if (res == null) return;
		if (!(picker instanceof Player player)) return;
		if (!(player.getMainHandItem().getItem() instanceof GolemInteractItem)
				&& !(player.getOffhandItem().getItem() instanceof GolemInteractItem)) return;
		if (!(res.getEntity() instanceof AbstractGolemEntity<?, ?> golem)) return;
		if (golem.getPassengers().isEmpty()) return;
		Entity best = null;
		Vec3 bestPos = null;
		double bestDist = maxDist;
		for (Entity e : golem.getIndirectPassengers()) {
			if (!filter.test(e)) continue;
			AABB aabb = e.getBoundingBox().inflate(e.getPickRadius());
			var opt = aabb.clip(from, to);
			if (opt.isEmpty()) continue;
			double d = from.distanceToSqr(opt.get());
			if (d < bestDist) {
				bestDist = d;
				best = e;
				bestPos = opt.get();
			}
		}
		if (best != null) ci.setReturnValue(new EntityHitResult(best, bestPos));
	}

}
