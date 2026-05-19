package dev.xkmc.modulargolems.content.modifier.common;

import dev.xkmc.l2library.base.effects.EffectUtil;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;

public class BellModifier extends GolemModifier {

	// 构造方法
	// 调用了父类 GolemModifier 的构造方法，并传入了两个参数：
	// StatFilterType.HEALTH 表示该修饰符主要涉及傀儡的生命值属性
	// 1 表示该修饰符的默认级别为 1
	public BellModifier() {
		super(StatFilterType.HEALTH, 1);
	}

	@Override
	// 在傀儡设置目标时被调用
	public void onSetTarget(AbstractGolemEntity<?, ?> golem, Mob mob, int level) {
		// 获取傀儡的边界框，并将其膨胀 48 个方块，这样可以检测到傀儡周围一定范围内的生物
		var aabb = golem.getBoundingBox().inflate(48);
		// 获取边界框内的所有生物，并将这些生物过滤为傀儡可以攻击的目标
		// 使用了 lambda 表达式 golem::canAttack 来过滤符合条件的生物
		var list = golem.level().getEntitiesOfClass(Mob.class, aabb, golem::canAttack);
		// 初始化一个布尔变量 sound，用于判断是否需要播放声音
		boolean sound = false;
		// 遍历过滤后的生物列表
		for (var e : list) {
			// 如果生物是敌人且不是爬行者且可以攻击傀儡
			if (e instanceof Enemy && !(e instanceof Creeper) && e.canAttack(golem)) {
				// !e.hasEffect(MobEffects.GLOWING) 为 true），如果是，则将 sound 设置为 true
				sound |= !e.getTags().contains("BellHit");
				e.getTags().add("BellHit");
				EffectUtil.addEffect(e, new MobEffectInstance(MobEffects.GLOWING, 200), EffectUtil.AddReason.NONE, golem);
				if (!(e.getTarget() instanceof AbstractGolemEntity<?, ?>)) {
					// 将其目标设置为傀儡
					e.setTarget(golem);
				}
			}
		}
		// 判断是否播放声音
		if (sound) {
			// SoundEvents.BELL_BLOCK 表示声音事件，后面两个参数分别是音量和音调
			golem.playSound(SoundEvents.BELL_BLOCK, 1, 1);
		}
	}
}
