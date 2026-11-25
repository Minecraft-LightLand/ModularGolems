package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * 湮灭踏地（对应 Boss state28/29 的 IAttackGoal）：
 * 快速向后跳拉开距离，随后侧向砸地产生冲击波，冲击波逐渐向前扩大。
 * 左右方向随机，与 Boss 的 state28（左）/ state29（右）两个变体一致。
 */
public class ObliteratorStompModifier extends GolemModifier {

	public static final int COOLDOWN = 200;
	/** 施放距离上限（格），对应 Boss 的 attackrange 5。 */
	public static final int MAX_RANGE = 16;   // = 冲击波最远生成半径（2+7*2）
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;
	/** 后跳之后、砸地那一帧（对应 Boss t39）。 */
	public static final int STOMP_TICK = 39;
	/** 整套冲击波铺完的帧（Boss t58）。 */
	public static final int END_TICK = 58;
	/** 冲击波逐圈外扩：起始半径 2，每圈 +2，共 8 圈（2…16）。 */
	public static final int WAVE_START = 2;
	public static final int WAVE_STEP = 2;
	public static final int RING_COUNT = 8;
	/** 每 2 tick 外扩一圈。 */
	public static final int RING_INTERVAL = 2;

	public ObliteratorStompModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new ObliteratorStompAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.GREEN));
	}

}
