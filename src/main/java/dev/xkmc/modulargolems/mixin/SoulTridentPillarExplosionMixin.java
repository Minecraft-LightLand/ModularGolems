package dev.xkmc.modulargolems.mixin;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFormAppearance;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.SoulPillarExplosionEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.SoulTridentEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 堕魂掷戟命中后的灵魂柱爆炸（本体硬编码 isRed=true）随形态外观改色：
 * 强制初始 / 跟随血量未达阈值时生成非红灵魂柱。
 */
@Pseudo
@Mixin(targets = "net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.SoulTridentEntity")
public class SoulTridentPillarExplosionMixin {

	@Redirect(method = "spawnSoulPillarExplosions",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"),
			require = 0)
	private boolean mg$formAppearanceRed(Level world, Entity entity) {
		if (entity instanceof SoulPillarExplosionEntity explosion) {
			Object self = this;
			if (self instanceof SoulTridentEntity trident
					&& trident.getOwner() instanceof AbstractGolemEntity<?, ?> golem) {
				explosion.setRed(GolemFormAppearance.visualFinal(golem, true, LMArmorHelper.PHASE_TWO));
			}
		}
		return world.addFreshEntity(entity);
	}

	/** 命中落点的大范围特效（本体硬编码 SOUL_EXPLOSION_RED）：按外观换成非红版本。 */
	@Redirect(method = "onHit",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"),
			require = 0)
	private void mg$formAppearanceExplosion(Level level, ParticleOptions type, double x, double y, double z,
											double dx, double dy, double dz) {
		ParticleOptions chosen = type;
		if (type == ModParticles.SOUL_EXPLOSION_RED.get()) {
			Object self = this;
			if (self instanceof SoulTridentEntity trident
					&& trident.getOwner() instanceof AbstractGolemEntity<?, ?> golem
					&& !GolemFormAppearance.visualFinal(golem, true, LMArmorHelper.PHASE_TWO)) {
				chosen = ModParticles.SOUL_EXPLOSION.get();
			}
		}
		level.addParticle(chosen, x, y, z, dx, dy, dz);
	}

}
