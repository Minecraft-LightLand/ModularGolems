package dev.xkmc.modulargolems.compat.materials.alexscaves.modifier;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import dev.xkmc.l2library.base.effects.api.InherentEffect;
import dev.xkmc.l2library.util.math.MathHelper;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class AtomicBoostedEffect extends InherentEffect {

	/**
	 * 核能强化期间循环播放核熔炉运转音的间隔（tick）。ogg 约 10.9s，取 220t（11s）让首尾基本衔接，
	 * 且单次 10s 的 buff 内只播一次，不会重复叠音。
	 */
	private static final int AMBIENT_INTERVAL = 220;
	private static final String AMBIENT_KEY = "AtomicBoostAmbient";

	public AtomicBoostedEffect(MobEffectCategory category, int color) {
		super(category, color);
		String name = "atomic_boost";
		UUID id = MathHelper.getUUIDFromString(name);
		double boost = 0.2;
		addAttributeModifier(Attributes.ATTACK_DAMAGE, id.toString(), boost, AttributeModifier.Operation.MULTIPLY_TOTAL);
		addAttributeModifier(Attributes.MOVEMENT_SPEED, id.toString(), boost, AttributeModifier.Operation.MULTIPLY_BASE);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		// 直接挂在“核能强化”buff 上：buff 存续期间，在服务端定时播放 Alex's Caves 的核熔炉运转音。
		if (entity.level().isClientSide()) return;
		if (!(entity instanceof AbstractGolemEntity<?, ?> golem)) return;
		long now = golem.level().getGameTime();
		long last = golem.getPersistentData().getLong(AMBIENT_KEY);
		if (last != 0 && now - last < AMBIENT_INTERVAL) return;
		golem.getPersistentData().putLong(AMBIENT_KEY, now);
		golem.playSound(ACSoundRegistry.NUCLEAR_FURNACE_ACTIVE.get(), 1.0F, 1.0F);
	}

}
