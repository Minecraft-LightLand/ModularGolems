package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.modulargolems.compat.materials.cataclysm.CataclysmProxy;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.AttributeGolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.function.Consumer;

public class NetheriteMonstrosityEarthquakeModifier extends AttributeGolemModifier implements EarthquakeHelper.Modifier {

	public NetheriteMonstrosityEarthquakeModifier() {
		super(1,
				new AttrEntry(GolemTypes.STAT_ATTACK, () -> 5),
				new AttrEntry(GolemTypes.STAT_ARMOR, () -> 5),
				new AttrEntry(GolemTypes.STAT_TOUGH, () -> 5)
		);
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
		golem.playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 1.0F + golem.getRandom().nextFloat() * 0.1F);
		for (LivingEntity entity : golem.level().getEntitiesOfClass(LivingEntity.class, golem.getBoundingBox().inflate(7.0))) {
			if (!golem.isAlliedTo(entity) && entity != golem) {
				float damage = (float) (golem.getAttributeValue(Attributes.ATTACK_DAMAGE) + entity.getMaxHealth() * CataclysmProxy.monstrosityEarthquakeDamage());
				boolean flag = entity.hurt(golem.damageSources().mobAttack(golem), damage);
				if (flag) {
					EarthquakeHelper.launch(golem, entity, 2f);
				}
			}
		}
	}

}
