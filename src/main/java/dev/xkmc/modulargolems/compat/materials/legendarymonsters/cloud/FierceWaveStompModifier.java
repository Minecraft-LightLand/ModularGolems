package dev.xkmc.modulargolems.compat.materials.legendarymonsters.cloud;

import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorEffects;
import dev.xkmc.modulargolems.compat.materials.legendarymonsters.armor.LMArmorHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import net.miauczel.legendary_monsters.Particle.custom.Circle;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.ElectricityEntity;
import net.miauczel.legendary_monsters.util.ParticleUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;
import java.util.List;

/**
 * 烈波重踏（逐帧复刻 Cloud_Golem state25）：抬起左腿连续踏地两次，每次踏地召唤一组扇形烈波。
 * <ul>
 *   <li>tick 25：第一次踏地，7 道烈波、间隔 30°，生成在 y 取整处</li>
 *   <li>tick 41：第二次踏地，9 道烈波、间隔 20°，生成在 y + 身高×0.15 处</li>
 * </ul>
 * 烈波本体是 {@code ElectricityEntity}（伤害 7、寿命 45），走 INDIRECT_MAGIC，天然无视护甲与盾牌。
 * 砸地表现（{@code attackParticle()}: ParticleBlock 碎屑圈 + 水平 GROW 环 + 尘土）只能在客户端画，
 * 由服务端每次踏地广播 {@link #EVENT_STOMP} 触发。
 * <p>
 * 注意：wiki 写的是「每次五道」，但本仓库编译/lock 的 8657289 里 state25 实际是 7 道（30°）与 9 道（20°）。
 * 这里按 jar 的实测值写，{@link #WAVES_1}/{@link #WAVES_2} 是常量，要改成 5/5 只动这两个。
 */
public class FierceWaveStompModifier extends GolemModifier {

	public static final int COOLDOWN = 200;
	/** 施放距离上限（格），对应 Boss state25 的 goal range 12。 */
	public static final int MAX_RANGE = 12;
	/** 施放的最小距离（格），对应 Boss 的 attackrangemin 5。 */
	public static final int MIN_RANGE = 0;

	/** 第一次踏地：tick / 道数 / 间隔角。 */
	public static final int STOMP_1_TICK = 25;
	public static final int WAVES_1 = 7;
	public static final float STEP_1 = 30.0F;

	/** 第二次踏地：tick / 道数 / 间隔角，生成高度用身高比例 0.15。 */
	public static final int STOMP_2_TICK = 41;
	public static final int WAVES_2 = 9;
	public static final float STEP_2 = 20.0F;
	public static final double Y_OFFSET_2 = 0.15;

	/** 烈波伤害与寿命，对应 {@code new ElectricityEntity(..., 7.0F, angle, 45.0F)}。 */
	public static final float WAVE_DAMAGE = 7.0F;
	public static final float WAVE_LIFE = 45.0F;

	/** 整招持续 tick，对应 attackMaxtick 56。 */
	public static final int TOTAL_TICKS = 56;

	/** 每次踏地的客户端表现事件。 */
	public static final byte EVENT_STOMP = 90;

	public FierceWaveStompModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
		addGoal.accept(5, new FierceWaveStompAttackGoal(entity, lv).withName(this.getDescriptionId()));
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(
				Component.translatable(getDescriptionId() + ".desc", Component.literal("2").withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GREEN),
				LMArmorHelper.phaseTwoGate().withStyle(ChatFormatting.GREEN));
	}

	/** 浮云胸甲：烈波每道各追一个目标（分配方式同灾变远古金属护腿的冲击波）。 */
	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		if (golem.level().isClientSide()) return;
		if (!LMArmorHelper.cloudChest(golem)) return;
		LMArmorEffects.steerWaves(golem, ElectricityEntity.class, e -> e.getOwner() == golem);
	}

	@Override
	public void handleEvent(AbstractGolemEntity<?, ?> golem, int value, byte event) {
		if (event != EVENT_STOMP) return;
		if (!golem.level().isClientSide) return;
		// 傀儡从身体正中踩下：Boss 那套 radius/extraX/extraZ 是给单脚踩地的大体型用的侧向偏移，这里直接用自身水平坐标
		// 踩地特效锚到地面（findFloor），不要用 getY()：客户端位置是插值的，可能还在半空
		Vec3 at = EarthquakeHelper.findFloor(golem.level(), golem.position(), 8);
		if (at == null) at = golem.position();
		double x = at.x;
		double y = at.y;
		double z = at.z;
		BlockState block = golem.level().getBlockState(BlockPos.containing(x, y - 0.5, z));
		BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, block);
		for (int i = 0; i < 360; i++) {
			golem.level().addParticle(option, x + Math.cos(i), y, z + Math.sin(i),
					Math.cos(i) * 0.25, 0.0, Math.sin(i) * 0.25);
		}
		golem.level().addParticle(new Circle.RingData(0.0F, (float) (Math.PI / 2), 30,
				1.0F, 1.0F, 1.0F, 1.0F, 20.0F, false, Circle.EnumRingBehavior.GROW),
				x, y + 0.1, z, 0.0, 0.0, 0.0);
		// state25 每踏一次都是 controlledSmashParticles(2, 0, 0, 0.5, 1)
		ParticleUtils.controlledSmashParticles(golem, 2.0F, 0.0F, 0.0F, 0.5F, 1.0F);
	}

}
