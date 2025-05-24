package dev.xkmc.modulargolems.compat.materials.l2hostility;

import dev.xkmc.l2hostility.content.capability.mob.CapStorageData;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.registrate.LHTraits;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdaptiveModifier extends GolemModifier {

	private static final String KEY = "LHGolemAdaptiveData";

	public AdaptiveModifier() {
		super(StatFilterType.HEALTH, 4);
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {
		if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) || event.getSource().is(DamageTypeTags.BYPASSES_EFFECTS))
			return;

		var root = entity.getPersistentData();
		Data data = new Data();
		if (root.contains(KEY, Tag.TAG_COMPOUND)) {
			data = TagCodec.fromTag(root.getCompound(KEY), Data.class);
			if (data == null) data = new Data();
		}

		String id = event.getSource().getMsgId();
		if (data.memory.contains(id)) {
			data.memory.remove(id);
			data.memory.add(0, id);
			int val = data.adaption.compute(id, (k, oldx) -> oldx == null ? 1 : oldx + 1);
			double factor = Math.pow(LHConfig.COMMON.adaptFactor.get(), val - 1);
			event.setAmount((float) (event.getAmount() * factor));
		} else {
			data.memory.add(0, id);
			data.adaption.put(id, 1);
			if (data.memory.size() > level) {
				String old = data.memory.remove(data.memory.size() - 1);
				data.adaption.remove(old);
			}
		}
		var ans = TagCodec.toTag(new CompoundTag(), data);
		if (ans != null) {
			root.put(KEY, ans);
		}
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		List<MutableComponent> ans = new ArrayList<>();
		ans.add(Component.translatable(LHTraits.ADAPTIVE.get().getDescriptionId() + ".desc",
				Component.literal("" + (int) Math.round(100 * (1 - LHConfig.COMMON.adaptFactor.get()))).withStyle(ChatFormatting.AQUA),
				Component.literal("" + v).withStyle(ChatFormatting.AQUA)
		).withStyle(ChatFormatting.GREEN));
		return ans;
	}


	@SerialClass
	public static class Data extends CapStorageData {
		@SerialClass.SerialField
		public final ArrayList<String> memory = new ArrayList<>();
		@SerialClass.SerialField
		public final HashMap<String, Integer> adaption = new HashMap<>();

		public Data() {
		}
	}

}
