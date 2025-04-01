package dev.xkmc.modulargolems.content.client.tracker;

import dev.xkmc.modulargolems.content.capability.GolemTracker;
import dev.xkmc.modulargolems.init.data.MGLangData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class TrackerInfo {

	public static Component getDesc(GolemTracker.TrackedData data) {
		float f = Mth.clamp(data.hp / data.mhp, 0f, 1f);
		int color = Mth.hsvToRgb(f / 3.0F, 1.0F, 0.5f);
		MutableComponent hc = Component.literal("" + Math.round(data.hp)).setStyle(Style.EMPTY.withColor(color));
		var hpText = hc.append(Component.literal("/" + Math.round(data.mhp))
				.withStyle(data.hp <= 0 ? ChatFormatting.DARK_RED : ChatFormatting.DARK_AQUA));
		return data.name == null ? hpText : Component.Serializer.fromJson(data.name).append(": ").append(hpText);
	}

	public static List<Component> getDetail(GolemTracker.TrackedData data, Player player, long time) {
		List<Component> ans = new ArrayList<>();
		if (data.name != null) ans.add(Component.Serializer.fromJson(data.name));
		float f = Mth.clamp(data.hp / data.mhp, 0f, 1f);
		int color = Mth.hsvToRgb(f / 3.0F, 1.0F, 0.5f);
		MutableComponent hc = Component.literal("" + Math.round(data.hp)).setStyle(Style.EMPTY.withColor(color));
		ans.add(MGLangData.HEALTH.get(hc, Math.round(data.mhp)).withStyle(data.hp <= 0 ? ChatFormatting.DARK_RED : ChatFormatting.DARK_AQUA));
		if (data.hp > 0) {
			long diff = (time - data.timestamp) / 20;
			ans.add(diff < 2 ?
					MGLangData.TRACKER_PRESENT.get().withStyle(ChatFormatting.DARK_GREEN) :
					MGLangData.TRACKER_TIME.get().withStyle(ChatFormatting.RED));
		}
		boolean diffDim = !data.lastDim.equals(player.level().dimension().location());
		var p = data.lastPos;
		boolean tooFar = diffDim || p.distSqr(player.blockPosition()) > 128 * 128;
		ans.add(MGLangData.TRACKER_DIM.get(data.lastDim.toString()).withStyle(diffDim ? ChatFormatting.RED : ChatFormatting.GRAY));
		ans.add(MGLangData.TRACKER_POS.get(p.getX(), p.getY(), p.getZ()).withStyle(tooFar ? ChatFormatting.RED : ChatFormatting.GRAY));
		ans.add(getStatusDesc(data));
		if (data.golemType != null) {
			var parts = data.golemType.values();
			if (data.materials.size() == parts.length) {
				for (int i = 0; i < parts.length; i++) {
					var id = data.materials.get(i);
					ans.add(parts[i].getDesc(Component.translatable("golem_material." + id.getNamespace() + "." + id.getPath()).withStyle(ChatFormatting.GOLD)));
				}
			}
		}
		return ans;
	}

	private static MutableComponent getStatusDesc(GolemTracker.TrackedData data) {
		return switch (data.status) {
			case ALIVE -> MGLangData.TRACKER_ALIVE.get().withStyle(ChatFormatting.GRAY);
			case RETRIEVED -> MGLangData.TRACKER_RETRIEVED.get().withStyle(ChatFormatting.GRAY);
			case OTHER_RETRIEVED -> data.cause == null ?
					MGLangData.TRACKER_RETRIEVED.get().withStyle(ChatFormatting.GRAY) :
					MGLangData.TRACKER_RETRIEVED_OTHER.get(Component.Serializer.fromJson(data.cause)).withStyle(ChatFormatting.GRAY);
			case DEATH -> data.cause == null ?
					MGLangData.TRACKER_DIED.get().withStyle(ChatFormatting.RED) :
					MGLangData.TRACKER_KILLED.get(Component.Serializer.fromJson(data.cause)).withStyle(ChatFormatting.RED);
			case DEATH_RECYCLE -> data.cause == null ?
					MGLangData.TRACKER_RECYCLE_DIED.get().withStyle(ChatFormatting.RED) :
					MGLangData.TRACKER_RECYCLE_KILLED.get(Component.Serializer.fromJson(data.cause)).withStyle(ChatFormatting.RED);
		};
	}

}
