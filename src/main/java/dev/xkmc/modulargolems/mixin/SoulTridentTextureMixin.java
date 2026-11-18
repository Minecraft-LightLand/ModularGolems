package dev.xkmc.modulargolems.mixin;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFormAppearance;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.SoulTridentEntity;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 堕魂掷戟的三叉戟贴图随形态外观切换：默认/最终用红色（本体原贴图），
 * 强制初始（或跟随血量未达阈值）时换成非红贴图。
 */
@Pseudo
@Mixin(targets = "net.miauczel.legendary_monsters.entity.ProjectileEntityRenderer.SoulTridentRenderer")
public class SoulTridentTextureMixin {

	private static final ResourceLocation NORMAL = new ResourceLocation("legendary_monsters",
			"textures/entity/posessed_paladin/layer/possessed_paladin_trident_layer.png");

	@Inject(method = "getTextureLocation", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
	private void mg$formAppearanceTexture(SoulTridentEntity entity, CallbackInfoReturnable<ResourceLocation> cir) {
		if (entity.getOwner() instanceof AbstractGolemEntity<?, ?> golem
				&& !GolemFormAppearance.visualFinal(golem, LMArmorHelper.paladinFinalForm(golem), LMArmorHelper.PHASE_TWO)) {
			cir.setReturnValue(NORMAL);
		}
	}

}
