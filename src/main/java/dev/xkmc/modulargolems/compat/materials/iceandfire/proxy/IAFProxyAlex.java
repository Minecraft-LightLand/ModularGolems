package dev.xkmc.modulargolems.compat.materials.iceandfire.proxy;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityFireDragon;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.entity.props.EntityDataProvider;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class IAFProxyAlex implements IAFProxy {

	@Override
	public void fireHit(LivingEntity target, LivingEntity user, int level) {
		if (IafConfig.dragonWeaponFireAbility) {
			if (target instanceof EntityIceDragon) {
				target.hurt(user.level().damageSources().inFire(), 3.5F + 5 * level);
			}

			target.setSecondsOnFire(5 * level);
			target.knockback(1.0D, user.getX() - target.getX(), user.getZ() - target.getZ());
		}
	}

	@Override
	public void iceHit(LivingEntity target, LivingEntity user, int level) {
		if (IafConfig.dragonWeaponIceAbility) {
			if (target instanceof EntityFireDragon) {
				target.hurt(user.level().damageSources().drown(), 3.5F + 5 * level);
			}
			EntityDataProvider.getCapability(target).ifPresent((data) -> data.frozenData.setFrozen(target, 100 * level));
			target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, level));
			target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, level));
			target.knockback(1.0D, user.getX() - target.getX(), user.getZ() - target.getZ());
		}
	}

	@Override
	public void lightningHit(LivingEntity target, LivingEntity user, int level) {
		if (IafConfig.dragonWeaponLightningAbility) {
			boolean flag = !(user instanceof Player) || !((double) user.attackAnim > 0.2D);
			if (!user.level().isClientSide && flag) {
				LightningBolt entity = EntityType.LIGHTNING_BOLT.create(target.level());
				assert entity != null;
				entity.addTag("l2weaponry:lightning");
				entity.moveTo(target.position());
				entity.setDamage(3 + 2 * level);
				if (user instanceof ServerPlayer sp) entity.setCause(sp);
				if (!target.level().isClientSide) {
					target.level().addFreshEntity(entity);
				}
			}

			if (target instanceof EntityFireDragon || target instanceof EntityIceDragon) {
				target.hurt(user.level().damageSources().lightningBolt(), 1.5F + 4 * level);
			}

			target.knockback(1.0D, user.getX() - target.getX(), user.getZ() - target.getZ());
		}
	}

	@Override
	public String modid() {
		return IceAndFire.MODID;
	}

	@Override
	public Supplier<Item> ingotIceSteel() {
		return IafItemRegistry.DRAGONSTEEL_ICE_INGOT;
	}

	@Override
	public Supplier<Item> ingotFireSteel() {
		return IafItemRegistry.DRAGONSTEEL_FIRE_INGOT;
	}

	@Override
	public Supplier<Item> ingotLightningSteel() {
		return IafItemRegistry.DRAGONSTEEL_LIGHTNING_INGOT;
	}

}
