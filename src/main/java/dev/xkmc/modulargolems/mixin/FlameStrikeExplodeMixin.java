package dev.xkmc.modulargolems.mixin;

import com.github.L_Ender.cataclysm.entity.effect.Flame_Strike_Entity;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 灾变本体里，灵魂形态的 {@code Flame_Strike_Entity} 结束时不会爆炸（{@code tick()} 中
 * {@code if (!isSoul())} 才触发 {@code IgnisExplosion}）。
 * <p>
 * 这里只对**傀儡召唤**的符文放行，使其与普通符文一样在结束时爆炸；焰魔本体及其它来源的
 * 灵魂符文保持原样（仍不爆炸），避免改变灾变本身的战斗设计。
 */
@Pseudo
@Mixin(targets = "com.github.L_Ender.cataclysm.entity.effect.Flame_Strike_Entity")
public class FlameStrikeExplodeMixin {

	/**
	 * 拦截 {@code tick()} 里爆炸判定处对 {@code isSoul()} 的调用（ordinal=1；ordinal=0 是粒子颜色）。
	 * 返回 false 即进入爆炸分支。
	 * <p>
	 * 被拦截的是实例方法，因此处理器第一个参数必须是接收者本身。
	 * {@code isSoul()} 是灾变自有方法，不在 refmap 里，注入点需 {@code remap = false}。
	 */
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
