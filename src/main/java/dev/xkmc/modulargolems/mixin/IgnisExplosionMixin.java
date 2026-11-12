package dev.xkmc.modulargolems.mixin;

import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
import dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers.IgnisFireballModifier;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Pseudo
@Mixin(targets = "com.github.L_Ender.cataclysm.util.CustomExplosion.IgnisExplosion")
public abstract class IgnisExplosionMixin extends Explosion {

	@Shadow
	@Final
	private ObjectArrayList<BlockPos> toBlow;

	@Shadow
	@Final
	@Mutable
	private float radius;

	public IgnisExplosionMixin(Level p_46024_, @Nullable Entity p_46025_, double p_46026_, double p_46027_, double p_46028_, float p_46029_, List<BlockPos> p_46030_) {
		super(p_46024_, p_46025_, p_46026_, p_46027_, p_46028_, p_46029_, p_46030_);
	}

	/**
	 * 腾炎头盔：火球爆炸范围增加。
	 * <p>
	 * 本体 {@code Ignis_Fireball_Entity} 把爆炸半径写死成 1，没法从外部传参。这里在
	 * {@code explode()} 开头按施法傀儡是否戴腾炎头盔把半径放大——爆炸伤害、击退和粒子
	 * 全部读这个字段，等价于整体放大范围。
	 * <p>
	 * 注入点选 {@code explode()}（无参方法）而不是构造器：构造器的参数描述符里有 MC 类型，
	 * 用 {@code remap = false} 在正式环境会因 SRG 名不匹配而找不到方法。
	 */
	@Inject(method = "explode", at = @At("HEAD"), require = 0, remap = false)
	private void mg$enlargeIgnisExplosion(CallbackInfo ci) {
		if (getIndirectSourceEntity() instanceof AbstractGolemEntity<?, ?> golem
				&& CataArmorHelper.ignisHelmet(golem)) {
			radius *= 1.0F + IgnisFireballModifier.EXPLOSION_RADIUS_BONUS;
		}
	}

	@Override
	public void clearToBlow() {
		toBlow.clear();
	}

	@Override
	public @NotNull List<BlockPos> getToBlow() {
		return toBlow;
	}

}
