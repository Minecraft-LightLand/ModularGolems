package dev.xkmc.modulargolems.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Pseudo
@Mixin(targets = "com.github.L_Ender.cataclysm.util.CustomExplosion.IgnisExplosion")
public abstract class IgnisExplosionMixin extends Explosion {

	@Shadow
	@Final
	private ObjectArrayList<BlockPos> toBlow;

	public IgnisExplosionMixin(Level p_46051_, @Nullable Entity p_46052_, double p_46055_, double p_46056_, double p_46057_, float p_46058_, List<BlockPos> p_312600_, BlockInteraction p_46060_, ParticleOptions p_312560_, ParticleOptions p_312844_, Holder<SoundEvent> p_320054_) {
		super(p_46051_, p_46052_, p_46055_, p_46056_, p_46057_, p_46058_, p_312600_, p_46060_, p_312560_, p_312844_, p_320054_);
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
