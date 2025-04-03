package dev.xkmc.modulargolems.compat.materials.geoty.modifier;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.util.FireTornadoTrap;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.BaseRangedAttackGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;

public class FireTornadoGoal extends BaseRangedAttackGoal {

	private int lv;

	public FireTornadoGoal(AbstractGolemEntity<?, ?> golem, int lv) {
		super(200, 2, 35, golem, lv);
		this.lv = lv;
	}

	@Override
	protected void performAttack(LivingEntity target) {
		var level = golem.level();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(target.getX(), target.getY(), target.getZ());
		while (pos.getY() > level.getMinBuildHeight() && !level.getBlockState(pos).blocksMotion()) {
			pos.move(Direction.DOWN);
		}
		FireTornadoTrap e = new FireTornadoTrap(ModEntityType.FIRE_TORNADO_TRAP.get(), level);
		e.setPos(pos.getX(), pos.getY() + 1, pos.getZ());
		e.setOwner(golem);
		e.setDuration(60);
		level.addFreshEntity(e);
	}
}
