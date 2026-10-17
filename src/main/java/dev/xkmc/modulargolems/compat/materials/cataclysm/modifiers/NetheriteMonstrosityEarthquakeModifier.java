package dev.xkmc.modulargolems.compat.materials.cataclysm.modifiers;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.CataCompatRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.modifier.base.AttributeGolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import dev.xkmc.modulargolems.util.GolemUtils;
import dev.xkmc.modulargolems.util.GolemCombatHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

import net.minecraftforge.event.entity.living.LivingHurtEvent;
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
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (EarthquakeHelper.isLeaping(entity)) event.setAmount(event.getAmount() * EarthquakeHelper.LEAP_DAMAGE_MULT);
	}

	@Override
	public void handleEvent(AbstractGolemEntity<?, ?> golem, int value, byte event) {
		if (event == EarthquakeHelper.FLAG) {
			EarthquakeHelper.makeParticles(golem, 0, 0);
		}
	}

	@Override
	public double getEarthquakeRangeSqr(AbstractGolemEntity<?, ?> golem, LivingEntity target, int lv) {
		return 8 * 8;
	}

	@Override
	public void performEarthQuake(AbstractGolemEntity<?, ?> golem, int level) {
		GolemCataProxy.monstrosityEarthquakeFX(golem);
		for (LivingEntity entity : golem.level().getEntitiesOfClass(LivingEntity.class, golem.getBoundingBox().inflate(7.0))) {
			if (GolemTargetUtil.canTarget(golem, entity)) {
				float damage = GolemUtils.adjustedDamage(
						(float) golem.getAttributeValue(Attributes.ATTACK_DAMAGE),
						GolemCataProxy.monstrosityEarthquakeDamage() * entity.getMaxHealth()
				);
				float factor = 0;
				if (golem.getItemBySlot(EquipmentSlot.HEAD).is(CataCompatRegistry.MONSTROSITY_HELMET.get()))
					factor++;
				if (golem.getItemBySlot(EquipmentSlot.LEGS).is(CataCompatRegistry.MONSTROSITY_SHINGUARD.get()))
					factor++;
				var src = golem.damageSources().mobAttack(golem);
				boolean flag = entity.hurt(src, damage * (1 + MGConfig.COMMON.earthquakeArmorBonus.get().floatValue() * factor));
				if (!flag) GolemCombatHelper.breakShield(golem, entity, src);
				if (flag) {
					EarthquakeHelper.launch(golem, entity, 2f);
				}
			}
		}
	}


	@Override
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc", EarthquakeHelper.leapReduction()).withStyle(ChatFormatting.GREEN));
	}

}
