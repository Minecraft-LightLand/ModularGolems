package dev.xkmc.modulargolems.content.entity.hostile;

import dev.xkmc.l2library.util.math.MathHelper;
import dev.xkmc.modulargolems.content.capability.GolemConfigEntry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.item.card.PathRecordCard;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class HostileFaction {

	public final ResourceLocation id;
	public final UUID uuid;

	public HostileFaction(ResourceLocation id) {
		this.id = id;
		uuid = MathHelper.getUUIDFromString(id.toString());
	}

	@Nullable
	public GolemConfigEntry getConfig(AbstractGolemEntity<?, ?> e, int col) {
		return null;
	}

	@Nullable
	public List<PathRecordCard.Pos> getPath(AbstractGolemEntity<?, ?> e, int col) {
		return null;
	}

	public ItemStack getBanner(AbstractGolemEntity<?, ?> e, int col) {
		return ItemStack.EMPTY;
	}

	public boolean hostileGolemAttacks(LivingEntity target) {
		if (target instanceof Player player && !(target instanceof FakePlayer)) {
			return player.canBeSeenAsEnemy();
		}
		if (target instanceof OwnableEntity ownable) {
			return ownable.getOwner() instanceof Player;
		}
		return false;
	}

	public boolean isAlliedTo(Entity other) {
		if (other instanceof AbstractGolemEntity<?, ?> golem) {
			return golem.isHostile() && uuid.equals(golem.getOwnerUUID());
		}
		return false;
	}

}
