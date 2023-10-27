package dev.xkmc.modulargolems.content.item.card;

import dev.xkmc.l2library.util.nbt.ItemCompoundTag;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EntityTypeFilterCard extends ClickEntityFilterCard<EntityType<?>> {

	private static final String KEY = "typeList";

	public EntityTypeFilterCard(Properties properties) {
		super(properties);
	}

	@Override
	protected EntityType<?> getValue(LivingEntity entity) {
		return entity.getType();
	}

	@Override
	protected Component getName(EntityType<?> entityType) {
		return entityType.getDescription();
	}

	protected List<EntityType<?>> getList(ItemStack stack) {
		var tag = stack.getTag();
		List<EntityType<?>> ans = new ArrayList<>();
		if (tag == null || !tag.contains(KEY)) return ans;
		for (var e : tag.getList(KEY, Tag.TAG_STRING)) {
			var type = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(e.getAsString()));
			if (type != null) {
				ans.add(type);
			}
		}
		return ans;
	}

	protected void setList(ItemStack stack, List<EntityType<?>> list) {
		var tag = ItemCompoundTag.of(stack).getSubList(KEY, Tag.TAG_STRING)
				.getOrCreate();
		tag.clear();
		for (var type : list) {
			var id = ForgeRegistries.ENTITY_TYPES.getKey(type);
			if (id != null) {
				tag.add(StringTag.valueOf(id.toString()));
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		var types = getList(stack);
		if (types.size() > 0 && !Screen.hasShiftDown()) {
			for (var e : types) {
				list.add(getName(e));
			}
			list.add(MGLangData.TARGET_SHIFT.get());
		} else {
			list.add(MGLangData.TARGET_TYPE_ADD.get());
			list.add(MGLangData.TARGET_TYPE_REMOVE.get());
			list.add(MGLangData.TARGET_REMOVE.get());
		}
	}

}
