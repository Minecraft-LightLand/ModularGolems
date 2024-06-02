package dev.xkmc.modulargolems.compat.materials.botania;

import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.common.entity.BotaniaEntities;
import vazkii.botania.common.entity.ManaBurstEntity;

public class GolemManaBurstEntity extends ManaBurstEntity {

	private final int lv;

	public GolemManaBurstEntity(LivingEntity user, int lv) {
		super(BotaniaEntities.MANA_BURST, user.level());
		setOwner(user);
		this.setBurstSourceCoords(NO_SOURCE);
		this.lv = lv;
	}

	@Override
	protected void onHitEntity(@NotNull EntityHitResult hit) {
		if (this.isAlive()) {
			if (!this.level().isClientSide) {
				var owner = getOwner();
				double dmg = 7;
				if (owner instanceof LivingEntity le) {
					dmg = le.getAttributeValue(Attributes.ATTACK_DAMAGE) * lv * MGConfig.COMMON.manaBurstDamage.get();
				}
				hit.getEntity().hurt(damageSources().indirectMagic(this, getOwner()), (float) dmg);
				this.level().broadcastEntityEvent(this, (byte) 3);
				this.discard();
			}
		}
	}
}
