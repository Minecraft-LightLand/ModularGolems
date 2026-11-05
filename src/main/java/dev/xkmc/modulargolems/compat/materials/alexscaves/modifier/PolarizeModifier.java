package dev.xkmc.modulargolems.compat.materials.alexscaves.modifier;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import dev.xkmc.modulargolems.compat.materials.alexscaves.ACCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.alexscaves.client.GolemPolarizeSound;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dev.xkmc.modulargolems.util.GolemTargetUtil;

public class PolarizeModifier extends GolemModifier {

	public static double range(int lv) {
		return (4 + lv) * 0.2 * MGConfig.COMMON.polarizeRange.get();
	}

	public static double damage(int lv) {
		return (1 + lv) / 2f * MGConfig.COMMON.polarizeDamage.get();
	}

	public static double force() {
		return MGConfig.COMMON.polarizeForce.get();
	}

	public PolarizeModifier() {
		super(StatFilterType.MASS, 4);
	}

	/** 正在播放的极化循环音，按实体 id 索引。 */
	private static final Map<Integer, GolemPolarizeSound> SOUNDS = new HashMap<>();

	/**
	 * 维护极化循环音：进入战斗时按红/蓝极性开一条，脱战或极性切换时换掉。
	 * <p>
	 * 用 MG 自己的 {@link GolemPolarizeSound} 而不是 AC 的 {@code ItemTickableSound}——后者要求
	 * 手持盾牌才能播，而磁力傀儡手里没有盾牌。
	 */
	private static void updateSound(AbstractGolemEntity<?, ?> golem, boolean ranged) {
		GolemPolarizeSound sound = SOUNDS.get(golem.getId());
		if (!golem.isAlive() || golem.isRemoved() || !golem.isAggressive()) {
			if (sound != null) {
				sound.stopSound();
				SOUNDS.remove(golem.getId());
			}
			return;
		}
		if (sound != null && sound.isAzure() == ranged) return;
		if (sound != null) sound.stopSound();
		GolemPolarizeSound created = new GolemPolarizeSound(golem, ranged,
				g -> g.getModifiers().containsKey(ACCompatRegistry.POLARIZE.get()));
		SOUNDS.put(golem.getId(), created);
		net.minecraft.client.Minecraft.getInstance().getSoundManager().play(created);
	}
	@Override
	public void onClientTick(AbstractGolemEntity<?, ?> golem, int level) {
		boolean ranged = golem.isInRangedMode() || golem.hasRangeAttack();
		updateSound(golem, ranged);
		if (golem.tickCount % 5 != 0) return;
		if (!golem.isAggressive()) return;
		double r = range(level);
		Vec3 src = golem.position().add(0.0, 0.2, 0.0);
		float particleMax = (float) (5 + golem.getRandom().nextInt(5));
		for (int particles = 0; (float) particles < particleMax; ++particles) {
			Vec3 dst = new Vec3(
					(golem.getRandom().nextFloat() - 0.5) * 0.3,
					(golem.getRandom().nextFloat() - 0.5) * 0.3,
					(r * 0.5F + r * 0.5F * golem.getRandom().nextFloat())
			).yRot((float) ((particles / particleMax) * Math.PI * 2.0)).add(src);
			if (!ranged) {
				golem.level().addParticle(ACParticleRegistry.SCARLET_SHIELD_LIGHTNING.get(),
						dst.x, dst.y, dst.z, src.x, src.y, src.z);
			} else {
				golem.level().addParticle(ACParticleRegistry.AZURE_SHIELD_LIGHTNING.get(),
						src.x, src.y, src.z, dst.x, dst.y, dst.z);
			}
		}
	}

	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		var target = golem.getTarget();
		if (target == null) return;
		boolean dmg = golem.tickCount % 10 == 0;
		float damage = (float) damage(level);
		double r = range(level);
		boolean ranged = golem.isInRangedMode() || golem.hasRangeAttack();
		var aabb = golem.getBoundingBox().inflate(r);
		List<LivingEntity> list = golem.level().getEntities(EntityTypeTest.forClass(LivingEntity.class), aabb,
				e -> e != golem && GolemTargetUtil.canTarget(golem, e));
		Vec3 pos = golem.position().add(0, golem.getBbHeight() / 2, 0);
		Vec3 forward = target.position().add(0, target.getBbHeight() / 2, 0).subtract(pos);
		forward = forward.length() < 1 ? golem.getForward().normalize() : forward.normalize();
		double val = golem.getAttributeValue(ForgeMod.ENTITY_REACH.get()) * 0.8;
		if (val < 1) val = 1;
		forward = forward.scale(val);
		for (var e : list) {
			Vec3 tar = e.position().add(0, e.getBbHeight() / 2, 0);
			if (tar.distanceTo(pos) > r) continue;
			Vec3 dir;
			if (ranged) {
				dir = tar.subtract(pos);
				if (dir.length() > 1) dir = dir.normalize();
			} else {
				dir = pos.add(forward).subtract(tar);
			}
			if (dir.length() > 1) dir = dir.normalize();
			dir = dir.scale(force());
			e.push(dir.x, dir.y, dir.z);
			if (dmg) e.hurt(golem.damageSources().mobAttack(golem), damage);
		}
	}

	/**
	 * 不依赖 modifier 挂载，直接执行一次极化效果。
	 * <p>
	 * 供阻尼盾等"临时获得极化"的场景使用：与 {@link #onAiStep} 同一套公式，
	 * 区别只在于 level 由调用方给定。
	 */
	public static void runOnce(AbstractGolemEntity<?, ?> golem, int level) {
		new PolarizeModifier().onAiStep(golem, level);
	}

	/**
	 * 与 {@link #runOnce} 对应，执行一次客户端粒子表现。
	 * <p>
	 * 阻尼盾这类"临时获得极化"的场景没有挂载 modifier，本类的 {@code onClientTick}
	 * 不会被 {@code AbstractGolemEntity} 的客户端循环调用，需要单独触发。
	 */
	public static void runOnceClient(AbstractGolemEntity<?, ?> golem, int level) {
		new PolarizeModifier().onClientTick(golem, level);
	}
	@Override
	public List<MutableComponent> getDetail(int v) {
		int num = (int) Math.round(damage(v));
		var val = Component.literal("" + num).withStyle(ChatFormatting.AQUA);
		return List.of(Component.translatable(getDescriptionId() + ".desc", val).withStyle(ChatFormatting.GREEN));
	}


}
