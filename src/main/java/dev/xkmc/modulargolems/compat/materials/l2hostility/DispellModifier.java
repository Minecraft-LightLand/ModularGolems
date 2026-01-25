package dev.xkmc.modulargolems.compat.materials.l2hostility;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.l2hostility.content.item.traits.EnchantmentDisabler;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.registrate.LHTraits;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.ArrayList;
import java.util.List;

public class DispellModifier extends GolemModifier {

	public DispellModifier() {
		super(StatFilterType.ATTACK, 4);
	}

	@Override
	public void modifySource(AbstractGolemEntity<?, ?> golem, CreateSourceEvent event, int value) {
		if (event.getResult() == null) return;
		if (!event.getResult().validState(DefaultDamageState.BYPASS_MAGIC)) return;
		event.enable(DefaultDamageState.BYPASS_MAGIC);
	}

	@Override
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		var target = event.getEntity();
		List<ItemStack> list = new ArrayList<>();

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack stack = target.getItemBySlot(slot);
			if (stack.isEnchanted() && !stack.getOrCreateTag().contains("l2hostility_enchantment")) {
				list.add(stack);
			}
		}

		if (!list.isEmpty()) {
			int time = LHConfig.COMMON.dispellTime.get() * level;
			int count = Math.min(level, list.size());

			for (int i = 0; i < count; ++i) {
				int index = entity.getRandom().nextInt(list.size());
				EnchantmentDisabler.disableEnchantment(entity.level(), list.remove(index), time);
			}

		}
	}

	@Override
	public void onDamaged(AttackCache cache, AbstractGolemEntity<?, ?> entity, int level) {
		LHTraits.DISPELL.get().onDamaged(level, entity, cache);
	}

	@Override
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {
		LHTraits.DISPELL.get().onAttackedByOthers(level, entity, event);
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		List<MutableComponent> ans = new ArrayList<>();
		ans.add(Component.translatable(LHTraits.DISPELL.get().getDescriptionId() + ".desc",
				Component.literal("" + v).withStyle(ChatFormatting.AQUA),
				Component.literal("" + LHConfig.COMMON.dispellTime.get() * v / 20).withStyle(ChatFormatting.AQUA)
		).withStyle(ChatFormatting.GREEN));
		return ans;
	}

}
