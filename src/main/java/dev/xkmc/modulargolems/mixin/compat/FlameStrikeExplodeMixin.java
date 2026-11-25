package dev.xkmc.modulargolems.mixin.compat;

import com.github.L_Ender.cataclysm.entity.effect.Flame_Strike_Entity;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 灾变本体里，灵魂形态的 Flame_Strike_Entity 结束时不会爆炸（tick() 中 !isSoul() 才触发 IgnisExplosion）。
 * 这里只对傀儡召唤的符文放行，使其与普通符文一样在结束时爆炸；焰魔本体保持原样。
 */
@Pseudo
@Mixin(targets = "com.github.L_Ender.cataclysm.entity.effect.Flame_Strike_Entity")
public class FlameStrikeExplodeMixin {

	@Redirect(method = "tick",
			at = @At(value = "INVOKE",
					target = "Lcom/github/L_Ender/cataclysm/entity/effect/Flame_Strike_Entity;isSoul()Z",
					ordinal = 1,
					remap = false),
			require = 0)
	private boolean mg_forceExplodeForGolem(Flame_Strike_Entity self) {
		boolean soul = self.isSoul();
		if (soul && self.getOwner() instanceof AbstractGolemEntity<?, ?>) {
			return false;
		}
		return soul;
	}

}
