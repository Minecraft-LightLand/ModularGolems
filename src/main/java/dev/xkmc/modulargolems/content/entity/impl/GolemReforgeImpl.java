package dev.xkmc.modulargolems.content.entity.impl;

import dev.xkmc.l2library.util.math.MathHelper;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.entity.common.ReforgeUpdatePacket;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.UUID;

public interface GolemReforgeImpl extends GolemImplBase {

	UUID REFORGE_ID = MathHelper.getUUIDFromString("GolemReforge");

	default int getMaxReforge() {
		return GolemType.getGolemType(asGolem().getType()).getBodyPart().toItem().count - 1;
	}

	default int getReforgeBase() {
		int total = 0;
		for (var e : GolemType.getGolemType(asGolem().getType()).values()) {
			total += e.toItem().count;
		}
		return total;
	}

	default void updateReforge(int reforge) {
		var self = asGolem();
		self.getPersistentData().putInt("GolemReforge", reforge);
		if (!self.level().isClientSide()) {
			var ins = self.getAttribute(Attributes.MAX_HEALTH);
			assert ins != null;
			ins.removeModifier(REFORGE_ID);
			ins.addPermanentModifier(new AttributeModifier(
					REFORGE_ID, "Golem Reforge " + reforge,
					-1d * reforge / getReforgeBase(),
					AttributeModifier.Operation.MULTIPLY_TOTAL
			));
		}
		self.markSizeDirty();
		self.checkSize();
		if (!self.level().isClientSide()) {
			ModularGolems.HANDLER.toTrackingPlayers(ReforgeUpdatePacket.of(self, reforge), self);
		}
	}

	default void checkReforge() {
		var self = asGolem();
		if (self.isAlive() && self.getGuardedDataImpl() <= self.getMaxHealth() / 2) {
			int reforge = self.getPersistentData().getInt("GolemReforge");
			if (reforge < getMaxReforge()) {
				reforge++;
				updateReforge(reforge);
				self.repair(self.getMaxHealth() / 4);
			}
		}
	}

	default boolean isReforged() {
		return asGolem().getPersistentData().getInt("GolemReforge") > 0;
	}

	default int getReforgeCount() {
		return asGolem().getPersistentData().getInt("GolemReforge");
	}

	default void repairWithItem() {
		var self = asGolem();
		int reforge = self.getPersistentData().getInt("GolemReforge");
		if (self.getGuardedDataImpl() > 0.75 * self.getMaxHealth() && reforge > 0)
			updateReforge(reforge - 1);
		else self.repair(self.getMaxHealth() / 4);

		float f1 = 1.0F + (self.getRandom().nextFloat() - self.getRandom().nextFloat()) * 0.2F;
		self.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, f1);
	}

	default void repair(float amount) {
		var self = asGolem();
		self.setGuardedDataImpl(Math.min(self.getMaxHealth(), self.getGuardedDataImpl() + amount), true, true);
	}

	@Nullable
	default InteractionResult tryRepairWithBodyMaterial(Player player, InteractionHand hand, GolemMaterial mat) {
		var self = asGolem();
		ItemStack itemstack = player.getItemInHand(hand);
		if (itemstack.isEmpty()) return null;
		Ingredient ing = GolemMaterialConfig.get().getRepairIngredient(mat.id());
		if (!ing.test(itemstack)) return null;
		if (!player.getAbilities().instabuild && self.isHostile()) return InteractionResult.PASS;
		if (self.getGuardedDataImpl() >= self.getMaxHealth() && !isReforged()) {
			return InteractionResult.PASS;
		}
		repairWithItem();
		if (!player.getAbilities().instabuild) {
			itemstack.shrink(1);
		}
		if (!self.level().isClientSide()) {
			GolemTriggers.HOT_FIX.trigger((ServerPlayer) player);
		}
		return InteractionResult.sidedSuccess(self.level().isClientSide);
	}


}
