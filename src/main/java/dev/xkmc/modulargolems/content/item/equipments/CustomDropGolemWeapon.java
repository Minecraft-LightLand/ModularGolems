package dev.xkmc.modulargolems.content.item.equipments;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;

public interface CustomDropGolemWeapon {

	boolean dropCustomDeathLoot(ServerLevel level, AbstractGolemEntity<?, ?> self, MetalGolemEntity attacker, ItemStack stack, DamageSource source);
}
