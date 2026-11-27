package dev.xkmc.modulargolems.content.item.equipments.realm;

import dev.xkmc.modulargolems.content.client.armor.GolemModelPaths;
import dev.xkmc.modulargolems.content.client.weapon.IEntityModelWeapon;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.item.equipments.MetalGolemWeaponItem;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 歼灭刃锯（界域联动，大型傀儡专属）。属性同下界合金巨剑；手臂锯刃模型见 {@code RealmAnnihilationSawModel}，
 * 攻击行为见 {@code RealmAnnihilationSawBehavior}（由 MobWeaponAPI 驱动：不挥击、锯刃持续旋转高频切割）。
 */
public class RealmAnnihilationSaw extends MetalGolemWeaponItem implements IEntityModelWeapon {

	public RealmAnnihilationSaw(Properties properties) {
		super(properties, 10, 0, 1, 2);
	}

	/** 必须为正，否则 startUsingItem 当帧即结束、永远进不了"使用中"，tickUsing 不会触发（对照绞肉锯/音波炮=72000）。 */
	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public ResourceLocation getModelForHand(InteractionHand hand) {
		return hand == InteractionHand.OFF_HAND ? GolemModelPaths.SAW_OFFHAND : GolemModelPaths.SAW_MAINHAND;
	}

	@Override
	public ResourceLocation getModelTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		return ModularGolems.loc("textures/equipments/realm_annihilation_saw.png");
	}

	@Override
	public boolean emissive() {
		return true;
	}

	@Override
	public ResourceLocation getEmissiveTexture(MetalGolemEntity entity, ItemStack stack, InteractionHand hand) {
		return ModularGolems.loc("textures/equipments/realm_annihilation_saw_emissive.png");
	}

	@Override
	public boolean shouldPlayAnimation(LivingEntity user, ItemStack stack, InteractionHand hand) {
		return true;
	}

	@Override
	public float getAnimationSpeed(LivingEntity user, ItemStack stack, InteractionHand hand) {
		return 1.0F;
	}

	@Override
	public float getAnimationTick(LivingEntity user, ItemStack stack, InteractionHand hand) {
		return user.tickCount;
	}

}
