package dev.xkmc.mob_weapon_api.integration.cataclysm;

import com.github.L_Ender.cataclysm.config.CMConfig;
import com.github.L_Ender.cataclysm.entity.projectile.Cursed_Sandstorm_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Phantom_Arrow_Entity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class CataclysmProxy {

	public static final Logger LOGGER = LogManager.getLogger();

	@Nullable
	public static AbstractArrow createGhostArrow(Level level, LivingEntity player, LivingEntity target) {
		try {
			Phantom_Arrow_Entity hommingArrowEntity;
			hommingArrowEntity = new Phantom_Arrow_Entity(level, player, target);
			hommingArrowEntity.setBaseDamage(CMConfig.PlayerPhantomArrowbasedamage);
			return hommingArrowEntity;
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return null;
	}

	@Nullable
	public static Entity createGhostStorm(LivingEntity user, Vec3 pos, Vec3 rot, LivingEntity target) {
		try {
			Cursed_Sandstorm_Entity e = new Cursed_Sandstorm_Entity(user, rot.x, rot.y, rot.z, user.level(),
					(float) CMConfig.CursedSandstormDamage, target);
			e.setPos(pos.x, user.getEyeY() - 0.5, pos.z);
			e.setUp(15);
			return e;
		} catch (Throwable e) {
			LOGGER.throwing(e);
		}
		return null;
	}

}
