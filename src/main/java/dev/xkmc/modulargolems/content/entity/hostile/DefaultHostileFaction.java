package dev.xkmc.modulargolems.content.entity.hostile;

import dev.xkmc.modulargolems.content.capability.GolemConfigEntry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.card.PathRecordCard;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DefaultHostileFaction extends HostileFaction {

	public DefaultHostileFaction(Identifier id) {
		super(id);
	}

	@Override
	public @Nullable GolemConfigEntry getConfig(AbstractGolemEntity<?, ?> e, int col) {
		return super.getConfig(e, col);
	}

	@Override
	public @Nullable PathRecordCard.Pos getPath(AbstractGolemEntity<?, ?> e, int col) {
		return super.getPath(e, col);
	}

	@Override
	public ItemStack getBanner(AbstractGolemEntity<?, ?> e, int col) {
		return Raid.getLeaderBannerInstance(e.registryAccess().lookupOrThrow(Registries.BANNER_PATTERN));
	}

}
