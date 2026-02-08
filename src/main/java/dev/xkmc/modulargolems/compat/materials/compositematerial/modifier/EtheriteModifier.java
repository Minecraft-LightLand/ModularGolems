package dev.xkmc.modulargolems.compat.materials.compositematerial.modifier;

import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EtheriteModifier extends GolemModifier {

	public EtheriteModifier() {
		super(StatFilterType.MASS, 4);
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		var source = event.getSource();
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
		if (source.is(DamageTypeTags.BYPASSES_EFFECTS)) return;
		// 等级1以上,免疫弹射物
		if (level >= 1 && source.is(DamageTypeTags.IS_PROJECTILE)) {
			event.setCanceled(true);
			return;
		}
		// 等级3以上,免疫环境伤害
		if (level >= 3 && source.getEntity() == null) {
			event.setCanceled(true);
			return;
		}
		super.onAttacked(entity, event, level);
	}

	@Override
	public boolean isImmuneTo(AbstractGolemEntity<?, ?> golem, MobEffectInstance ins, int level) {
		// 等级2以上,免疫非增益且可被牛奶清除的效果
		return level >= 2 && !ins.getEffect().isBeneficial() && ins.isCurativeItem(Items.MILK_BUCKET.getDefaultInstance());
	}

	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
		// 限制触发频率
		// 每600tick(30秒)触发一次，且在tickCount为20时触发
		if (golem.tickCount % MGConfig.COMMON.ethertiteRepairDelay.get() == 20) {//TODO config
			golem.repairWithItem();
		}
	}

	public List<MutableComponent> getDetail(int v) {
		return IntStream.range(0, 5)
				.mapToObj(i -> Component.translatable(getDescriptionId() + ".desc" + (i + 1)).withStyle
                        (i==0 ? ChatFormatting.DARK_GREEN : i <= v ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_GRAY))
				.collect(Collectors.toList());
	}

}
