package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.modulargolems.content.entity.hostile.HostileGolemRegistry;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

/**
 * 傀儡旗帜的选取逻辑：普通旗帜槽、敌对阵营旗帜、小队队长旗帜。
 * <p>
 * 抽出来给两处共用：普通模型用人形/大型/犬形的 {@code GolemBannerLayer} 渲染；
 * 女仆模型由 TLM 自带的背包/旗帜层通过 {@code IMaid#getBackpackShowItem()} 取旗帜。
 */
public final class GolemBannerUtil {

	private GolemBannerUtil() {
	}

	public static boolean renders(ItemStack stack) {
		return stack.getItem() instanceof BannerItem;
	}

	public static ItemStack getBanner(AbstractGolemEntity<?, ?> entity) {
		// the banner has its own slot, the head slot no longer takes one
		ItemStack stack = entity.getBannerSlot().getItem();
		var selfFact = HostileGolemRegistry.tryGetFaction(entity);
		if (selfFact.isPresent()) {
			if (entity.hasPassenger(e -> e instanceof AbstractGolemEntity<?, ?>))
				return ItemStack.EMPTY;
			return selfFact.get().getBanner(entity, entity.getConfigColor());
		}
		if (renders(stack)) return stack;
		var entry = entity.getConfigEntry(MGLangData.LOADING.get());
		if (entry != null) {
			entry.clientTick(entity.level(), false);
			UUID captainId = entry.squadConfig.getCaptainId();
			boolean showFlag = captainId != null && entity.getUUID().equals(captainId);
			if (showFlag) {
				String color = DyeColor.values()[entry.getColor()].getName();
				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(color + "_banner"));
				if (item != null) {
					return item.getDefaultInstance();
				}
			}
		}
		return ItemStack.EMPTY;
	}

}
