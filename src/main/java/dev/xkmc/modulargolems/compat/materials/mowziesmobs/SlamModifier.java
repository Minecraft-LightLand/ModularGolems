package dev.xkmc.modulargolems.compat.materials.mowziesmobs;

import com.bobmowzie.mowziesmobs.server.entity.EntityHandler;
import com.bobmowzie.mowziesmobs.server.entity.effects.EntityAxeAttack;
import com.bobmowzie.mowziesmobs.server.item.ItemHandler;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public class SlamModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public SlamModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> cons) {
		cons.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int level) {
		EntityAxeAttack axeAttack = new EntityAxeAttack(EntityHandler.AXE_ATTACK.get(), golem.level(), golem, true);
		axeAttack.absMoveTo(golem.getX(), golem.getY(), golem.getZ(), golem.getYRot(), golem.getXRot());
		axeAttack.setAxeStack(ItemHandler.WROUGHT_AXE.get().getDefaultInstance());
		axeAttack.tickCount = 13;
		golem.level().addFreshEntity(axeAttack);
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return target.onGround() ? 144 : -100;
	}

}
