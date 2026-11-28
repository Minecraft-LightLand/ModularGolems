package dev.xkmc.modulargolems.content.entity.humanoid.weapon;

import dev.xkmc.mob_weapon_api.api.goals.IMeleeGoal;
import dev.xkmc.mob_weapon_api.example.goal.SmartHoldRangedAttackGoal;
import dev.xkmc.mob_weapon_api.registry.WeaponStatus;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;

/**
 * 「近战型 HOLD 武器」（灾变绞肉锯、歼灭刃锯等持续近战武器）的通用 Goal。
 * <p>
 * 父类（{@code SmartHoldRangedAttackGoal}）用 {@code distanceTo(target) < range} 判断是否进入「使用中」，
 * 且对近战 HOLD 强制 {@code keepDistance=false}（一直 moveTo 贴身）。这会带来两个问题：
 * <ol>
 *   <li>用<b>中心距</b>比，体积极大的目标永远进不了「使用中」→ 锯刃不转、只能 melee 拉扯/挥击；</li>
 *   <li>近战 HOLD 不保持距离 → 一进入使用就贴死目标，没有「拉扯」。</li>
 * </ol>
 * 这里对近战 HOLD：射程改用<b>melee 实际命中距离</b>（√(ENTITY_REACH² + 目标宽度)），并让它<b>保持距离</b>
 * （{@code strafing()} 太近后退、太远接近、中间停住），于是会「拉扯着切割」而不是贴脸。
 * 另外 {@code canUse()} 在滞空 / 技能互斥锁未释放时不启动，避免打断大跳、冲锋的落地结算。
 * 远程 HOLD（声波炮）不受影响，仍是中心距、保持距离。
 */
public class GolemHoldAttackGoal<E extends Mob> extends SmartHoldRangedAttackGoal<E> {

	private int meleeAttackTime = -1;

	public GolemHoldAttackGoal(E mob, IMeleeGoal melee, double speed) {
		super(mob, melee, speed);
	}

	private boolean isMelee(ItemStack stack) {
		return getWeaponStatus(stack).map(WeaponStatus::isMelee).orElse(false);
	}

	/** 近战 HOLD 的射程 = melee 实际命中距离（ENTITY_REACH² + 目标宽度）外加缓冲。
	 *  这样只要 melee 够得着，hold 也判为在射程内，消除「最边缘只挥击不开转」的夹层。 */
	@Override
	public double range(ItemStack stack) {
		double base = super.range(stack);
		if (isMelee(stack)) {
			LivingEntity target = mob.getTarget();
			if (target != null) {
				double reach = mob.getAttributeValue(ForgeMod.ENTITY_REACH.get());
				return Math.sqrt(reach * reach + target.getBbWidth()) + 0.5;
			}
		}
		return base;
	}

	/** 近战 HOLD 也保持距离（range() 已是边缘距，能保证 withInRange），否则会贴脸而不是拉扯。 */
	@Override
	protected boolean keepDistance(ItemStack stack) {
		if (isMelee(stack)) return true;
		return super.keepDistance(stack);
	}

	@Override
	public boolean canUse() {
		if (!super.canUse()) return false;
		// 滞空（大跳/冲锋起跳途中）或技能互斥锁未释放时，绝不能启动 hold goal；
		// 否则会在半空抢占 melee goal，导致 tryJumpAttack 的落地结算永远跑不到（大跳卡死）。
		if (!mob.onGround()) return false;
		if (mob instanceof AbstractGolemEntity<?, ?> g && EarthquakeHelper.isBusy(g)) return false;
		ItemStack stack = mob.getItemInHand(holder.getWeaponHand());
		if (isMelee(stack)) {
			LivingEntity target = mob.getTarget();
			if (target == null) return false;
			double r = range(stack) + 0.5;
			boolean ret = mob.distanceToSqr(target) < r * r;
			return ret;
		}
		return true;
	}

	@Override
	public void stop() {
		meleeAttackTime = -1;
		super.stop();
	}

	/** 近战 HOLD 的「拉扯」与普通武器完全一致（见 GolemMeleeGoal.tickMove）：
	 *  进入近战距离后 moveControl.strafe(-0.5F / -1F, 0) 向后拉，离远则导航靠近。 */
	@Override
	protected void strafing() {
		ItemStack stack = mob.getItemInHand(holder.getWeaponHand());
		if (!isMelee(stack)) {
			super.strafing();
			return;
		}
		LivingEntity target = mob.getTarget();
		if (target == null) return;
		boolean sight = mob.getSensing().hasLineOfSight(target);
		if (sight != seeTime > 0) seeTime = 0;
		if (sight) ++seeTime; else --seeTime;
		double dist = Math.sqrt(mob.distanceToSqr(target));
		double end = range(stack) - 0.5;   // melee 实际命中距离
		double far = end - 0.5;
		boolean hasRange = mob instanceof AbstractGolemEntity<?, ?> ag && ag.hasRangeAttack();
		if (dist < far && end > 2.4 || hasRange) {
			if (!mob.getNavigation().isDone()) mob.getNavigation().stop();
			mob.getMoveControl().strafe(hasRange || dist < far - 1 ? -1F : -0.5F, 0);
		} else if (dist > far) {
			mob.getNavigation().moveTo(target, speedModifier);
		}
		mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
	}

	/**
	 * 与 {@code SmartHoldRangedAttackGoal#tick()} 一致，唯一差别是 {@code withInRange} 用 {@link #range(ItemStack)}（近战为边缘距）。
	 */
	@Override
	public void tick() {
		ItemStack stack = mob.getItemInHand(holder.getWeaponHand());
		var weapon = get(stack);
		if (weapon.isEmpty()) return;
		if (weapon.get().canMelee()) doMelee();
		strafing();
		LivingEntity target = mob.getTarget();
		boolean invalidTarget = target == null || !target.isAlive();
		double r = range(stack) + 0.5;
		boolean withInRange = !invalidTarget && mob.distanceToSqr(target) < r * r;
		var user = holder.toUser();
		if (mob.isUsingItem()) {
			boolean infiniteUse = weapon.get().infiniteUse(mob, stack);
			if (seeTime < -60 || infiniteUse && !withInRange) {
				mob.stopUsingItem();
			} else if (seeTime > 0 && !invalidTarget) {
				int i = mob.getTicksUsingItem();
				if (!infiniteUse && i >= weapon.get().holdTime(mob, stack)) {
					meleeAttackTime = weapon.get().trigger(user, stack, target, i);
					mob.stopUsingItem();
				} else {
					weapon.get().tickUsing(user, stack, i);
				}
			}
		} else if (meleeFinished() && --meleeAttackTime <= 0 && seeTime >= -60 && withInRange) {
			mob.startUsingItem(holder.getWeaponHand());
			weapon.get().startHolding(user.user(), stack, holder.getWeaponHand());
		}
	}

}
