package dev.xkmc.modulargolems.init.registrate;

import com.tterrag.registrate.util.entry.EntityEntry;
import dev.xkmc.modulargolems.content.entity.misc.BeaconLaserEntity;
import dev.xkmc.modulargolems.content.entity.misc.BeaconLaserRenderer;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.world.entity.MobCategory;

public class GolemMiscEntities {

	public static final EntityEntry<BeaconLaserEntity> LASER = ModularGolems.REGISTRATE
			.<BeaconLaserEntity>entity("beacon_laser", BeaconLaserEntity::new, MobCategory.MISC)
			.properties(p -> p.fireImmune().noSave().noSummon().sized(0, 0))
			.renderer(() -> BeaconLaserRenderer::new)
			.register();

	public static void register() {

	}

}
