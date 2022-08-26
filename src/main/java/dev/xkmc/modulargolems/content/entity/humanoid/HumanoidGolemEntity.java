package dev.xkmc.modulargolems.content.entity.humanoid;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.modulargolems.content.entity.common.SweepGolemEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

@SerialClass
public class HumanoidGolemEntity extends SweepGolemEntity<HumanoidGolemEntity, HumaniodGolemPartType> {

	public HumanoidGolemEntity(EntityType<HumanoidGolemEntity> type, Level level) {
		super(type, level);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (e) -> e instanceof Enemy && !(e instanceof Creeper)));
		this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (itemstack.isEmpty()) {
			if (!player.level.isClientSide) {
				for (EquipmentSlot slot : EquipmentSlot.values()) {
					dropSlot(slot);
				}
			}
			return super.mobInteract(player, hand);
		}
		EquipmentSlot slot = getEquipmentSlotForItem(itemstack);
		if (itemstack.canEquip(slot, this)) {
			if (level.isClientSide()) {
				return InteractionResult.SUCCESS;
			}
			if (hasItemInSlot(slot)) {
				dropSlot(slot);
			}
			setItemSlot(slot, itemstack.split(1));
			return InteractionResult.CONSUME;
		}
		return InteractionResult.FAIL;
	}

	protected void dropCustomDeathLoot(DamageSource source, int i, boolean b) {
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			dropSlot(slot);
		}
	}

	private void dropSlot(EquipmentSlot slot) {
		ItemStack itemstack = this.getItemBySlot(slot);
		if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack)) {
			this.spawnAtLocation(itemstack);
			this.setItemSlot(slot, ItemStack.EMPTY);
		}
	}

}
