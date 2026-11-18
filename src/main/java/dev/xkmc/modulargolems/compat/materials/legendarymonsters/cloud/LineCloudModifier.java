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
 * 一字落云（对应 Cloud_Golem state 20）：朝目标方向依次降下十几团云，
 * 每团落地爆炸造成范围伤害。落点与结算见 {@link LineCloudAttackGoal}。
 */
public class LineCloudModifier extends GolemModifier {

	public static final int COOLDOWN = 240;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 24;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	/** 云的团数，对应 Boss state20 沿方向 4→24 格依次落下的 11 团云。 */
	public static final int CLOUD_COUNT = 11;
	/** 第一团云距傀儡的距离（格），对应 Boss 的起始 progress 4。 */
	public static final double CLOUD_START = 1.5;
	/** 相邻两团云的间距（格），对应 Boss 的 progress 步长 2。 */
	public static final double CLOUD_STEP = 1.5;
	/** 云生成在目标头顶上方多少格，对应 Boss 的 {@code target.getBoundingBox().maxY + 2}。 */
	public static final double CLOUD_HEIGHT = 2.0;
	/** 每团云的爆炸半径（格）。 */
	public static final float CLOUD_RADIUS = 2.0F;
	/** 每团云的伤害系数（乘傀儡攻击力）。 */
	public static final float CLOUD_DAMAGE = 0.5F;

	/** 每级减半冷却（与其它远程技能一致）。 */
	public static int cooldownFor(int lv) {
		return Math.max(1, COOLDOWN >> (lv - 1));
	}

	public LineCloudModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new LineCloudAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(
				Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.GREEN),
				LMArmorHelper.phaseTwoAboveGate().withStyle(ChatFormatting.GREEN));
	}
}
