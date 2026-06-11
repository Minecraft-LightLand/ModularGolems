package dev.xkmc.modulargolems.compat.materials.cataclysm;

import dev.xkmc.cataclysm_mux.GolemCataProxy;
import dev.xkmc.modulargolems.compat.materials.cataclysm.armor.MaledictusArmorItem;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.init.data.MGConfig;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class CataEventHandler {

	@SubscribeEvent
	public static void onDeath(LivingDeathEvent event) {
		if (!(event.getEntity() instanceof MetalGolemEntity golem)) return;
		if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
		var stack = golem.getItemBySlot(EquipmentSlot.CHEST);
		if (!stack.is(CataCompatRegistry.MALEDICTUS_CHESTPLATE.get())) return;
		long prev = stack.getOrCreateTag().getLong("NextAvailableTime");
		long time = golem.level().getGameTime();
		if (prev > time) return;
		event.setCanceled(true);
		int count = MaledictusArmorItem.getCount(golem);
		int cd = MGConfig.COMMON.maledictusReviveCD.get();
		int red = MGConfig.COMMON.maledictusReviveCDPartReduction.get();
		cd = (cd - red * count) * 20;
		long next = time + cd;
		stack.getOrCreateTag().putLong("NextAvailableTime", next);
		double php = MGConfig.COMMON.maledictusRevivePHP.get();
		double bonus = MGConfig.COMMON.maledictusRevivePHPPartBonus.get();
		php = php + bonus * count;
		golem.setGuardedDataImpl(golem.getMaxHealth() * (float) php);
		golem.level().playSound(null, golem.getX(), golem.getY(), golem.getZ(), SoundEvents.TOTEM_USE, golem.getSoundSource(), 1.25F, 1.0F);
		golem.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0));
		double d0 = golem.getX();
		double d1 = golem.getY() + golem.getBbHeight() + 1;
		double d2 = golem.getZ();
		var particle = new ResourceLocation(CataDispatch.MODID, "cursed_algiz");
		var reg = ForgeRegistries.PARTICLE_TYPES;
		if (!reg.containsKey(particle)) return;
		if (reg.getValue(particle) instanceof ParticleOptions type && golem.level() instanceof ServerLevel sl)
			sl.sendParticles(type, d0, d1, d2, 1, 0.0F, 0.0F, 0.0F, 0.0F);
	}

	@SubscribeEvent
	public static void onExplosion(ExplosionEvent.Detonate event) {
		var direct = event.getExplosion().getDirectSourceEntity();
		var owner = event.getExplosion().getIndirectSourceEntity();
		if (direct == null || !(owner instanceof AbstractGolemEntity<?, ?> golem)) return;
		boolean fireball = GolemCataProxy.isIgnisExplosive(direct);
		boolean strike = GolemCataProxy.isIgnisStrike(direct);
		if (fireball || strike) {
			if (!golem.isHostile()) event.getAffectedBlocks().clear();
			event.getAffectedEntities().removeIf(e -> {
				if (e instanceof ItemEntity) return true;
				if (e instanceof LivingEntity le) {
					if (!golem.canAttack(le)) return true;
					if (GolemCataProxy.isAbyssFireball(direct)) {
						GolemCataProxy.inflictStun(golem, le, 60);
						le.invulnerableTime = 0;
					} else if (GolemCataProxy.isSoul(direct)) {
						GolemCataProxy.inflictStun(golem, le, 40);
						le.invulnerableTime = 0;
					}
				}
				if (e instanceof TraceableEntity proj) {
					if (proj.getOwner() == golem) {
						return true;
					}
				}
				return false;
			});
		}
	}

	@SubscribeEvent
	public static void onMobGrief(EntityMobGriefingEvent event) {
		var owner = GolemCataProxy.getOwner(event.getEntity());
		if (owner instanceof AbstractGolemEntity<?, ?> golem) {
			if (!golem.isHostile()) {
				event.setResult(Event.Result.DENY);
			}
		}
	}

}
