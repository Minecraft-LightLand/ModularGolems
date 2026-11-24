package dev.xkmc.modulargolems.mixin.compat;

import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.SoulPillarExplosionEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 堕魂掷戟/灵魂柱爆炸的预警圈与大团粒子按形态外观换色。
 * <p>
 * 本体预警圈颜色字段 uR/uG/uB 默认写死红色、从不读取 isRed；大团粒子固定用红色贴图
 * {@code soul_pillar_explosion}（纯红通道，乘顶点色只会变成暗红，无法变成蓝）。因此：
 * 非红形态时把预警圈改成青绿色，并把大团粒子换成本体现成的非红贴图 {@code soul_explosion}。
 */
@Pseudo
@Mixin(targets = "net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.SoulPillarExplosionEntity")
public class SoulPillarExplosionEntityAppearanceMixin {

	@Shadow
	public float uR;
	@Shadow
	public float uG;
	@Shadow
	public float uB;

	@Inject(method = "tick", at = @At("HEAD"), require = 0, remap = false)
	private void mg$ringColor(CallbackInfo ci) {
		boolean red = ((SoulPillarExplosionEntity) (Object) this).getRed();
		this.uR = red ? 1f : 0f;
		this.uG = red ? 0f : 0.9f;
		this.uB = red ? 0f : 0.8f;
	}

	/** 起手/爆炸的地面灵魂粒子本体硬编码 GROUNDSOUL_RED；非红形态换成 GROUNDSOUL。 */
	@Redirect(method = "tick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"),
			require = 0)
	private void mg$groundSoul(Level level, ParticleOptions type, double x, double y, double z,
							   double dx, double dy, double dz) {
		ParticleOptions chosen = type;
		if (type == ModParticles.GROUNDSOUL_RED.get()
				&& !((SoulPillarExplosionEntity) (Object) this).getRed()) {
			chosen = ModParticles.GROUNDSOUL.get();
		}
		level.addParticle(chosen, x, y, z, dx, dy, dz);
	}

	@Redirect(method = "tick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addAlwaysVisibleParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"),
			require = 0)
	private void mg$explosionParticle(Level level, ParticleOptions type, double x, double y, double z,
									  double dx, double dy, double dz) {
		ParticleOptions chosen = ((SoulPillarExplosionEntity) (Object) this).getRed()
				? type : ModParticles.SOULSTRIKE.get();
		level.addAlwaysVisibleParticle(chosen, x, y, z, dx, dy, dz);
	}

}
