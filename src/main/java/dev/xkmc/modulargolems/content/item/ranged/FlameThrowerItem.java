package dev.xkmc.modulargolems.content.item.ranged;

import dev.xkmc.l2core.init.reg.ench.EnchHelper;
import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.init.data.MGLangData;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import dev.xkmc.modulargolems.util.TNTLauncher;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class FlameThrowerItem extends ProjectileWeaponItem implements IShoulderCannonAnimated {

	public static void setCharge(ItemStack stack, int charge) {
		stack.set(GolemItems.DC_CHARGE, charge);
	}

	public static int getCharge(ItemStack stack) {
		return stack.getOrDefault(GolemItems.DC_CHARGE, 0);
	}

	public FlameThrowerItem(Properties p) {
		super(p);
	}

	@Override
	protected void shootProjectile(LivingEntity e, Projectile proj, int t, float x, float y, float z, @Nullable LivingEntity target) {
	}

	@Override
	public void onTick(MetalGolemEntity e, ItemStack stack, InteractionHand hand) {
		if (e.tickCount % 40 != (hand == InteractionHand.MAIN_HAND ? 10 : 30)) return;
		var target = e.getTarget();
		if (target == null || !target.isAlive()) return;
		if (CannonPoseUtil.BEACON.isOutOfRange(e, hand, 15)) return;
		var ammo = e.getProjectile(stack);
		var pos = CannonPoseUtil.BEACON.getOrigin(e, hand);
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
			var proj = new LargeFireball(e.level(), e, dir, 2);
			proj.setPos(pos);
			proj.setDeltaMovement(dir);
			e.level().addFreshEntity(proj);
			if (!e.isSilent())
				e.playSound(SoundEvents.BLAZE_SHOOT);
			if (!e.isHostile())
				ammo.shrink(1);
			return;
		}
		if (!e.isHostile() && EnchHelper.getLv(stack, Enchantments.INFINITY) <= 0) {
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
		var proj = new SmallFireball(e.level(), e, dir);
		proj.setPos(pos);
		proj.setDeltaMovement(dir);
		e.level().addFreshEntity(proj);
		if (!e.isSilent())
			e.playSound(SoundEvents.BLAZE_SHOOT);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, TooltipDisplay display, Consumer<Component> list, TooltipFlag flag) {
		list.accept(MGLangData.FLAMETHROWER.get());
		list.accept(MGLangData.FLAMETHROWER_TNT.get());
		list.accept(MGLangData.FLAMETHROWER_FIRECHARGE.get());
		list.accept(MGLangData.FLAMETHROWER_FLAME.get());
		list.accept(MGLangData.GOLEM_EQUIPMENT.get(GolemTypes.ENTITY_GOLEM.get().getDescription().copy().withStyle(ChatFormatting.GOLD))
				.withStyle(ChatFormatting.UNDERLINE));
		list.accept(MGLangData.SHOULDER_WEAPON.get());
	}

	@Override
	public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
		return super.isPrimaryItemFor(stack, enchantment) || enchantment.is(Enchantments.INFINITY);
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		return super.supportsEnchantment(stack, enchantment) || enchantment.is(Enchantments.INFINITY);
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
	public @Nullable Identifier getModelForHand(InteractionHand hand) {
		return hand == InteractionHand.MAIN_HAND ? GolemModelPaths.FLAME_RIGHT : GolemModelPaths.FLAME_LEFT;
	}

	@Override
	public @Nullable Identifier getAnimBaseId(MetalGolemEntity user, ItemStack stack, InteractionHand hand) {
		return hand == InteractionHand.MAIN_HAND ? GolemModelPaths.BEACON_RIGHT : GolemModelPaths.BEACON_LEFT;
	}

	@Override
	public boolean emissive() {
		return true;
	}

	@Override
	public Identifier getEmissiveTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		var id = BuiltInRegistries.ITEM.getKey(this);
		return id.withPath(e -> "textures/equipments/" + e + "_emissive.png");
	}

	@Override
	public Identifier getModelTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		var id = BuiltInRegistries.ITEM.getKey(this);
		return id.withPath(e -> "textures/equipments/" + e + ".png");
	}

}
