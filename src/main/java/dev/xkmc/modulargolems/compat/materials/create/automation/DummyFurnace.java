package dev.xkmc.modulargolems.compat.materials.create.automation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class DummyFurnace extends ProjectileWeaponItem {

	public DummyFurnace() {
		super(new Properties());
	}

	private static boolean isValid(ItemStack stack) {
		return !stack.isEmpty() && (stack.isStackable() || stack.hasCraftingRemainingItem()) &&
				stack.getBurnTime(RecipeType.SMELTING) > 0;
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return DummyFurnace::isValid;
	}

	@Override
	public int getDefaultProjectileRange() {
		return 0;
	}

	@Override
	protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {

	}

}
