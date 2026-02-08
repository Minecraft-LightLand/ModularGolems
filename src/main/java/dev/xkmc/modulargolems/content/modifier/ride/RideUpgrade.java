package dev.xkmc.modulargolems.content.modifier.ride;

import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.AttributeGolemModifier;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.List;
import java.util.function.Consumer;

public class RideUpgrade extends AttributeGolemModifier {

	public RideUpgrade(int max, AttrEntry... entries) {
		super(max, entries);
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		if (event.getSource().getEntity() instanceof Mob mob && mob.getTarget() != entity) {
			event.setCanceled(true);
		}
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		if (event.getSource().getEntity() instanceof Mob mob && mob.getTarget() != entity) {
			event.setAmount(0);
		}
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		// 该Golem应该是被动的，不会主动攻击其他实体
		addFlag.accept(GolemFlags.PASSIVE);
	}

	@Override
	public boolean fitsOn(GolemType<?, ?> type) {
		// 只有狗型Golem才能装备这个坐骑升级
		return type == GolemTypes.TYPE_DOG.get();
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		var ans = super.getDetail(v);
		ans.add(0, Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.GREEN));
		return ans;
	}
}
