package dev.xkmc.modulargolems.compat.materials.cataclysm;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.Internal_Animation_Monster;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 调试辅助：记录灾变 Boss 每次切换 {@code attackState} 的日志，用来识别它当时施放的是哪一招。
 * <p>
 * 只在服务端、状态变化时打印一行，格式：
 * {@code [CataBossDebug] <实体> attackState 旧 -> 新 (attackTicks N)}。
 * 把日志里某一行对应的 attackState 数值告诉我，我就能把 Boss 的那一招做成傀儡技能。
 * <p>
 * 用法：进游戏对着 Boss 看日志，看到想要的招式时记下那一行的 state 号。
 * 想连 tick 也一起看（确认动作帧）就把 {@link #TRACE_TICKS} 设成 true，
 * 它会每 {@link #TRACE_INTERVAL} tick 打一行，日志会比较多。
 */
public class CataBossDebug {

	/** 关掉就设成 false。 */
	public static boolean ENABLED = false;

	/** 是否逐 tick 跟踪 attackTicks（排查动作帧时打开）。 */
	public static boolean TRACE_TICKS = false;

	/** 逐 tick 跟踪的打印间隔。 */
	public static int TRACE_INTERVAL = 5;

	private static final Map<Integer, Integer> LAST_STATE = new HashMap<>();
	private static final Map<Integer, Integer> LAST_TICK = new HashMap<>();

	public static void onLivingTick(EntityTickEvent.Pre event) {
		if (!ENABLED) return;
		if (!(event.getEntity() instanceof LivingEntity e)) return;
		if (e.level().isClientSide) return;
		if (!(e instanceof Internal_Animation_Monster boss)) return;
		int state = boss.getAttackState();
		Integer prev = LAST_STATE.put(e.getId(), state);
		if (prev == null || prev != state) {
			LAST_TICK.put(e.getId(), 0);
			ModularGolems.LOGGER.info("[CataBossDebug] {} attackState {} -> {} (attackTicks {})",
					e.getType(), prev == null ? "?" : prev, state, boss.attackTicks);
			return;
		}
		if (!TRACE_TICKS || state <= 0) return;
		int tick = boss.attackTicks;
		Integer last = LAST_TICK.put(e.getId(), tick);
		if (last == null || tick - last >= TRACE_INTERVAL) {
			ModularGolems.LOGGER.info("[CataBossDebug] {} attackState {} attackTicks {}",
					e.getType(), state, tick);
		}
	}

}
