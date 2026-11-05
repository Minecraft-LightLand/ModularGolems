package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import dev.xkmc.modulargolems.util.TNTLauncher;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class FlameThrowerItem extends ProjectileWeaponItem implements IGenericShoulderCannonAnimated {

	public static void setCharge(ItemStack stack, int charge) {
		stack.getOrCreateTag().putInt("BlazeCharge", charge);
	}

	public static int getCharge(ItemStack stack) {
		var tag = stack.getTag();
		if (tag == null) return 0;
		return tag.getInt("BlazeCharge");
	}

	public FlameThrowerItem(Properties p) {
		super(p);
	}

	@Override
	public void onTick(AbstractGolemEntity<?, ?> e, ItemStack stack, int slot) {
		if (e.tickCount % 40 != (slot < 0 ? 30 : 10)) return;
		var target = e.getTarget();
		if (target == null || !target.isAlive()) return;
		var pose = e.getCannonPose(CannonPoseUtil.FLAME_THROWER);
		if (pose.isOutOfRange(e, slot, 15)) return;
		var ammo = e.getProjectile(stack);
		var pos = pose.getOrigin(e, slot);
		if (ammo.is(Items.TNT)) {
			var tnt = TNTLauncher.getTNTEntity(e, pos, target);
			if (tnt == null) return;
			e.level().addFreshEntity(tnt);
			if (!e.isSilent())
				e.playSound(SoundEvents.BLAZE_SHOOT);
			if (!e.isHostile())
				ammo.shrink(1);
			return;
		}
		var dst = target.position().add(0, target.getBbHeight() / 2, 0);
		var dir = dst.subtract(pos).normalize();
		if (ammo.is(Items.FIRE_CHARGE)) {
			var proj = new LargeFireball(e.level(), e, dir.x, dir.y, dir.z, 2);
			proj.setPos(pos);
			proj.setDeltaMovement(dir);
			e.level().addFreshEntity(proj);
			if (!e.isSilent())
				e.playSound(SoundEvents.BLAZE_SHOOT);
			if (!e.isHostile())
				ammo.shrink(1);
			return;
		}
		if (!e.isHostile() && stack.getEnchantmentLevel(Enchantments.INFINITY_ARROWS) <= 0) {
			int charge = getCharge(stack);
			if (charge <= 0) {
				if (!ammo.is(Items.BLAZE_POWDER)) return;
				setCharge(stack, 15);
				if (!e.isHostile())
					ammo.shrink(1);
			} else {
				setCharge(stack, charge - 1);
			}
		}
		var proj = new SmallFireball(e.level(), e, dir.x, dir.y, dir.z);
		proj.setPos(pos);
		proj.setDeltaMovement(dir);
		e.level().addFreshEntity(proj);
		if (!e.isSilent())
			e.playSound(SoundEvents.BLAZE_SHOOT);
	}

	@Override
	public @Nullable ResourceLocation getAnimBaseId(AbstractGolemEntity<?, ?> user, ItemStack stack, int slot) {
		// deploy animations reuse the beacon cannon models on both shoulders
		return slot < 0 ? GolemModelPaths.BEACON_LEFT : GolemModelPaths.BEACON_RIGHT;
	}

	@Override
	public @Nullable ResourceLocation getModel(int slot) {
		return slot < 0 ? GolemModelPaths.FLAME_LEFT : GolemModelPaths.FLAME_RIGHT;
	}

	@Override
	public ResourceLocation getModelTexture(AbstractGolemEntity<?, ?> entity, ItemStack stack, int slot) {
		var id = ForgeRegistries.ITEMS.getKey(this);
		assert id != null;
		return id.withPath(e -> "textures/equipments/" + e + ".png");
	}

	@Override
	public ResourceLocation getEmissiveTexture(AbstractGolemEntity<?, ?> entity, ItemStack stack, int slot) {
		var id = ForgeRegistries.ITEMS.getKey(this);
		assert id != null;
		return id.withPath(e -> "textures/equipments/" + e + "_emissive.png");
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		list.add(MGLangData.FLAMETHROWER.get());
		list.add(MGLangData.FLAMETHROWER_TNT.get());
		list.add(MGLangData.FLAMETHROWER_FIRECHARGE.get());
		list.add(MGLangData.FLAMETHROWER_FLAME.get());
		list.add(MGLangData.GOLEM_EQUIPMENT.get(GolemTypes.ENTITY_GOLEM.get().getDescription().copy().withStyle(ChatFormatting.GOLD))
				.withStyle(ChatFormatting.UNDERLINE));
		list.add(MGLangData.SHOULDER_WEAPON.get());
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment == Enchantments.INFINITY_ARROWS;
	}

	private static boolean supports(ItemStack stack) {
		return stack.is(Items.BLAZE_POWDER) || stack.is(Items.FIRE_CHARGE) || stack.is(Items.TNT);
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return FlameThrowerItem::supports;
	}

	@Override
	public int getDefaultProjectileRange() {
		return 15;
	}

	@Override
	public boolean emissive() {
		return true;
	}

}
