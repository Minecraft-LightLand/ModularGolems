package dev.xkmc.modulargolems.compat.materials.geoty.modifier;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.undead.HauntedArmorServant;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.init.ModSounds;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class HauntedModifier extends GolemModifier {

	public HauntedModifier() {
		super(StatFilterType.ATTACK, 4);
	}

	@Override
	public void onKillTarget(AbstractGolemEntity<?, ?> golem, LivingEntity entity, LivingDeathEvent event, int level) {
		if (!(entity instanceof Enemy)) return;
		double chance = 1;
		for (var e : EquipmentSlot.values()) {
			if (e.isArmor() && !entity.getItemBySlot(e).isEmpty())
				chance++;
		}
		chance *= level * MGConfig.COMMON.hauntedBaseChance.get();
		if (chance < golem.getRandom().nextDouble()) return;
		HauntedArmorServant summoned = new HauntedArmorServant(ModEntityType.HAUNTED_ARMOR_SERVANT.get(), golem.level());

		for (EquipmentSlot e : EquipmentSlot.values()) {
			summoned.setItemSlot(e, entity.getItemBySlot(e));
			summoned.setGuaranteedDrop(e);
			entity.setItemSlot(e, ItemStack.EMPTY);
		}

		setIfEmpty(summoned, EquipmentSlot.HEAD, ModItems.CURSED_KNIGHT_HELMET.get());
		setIfEmpty(summoned, EquipmentSlot.CHEST, ModItems.CURSED_KNIGHT_CHESTPLATE.get());
		setIfEmpty(summoned, EquipmentSlot.LEGS, ModItems.CURSED_KNIGHT_LEGGINGS.get());
		setIfEmpty(summoned, EquipmentSlot.FEET, ModItems.CURSED_KNIGHT_BOOTS.get());

		summoned.setPersistenceRequired();
		summoned.moveTo(entity.blockPosition(), entity.getYRot(), entity.getXRot());
		summoned.setLeftHanded(entity.getMainArm() == HumanoidArm.LEFT);
		if (golem.getOwner() != null) {
			summoned.setTrueOwner(golem.getOwner());
		} else {
			summoned.setOwnerId(golem.getOwnerUUID());
		}
		if (golem.level().addFreshEntity(summoned)) {
			golem.playSound(ModSounds.SUMMON_SPELL.get());
		}
	}

	private void setIfEmpty(Mob summoned, EquipmentSlot slot, Item item) {
		if (summoned.getItemBySlot(slot).isEmpty()) {
			ItemStack stack = item.getDefaultInstance();
			stack.enchant(Enchantments.VANISHING_CURSE, 1);
			summoned.setItemSlot(slot, stack);
			summoned.setDropChance(slot, 0);
		}
	}

}
