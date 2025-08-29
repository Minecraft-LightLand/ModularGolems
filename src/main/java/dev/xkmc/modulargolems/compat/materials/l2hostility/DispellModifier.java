package dev.xkmc.modulargolems.compat.materials.l2hostility;

import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.l2hostility.content.item.traits.EnchantmentDisabler;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import dev.xkmc.l2hostility.init.registrate.LHTraits;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;

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
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, DamageData.Offence event, int level) {
		var target = event.getTarget();
		List<ItemStack> list = new ArrayList<>();

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack stack = target.getItemBySlot(slot);
			if (stack.isEnchanted() && LHItems.DC_DISPELL_ENCH.get(stack) == null) {
				list.add(stack);
			}
		}

		if (!list.isEmpty()) {
			int time = LHConfig.SERVER.dispellTime.get() * level;
			int count = Math.min(level, list.size());

			for (int i = 0; i < count; ++i) {
				int index = entity.getRandom().nextInt(list.size());
				EnchantmentDisabler.disableEnchantment(entity.level(), list.remove(index), time);
			}

		}
	}

	@Override
	public boolean onAttacked(AbstractGolemEntity<?, ?> entity, DamageData.Attack event, int level) {
		if (level > 0 &&
				!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) &&
				!event.getSource().is(DamageTypeTags.BYPASSES_EFFECTS) &&
				event.getSource().is(Tags.DamageTypes.IS_MAGIC)) {
			return true;
		}
		return false;
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		List<MutableComponent> ans = new ArrayList<>();
		ans.add(Component.translatable(LHTraits.DISPELL.get().getDescriptionId() + ".desc",
				Component.literal("" + v).withStyle(ChatFormatting.AQUA),
				Component.literal("" + LHConfig.SERVER.dispellTime.get() * v / 20).withStyle(ChatFormatting.AQUA)
		).withStyle(ChatFormatting.GREEN));
		return ans;
	}

}
