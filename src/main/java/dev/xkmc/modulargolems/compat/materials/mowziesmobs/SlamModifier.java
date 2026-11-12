package dev.xkmc.modulargolems.compat.materials.mowziesmobs;

import com.bobmowzie.mowziesmobs.server.entity.EntityHandler;
import com.bobmowzie.mowziesmobs.server.entity.effects.EntityAxeAttack;
import com.bobmowzie.mowziesmobs.server.item.ItemHandler;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import java.util.function.Consumer;

public class SlamModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public static final int COOLDOWN = 200;

	public SlamModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> cons) {
		cons.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (EarthquakeHelper.isLeaping(entity)) event.setAmount(event.getAmount() * EarthquakeHelper.LEAP_DAMAGE_MULT);
	}

	@Override
	public void performJump(AbstractGolemEntity<?, ?> golem, int lv) {
		LivingEntity target = golem.getTarget();
		if (target == null) {
			golem.addDeltaMovement(new Vec3(0, 1.3, 0));
			return;
		}
		Vec3 flat = new Vec3(target.getX() - golem.getX(), 0, target.getZ() - golem.getZ());
		if (flat.lengthSqr() < 1e-6) {
			Vec3 look = golem.getLookAngle();
			flat = new Vec3(look.x, 0, look.z);
			if (flat.lengthSqr() < 1e-6) flat = new Vec3(0, 0, 1);
		}
		double dist = flat.length();
		flat = flat.normalize();
		double travel = Math.max(dist - 3.0, 0.5);
		golem.setDeltaMovement(flat.x * travel / 32.0, 1.3, flat.z * travel / 32.0);
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
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		return COOLDOWN;
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return target.onGround() ? 144 : -100;
	}


	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", EarthquakeHelper.leapReduction()).withStyle(ChatFormatting.GREEN));
	}

}
