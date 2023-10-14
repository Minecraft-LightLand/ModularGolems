package dev.xkmc.modulargolems.content.item.card;

import dev.xkmc.l2library.util.nbt.ItemCompoundTag;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UuidFilterCard extends ClickEntityFilterCard<UUID> {

	private static final String KEY = "idList";

	public UuidFilterCard(Properties properties) {
		super(properties);
	}

	@Override
	protected UUID getValue(LivingEntity entity) {
		return entity.getUUID();
	}

	public List<UUID> getList(ItemStack stack) {
		var tag = stack.getTag();
		List<UUID> ans = new ArrayList<>();
		if (tag == null || !tag.contains(KEY)) return ans;
		for (var e : tag.getList(KEY, Tag.TAG_INT_ARRAY)) {
			ans.add(NbtUtils.loadUUID(e));
		}
		return ans;
	}

	public void setList(ItemStack stack, List<UUID> list) {
		var tag = ItemCompoundTag.of(stack).getSubList(KEY, Tag.TAG_INT_ARRAY)
				.getOrCreate();
		tag.clear();
		for (var uuid : list) {
			tag.add(NbtUtils.createUUID(uuid));
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		var ids = getList(stack);
		if (ids.size() > 0 && !Screen.hasShiftDown()) {
			for (var e : ids) {
				list.add(Component.literal(e.toString().substring(0, 8)));
			}
			list.add(MGLangData.TARGET_SHIFT.get());
		} else {
			list.add(MGLangData.TARGET_UUID_ADD.get());
			list.add(MGLangData.TARGET_UUID_REMOVE.get());
			list.add(MGLangData.TARGET_REMOVE.get());
		}
	}


}
