package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.BiConsumer;
import java.util.List;

/**
 * 浮云落阵（对应 Cloud_Golem state 9）：在傀儡头顶排出一圈云，随后整圈一起砸向地面。
 * 落点与结算见 {@link CloudDropAttackGoal}。
 */
public class CloudDropModifier extends GolemModifier {

	public static final int COOLDOWN = 200;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 16;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	/** 环上云的圈数，对应 Boss 的 6 → 9 → 12 → 15 → 18 → 21 → 24 七圈。 */
	public static final int RING_COUNT = 7;
	/** 相邻两圈之间的间隔（tick）。 */
	public static final int RING_GAP = 3;
	/** 第一圈的半径（格）。 */
	public static final double RING_START = 2.5;
	/** 每向外一圈增加的半径（格）。 */
	public static final double RING_STEP = 1.0;
	/** 云下落的高度（格）。 */
	public static final double CLOUD_HEIGHT = 8.0;
	/** 每团云的爆炸半径（格）。 */
	public static final float CLOUD_RADIUS = 2.0F;
	/** 每团云的伤害系数（乘傀儡攻击力）。 */
	public static final float CLOUD_DAMAGE = 0.35F;

	public CloudDropModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new CloudDropAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(
				Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.GREEN),
				LMArmorHelper.phaseTwoAboveGate().withStyle(ChatFormatting.GREEN));
	}
}
