package dev.xkmc.modulargolems.mixin;

import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(ExperienceOrb.class)
public interface ExperienceOrbAccessor {

	@Accessor
	int getCount();

	@Accessor
	void setCount(int count);

}
