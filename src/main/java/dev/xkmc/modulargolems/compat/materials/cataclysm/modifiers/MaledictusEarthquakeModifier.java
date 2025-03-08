package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.cataclysm.CataclysmProxy;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.function.Consumer;

public class MaledictusEarthquakeModifier extends GolemModifier implements EarthquakeHelper.Modifier {

	public MaledictusEarthquakeModifier() {
		super(StatFilterType.HEALTH, 1);
	}

	@Override
	public void onRegisterFlag(Consumer<GolemFlags> cons) {
		cons.accept(GolemFlags.EARTH_QUAKE);
	}

	@Override
	public void handleEvent(AbstractGolemEntity<?, ?> golem, int value, byte event) {
		if (event == EarthquakeHelper.FLAG) {
			EarthquakeHelper.makeParticles(golem, 0, 0);
		}
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int level) {
		golem.playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.5F, 1.0F + golem.getRandom().nextFloat() * 0.1F);
		for (LivingEntity entity : golem.level().getEntitiesOfClass(LivingEntity.class, golem.getBoundingBox().inflate(7.0))) {
			if (!golem.isAlliedTo(entity) && entity != golem) {
				float damage = (float) (golem.getAttributeValue(Attributes.ATTACK_DAMAGE) + entity.getMaxHealth() * CataclysmProxy.maledictusEarthquakeDamage());
				boolean flag = entity.hurt(golem.damageSources().mobAttack(golem), damage);
				if (flag) {
					EarthquakeHelper.launch(golem, entity, 0.5f);
				}
			}
		}
		dev.xkmc.mob_weapon_api.integration.cataclysm.CataclysmProxy.spawnHalberd(golem);
	}

}
