package dev.xkmc.modulargolems.content.capability;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.card.PathRecordCard;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SerialClass
public class PathConfig {

	@SerialField
	protected final ArrayList<ItemStack> path = new ArrayList<>();

	@Nullable
	public static List<PathRecordCard.Pos> getPath(AbstractGolemEntity<?, ?> e) {
		if (ModList.get().isLoaded("curios")) {
			var opt = CurioCompatRegistry.getItem(e, "golem_route")
					.map(PathRecordCard::getList);
			if (opt.isPresent()) return opt.get();
		}
		var config = e.getConfigEntry(null);
		if (config != null) {
			return config.pathConfig.getList();
		}
		return null;
	}

	private List<PathRecordCard.Pos> getList() {
		for (var e : path) {
			if (e.getItem() instanceof PathRecordCard) {
				return PathRecordCard.getList(e);
			}
		}
		return List.of();
	}
}
