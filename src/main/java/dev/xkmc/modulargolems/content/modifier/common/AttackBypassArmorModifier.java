package dev.xkmc.modulargolems.content.modifier.common;

import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

// 一个护甲穿透的升级
public class AttackBypassArmorModifier extends GolemModifier {

	// 构造函数，传入最大等级
	public AttackBypassArmorModifier(int max) {
		super(StatFilterType.ATTACK, max);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		int perc = Math.round(MGConfig.COMMON.armorBypassChance.get().floatValue() * v * 100);
		return List.of(Component.translatable(getDescriptionId() + ".desc", perc).withStyle(ChatFormatting.GREEN));
	}

	@Override
	// 该方法在攻击事件创建时被调用，用于修改攻击源
	public void modifySource(AbstractGolemEntity<?, ?> golem, CreateSourceEvent event, int value) {
		// 首先检查攻击事件的结果是否存在，如果不存在则直接返回
		if (event.getResult() == null) return;
		// 检查攻击结果是否包含 BYPASS_ARMOR 这个状态，如果不存在则返回
		if (!event.getResult().validState(DefaultDamageState.BYPASS_ARMOR)) return;
		// 通过随机数判断是否启用 BYPASS_ARMOR 状态，如果计算出的概率大于随机生成的双精度数，则启用这个状态
		if (MGConfig.COMMON.armorBypassChance.get() * value > golem.getRandom().nextDouble())
			event.enable(DefaultDamageState.BYPASS_ARMOR);
	}

}
