package dev.xkmc.modulargolems.compat.materials.iceandfire.proxy;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.component.IafEntityData;
import com.iafenvoy.iceandfire.entity.EntityFireDragon;
import com.iafenvoy.iceandfire.entity.EntityIceDragon;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class IAFProxyCE implements IAFProxy {

	@Override
	public void fireHit(LivingEntity target, LivingEntity user, int level) {
		if (IafCommonConfig.INSTANCE.armors.dragonFireAbility.getValue()) {
			if (target instanceof EntityIceDragon) {
				target.hurt(user.level().damageSources().inFire(), 3.5F + 5 * level);
			}
			target.setSecondsOnFire(5 * level);
			target.knockback(1.0, user.getX() - target.getX(), user.getZ() - target.getZ());
		}

	}

	@Override
	public void iceHit(LivingEntity target, LivingEntity user, int level) {

		if (IafCommonConfig.INSTANCE.armors.dragonIceAbility.getValue()) {
			if (target instanceof EntityFireDragon) {
				target.hurt(user.level().damageSources().drown(), 3.5F + 5 * level);
			}

			IafEntityData data = IafEntityData.get(target);
			data.frozenData.setFrozen(target, 100 * level);
			target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, level));
			target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, level));
			target.knockback(1.0, user.getX() - target.getX(), user.getZ() - target.getZ());
		}
	}

	@Override
	public void lightningHit(LivingEntity target, LivingEntity user, int level) {
		if (IafCommonConfig.INSTANCE.armors.dragonLightningAbility.getValue()) {
			boolean flag = !(user instanceof Player) || !((double) user.attackAnim > 0.2);

			if (!user.level().isClientSide && flag) {
				LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(target.level());

				assert lightningboltentity != null;

				lightningboltentity.getTags().add("iceandfire.bolt_skip_loot");
				lightningboltentity.getTags().add(user.getStringUUID());
				lightningboltentity.addTag("l2weaponry:lightning");
				lightningboltentity.setDamage(3 + 2 * level);
				lightningboltentity.moveTo(target.position());
				if (!target.level().isClientSide) {
					target.level().addFreshEntity(lightningboltentity);
				}
			}

			if (target instanceof EntityFireDragon || target instanceof EntityIceDragon) {
				target.hurt(user.level().damageSources().lightningBolt(), 1.5F + 4 * level);
			}

			target.knockback(1.0, user.getX() - target.getX(), user.getZ() - target.getZ());
		}

	}

	@Override
	public String modid() {
		return IceAndFire.MOD_ID;
	}

	@Override
	public Supplier<Item> ingotIceSteel() {
		return IafItems.DRAGONSTEEL_ICE_INGOT;
	}

	@Override
	public Supplier<Item> ingotFireSteel() {
		return IafItems.DRAGONSTEEL_FIRE_INGOT;
	}

	@Override
	public Supplier<Item> ingotLightningSteel() {
		return IafItems.DRAGONSTEEL_LIGHTNING_INGOT;
	}


}
