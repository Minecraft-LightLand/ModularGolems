package dev.xkmc.modulargolems.mixin;

import com.github.alexmodguy.alexscaves.client.sound.ItemTickableSound;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 让 AC 的循环物品音效对傀儡也能播。
 * <p>
 * {@code ItemTickableSound.canPlaySound()} 要求 {@code user.isUsingItem()}——因为原版这些音效都由
 * 物品的 {@code onUseTick} 驱动，而那意味着实体正按着右键。傀儡的阻尼盾极化是 MG 自己复刻的
 * （{@code PolarizeModifier}），实体并不处于"使用中"，于是音效永远播不出来。
 * <p>
 * 这里对傀儡去掉 {@code isUsingItem()} 这一条，其余（静音、手持物品匹配）保持不变。
 */
@Pseudo
@Mixin(ItemTickableSound.class)
public class ItemTickableSoundMixin {

	@Shadow
	@Final
	protected LivingEntity user;

	/**
	 * {@code tick()} 里也有一道 {@code user.isUsingItem()} 的闸门：不满足时 {@code itemStack} 保持空，
	 * 直接 {@code stop()}。只放宽 {@code canPlaySound()} 不够——音效请求发出去了，下一 tick 又会被停掉。
	 * 这里对傀儡把这道判断换成"手里有匹配物品"。
	 */
	@Redirect(method = "tick", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/entity/LivingEntity;isUsingItem()Z"), require = 0)
	private boolean mg$golemUsing(LivingEntity entity) {
		if (entity.isUsingItem()) return true;
		if (!(entity instanceof AbstractGolemEntity<?, ?>)) return false;
		ItemTickableSound self = (ItemTickableSound) (Object) this;
		return self.isValidItem(entity.getItemInHand(InteractionHand.MAIN_HAND))
				|| self.isValidItem(entity.getItemInHand(InteractionHand.OFF_HAND));
	}

	@Inject(method = "canPlaySound", at = @At("RETURN"), cancellable = true, require = 0)
	private void mg$allowGolem(CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue()) return;
		if (!(user instanceof AbstractGolemEntity<?, ?>)) return;
		if (user.isSilent()) return;
		ItemTickableSound self = (ItemTickableSound) (Object) this;
		boolean valid = self.isValidItem(user.getItemInHand(InteractionHand.MAIN_HAND))
				|| self.isValidItem(user.getItemInHand(InteractionHand.OFF_HAND));
		cir.setReturnValue(valid);
	}

}
