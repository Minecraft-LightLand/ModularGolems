package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.CataArmorHelper;
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
 * 裂地重踏：重踏地面，向目标方向释放一道扇形冲击波。复刻灾变远古遗魂的擦地重踏。
 * <p>
 * 与远古金属其它技能一致：生命值低于一半（或被胸甲钉在最终形态）时冷却减半，
 * 并且**连续重踏 {@link #REPEAT} 次**。
 */
public class AncientRemnantStompModifier extends GolemModifier {

	/** 本体 STOMP_COOLDOWN。 */
	public static final int COOLDOWN = 200;
	/** 施放距离上限（格）。 */
	public static final int MAX_RANGE = 20;
	/** 施放的最小距离（格）；0 表示不限制。 */
	public static final int MIN_RANGE = 0;

	public static final int[] DISTANCES = {2, 3, 5, 7, 9, 10, 12, 14, 16, 18};
	/** 半血（或穿胸甲）时的连续重踏次数。 */
	public static final int REPEAT = 2;

	public AncientRemnantStompModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(3, new AncientRemnantStompAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc",
				CataArmorHelper.healthPct(CataArmorHelper.HALF_HEALTH),
				CataArmorHelper.number(REPEAT)).withStyle(ChatFormatting.GREEN));
	}

}
