package dev.xkmc.modulargolems.compat.materials.legendarymonsters;

import dev.xkmc.modulargolems.init.ModularGolems;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.OriginClasses.IAnimatedMonster;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 调试辅助：记录传奇怪物（含 boss）每次切换 attackState 的日志，用来识别它当时施放的是哪一招。
 * 只在服务端、状态变化时打印一行，格式：<code>[LMBossDebug] &lt;实体&gt; attackState 旧 -> 新 (tick N)</code>。
 * 把日志里某一行对应的 attackState 数值告诉我，我就能把 boss 的那一招做成傀儡技能。
 */
public class LMBossDebug {

	/** 关掉就设成 false。 */
	public static boolean ENABLED = true;

	private static final Map<Integer, Integer> LAST = new HashMap<>();

	public static void onLivingTick(LivingEvent.LivingTickEvent event) {
		if (!ENABLED) return;
		LivingEntity e = event.getEntity();
		if (e.level().isClientSide) return;
		if (!(e instanceof IAnimatedMonster boss)) return;
		int state = boss.getAttackState();
		Integer prev = LAST.put(e.getId(), state);
		if (prev == null || prev.intValue() != state) {
			ModularGolems.LOGGER.info("[LMBossDebug] {} attackState {} -> {} (tick {})",
					e.getType(), prev == null ? "?" : prev, state, boss.getAttackTicks());
		}
	}
}
