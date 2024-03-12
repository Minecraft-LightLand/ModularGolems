package dev.xkmc.modulargolems.content.client.outline;

import com.simibubi.create.CreateClient;
import dev.xkmc.modulargolems.content.item.card.PathRecordCard;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collection;

public class BlockOutliner {

	public static void drawOutlines(Player player, Collection<PathRecordCard.Pos> selection) {
		Level level = Minecraft.getInstance().level;
		if (level == null) return;
		if (Minecraft.getInstance().player != player) return;
		BlockPos pre = null, first = null;
		float time = (level.getGameTime() + Minecraft.getInstance().getPartialTick()) / 40f % 2 - 1;
		for (var point : selection) {
			var id = point.level();
			if (!level.dimension().location().equals(id)) continue;
			BlockPos pos = point.pos();
			VoxelShape shape = Shapes.block();
			CreateClient.OUTLINER.showAABB(point, shape.bounds().move(pos)).colored(pre == null ? 0x7fff7f : 8375776).lineWidth(0.0625F);
			if (pre != null) {
				line(pre, pos, time);
			} else first = pos;
			pre = pos;
		}
		if (pre != null) line(pre, first, time);
	}

	private static void line(BlockPos a, BlockPos b, float time) {
		CreateClient.OUTLINER.endChasingLine(Pair.of(a, b), (time > 0 ? a : b).getCenter(), (time > 0 ? b : a).getCenter(), Math.abs(time), false)
				.colored(8375776).lineWidth(0.0625F);
	}

}
