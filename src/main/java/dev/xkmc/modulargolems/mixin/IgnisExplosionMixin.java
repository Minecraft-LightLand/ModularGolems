package dev.xkmc.modulargolems.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
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

	@Shadow @Final private ObjectArrayList<BlockPos> toBlow;

	public IgnisExplosionMixin(Level p_46024_, @Nullable Entity p_46025_, double p_46026_, double p_46027_, double p_46028_, float p_46029_, List<BlockPos> p_46030_) {
		super(p_46024_, p_46025_, p_46026_, p_46027_, p_46028_, p_46029_, p_46030_);
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
