package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import dev.xkmc.modulargolems.util.GolemTargetUtil;

public class AncientRemnantLeapModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public AncientRemnantLeapModifier() {
		super(StatFilterType.MOVEMENT, 1);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {
		addFlag.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (EarthquakeHelper.isLeaping(entity)) event.setAmount(event.getAmount() * EarthquakeHelper.LEAP_DAMAGE_MULT);
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return 25 * 25;
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int lv) {
		var level = golem.level();
		GolemCataProxy.remnantTailSlamSound(golem);
		GolemCataProxy.screenShake(golem, 30.0F);
		float atk = (float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE);
		var aabb = golem.getBoundingBox().inflate(5, 3, 5);
		List<LivingEntity> list = new ArrayList<>();
		for (var e : level.getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			float dmg = atk + Math.min(atk, e.getMaxHealth() * 0.05f);
			GolemCombatHelper.meleeHit(golem, e, golem.damageSources().mobAttack(golem), dmg);
			EarthquakeHelper.launch(golem, e, 0.6f);
			list.add(e);
		}
		int count = 22 + golem.getRandom().nextInt(8);
		float yaw = golem.getYRot(), pitch = golem.getXRot();
		for (int i = 0; i < count; i++) {
			float angle = yaw + 90 + i * 360F / count;
			golem.setYRot(angle);
			golem.setXRot(0);
			GolemCataProxy.spawnEarthQuake(golem);
		}
		golem.setYRot(yaw);
		golem.setXRot(pitch);
		golem.yBodyRot = yaw;
		golem.yHeadRot = yaw;
	}

	@Override
	public int getCoolDown(AbstractGolemEntity<?, ?> golem, int lv) {
		return 160;
	}


	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", EarthquakeHelper.leapReduction()).withStyle(ChatFormatting.GREEN));
	}

}
