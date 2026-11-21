package dev.xkmc.modulargolems.mixin;

import com.github.alexmodguy.alexscaves.server.entity.item.SeekingArrowEntity;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 追踪箭（{@code SeekingArrowEntity}）的友方过滤。
 * <p>
 * 原版 {@code tick()} 选目标时的条件是：
 * <pre>
 * !ownedBy(entity) &amp;&amp; (owner == null || !entity.isAlliedTo(owner))
 * </pre>
 * 它用 {@code entity.isAlliedTo(owner)} <b>单向</b>判定「目标是否与射手是友方」。
 * <p>
 * 而 vanilla {@code Entity#isAlliedTo(Entity)} <b>只比较 scoreboard team</b>：
 * <ul>
 *   <li>玩家射箭：owner 是玩家、目标也是玩家，team 比较能正常工作；</li>
 *   <li>傀儡射箭：owner 是傀儡，{@code Player.isAlliedTo(golem)} 恒为 false
 *       （玩家和傀儡不在同一 scoreboard team），于是玩家、主人、友方单位全被当成敌人，
 *       箭会追着它们打。</li>
 * </ul>
 * MG 覆写的 {@code AbstractGolemEntity#isAlliedTo} 只在「傀儡 → 别人」这个方向生效，
 * 救不了箭这里的反向调用；而傀儡的友方规则远不止 {@code isAlliedTo}——
 * {@code canAttack} 里还叠了 {@code GOLEM_FRIENDLY} tag、{@code targetFilter.friendlyToward}、
 * {@code GolemFlags.PASSIVE}、敌对阵营豁免等一整套判定。
 * <p>
 * 实现方式：{@code tick()} 的选目标发生在 {@code id == -1} 分支里，锁定后写
 * {@code setArcTowardsID}。这里在 {@code tick()} TAIL 检查已锁定目标，
 * 若 owner 是傀儡且该目标「不能打」，就清空锁定，让下一 tick 重新选目标。
 * 这样不需要 {@code @Redirect} 到 vanilla 方法，避免跨 mod 方法的 remap 歧义
 * （{@code method=tick} 是 AC 方法不该 remap，而 {@code target=isAlliedTo} 是 vanilla 该 remap，
 * 一个注解只有一个 remap 属性，冲突时必然有一侧在正式环境静默失效）。
 */
@Pseudo
@Mixin(SeekingArrowEntity.class)
public class SeekingArrowAllyMixin {

	@Shadow(remap = false)
	private int getArcTowardsID() {
		throw new AssertionError();
	}

	@Shadow(remap = false)
	private void setArcTowardsID(int id) {
		throw new AssertionError();
	}

	@Inject(method = "tick", at = @At("TAIL"), remap = false, require = 0)
	private void mg$clearFriendlyTarget(CallbackInfo ci) {
		SeekingArrowEntity self = (SeekingArrowEntity) (Object) this;
		if (self.level().isClientSide) return;
		int id = getArcTowardsID();
		if (id == -1) return;
		Entity owner = self.getOwner();
		if (!(owner instanceof AbstractGolemEntity<?, ?> golem)) return;
		Entity locked = self.level().getEntity(id);
		if (locked == null) {
			setArcTowardsID(-1);
			return;
		}
		// 目标已经死了 / 被移除 / 傀儡不该打它（友方、被动、黑名单、配置过滤）
		if (!locked.isAlive() || !(locked instanceof LivingEntity living) || !golem.canAttack(living)) {
			setArcTowardsID(-1);
		}
	}

}
