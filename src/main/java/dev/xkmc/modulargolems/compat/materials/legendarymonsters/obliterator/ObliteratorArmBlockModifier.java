package dev.xkmc.modulargolems.compat.materials.legendarymonsters.obliterator;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.LMProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.content.modifier.special.ICooldownSkill;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.List;

/**
 * 湮灭格挡（对应 Boss state52 的 ArmBlockGoal + canArmBlock）：
 * 未在施法时受击有概率格挡当次伤害，随后瞬移到目标身后。
 * 与 Boss 一致：三阶段（生命 &lt;30%）才解锁，冷却 10 秒。
 */
public class ObliteratorArmBlockModifier extends GolemModifier implements ICooldownSkill {

	public static final int COOLDOWN = 200;
	/** 三阶段阈值（Boss 的 canArmBlock 要求 {@code getPhase() >= 3}）。 */
	public static final float PHASE_THREE = ObliteratorPhaseHelper.PHASE_THREE;
	/** 触发格挡的概率。 */
	public static final float BLOCK_CHANCE = 0.35F;
	private static final String KEY = "ObliteratorArmBlock";

	public ObliteratorArmBlockModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (entity.level().isClientSide()) return;
		// 施法中/跳跃中不格挡（对应「未在施法时受击」，也避免空中瞬移）
		if (EarthquakeHelper.isBusy(entity)) return;
		if (!ObliteratorPhaseHelper.isPhaseThree(entity)) return;
		if (event.getSource().getEntity() == null) return;
		long time = entity.level().getGameTime();
		long last = entity.getPersistentData().getLong(KEY);
		if (last > time - COOLDOWN && last <= time) return;
		if (entity.getRandom().nextFloat() > BLOCK_CHANCE) return;
		event.setCanceled(true);
		entity.getPersistentData().putLong(KEY, time);
		entity.playSound(net.miauczel.legendary_monsters.sound.ModSounds.BLOCK.get(), 1.0F, 0.9F);   // boss 格挡 BLOCK 1.0（pitch 0.9）
		LMProxy.obliteratorTeleportBehind(entity, -4.0);
	}

	@Override
	public int getRemainingCooldown(AbstractGolemEntity<?, ?> golem) {
		long time = golem.level().getGameTime();
		long last = golem.getPersistentData().getLong(KEY);
		return (int) Math.max(0, last + COOLDOWN - time);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(
				Component.translatable(getDescriptionId() + ".desc", ObliteratorPhaseHelper.pctText(BLOCK_CHANCE)).withStyle(ChatFormatting.GREEN),
				ObliteratorPhaseHelper.phaseThreeGate().withStyle(ChatFormatting.GREEN));
	}

}
