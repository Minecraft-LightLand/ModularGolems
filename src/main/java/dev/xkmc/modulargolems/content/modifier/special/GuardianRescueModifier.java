package dev.xkmc.modulargolems.content.modifier.special;

import dev.xkmc.l2library.base.effects.EffectUtil;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.util.GolemTargetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.List;

/**
 * 单骑救主（追溯指针升级）：所有者血量低于 {@link #OWNER_HP_RATIO} 且受击时，
 * 傀儡立刻传送到所有者身边，并把攻击者的仇恨吸引到自己身上（照钟升级的嘲讽写法）。
 */
public class GuardianRescueModifier extends GolemModifier implements ICooldownSkill {

	/** 触发所需的"所有者血量占比"上限：50%。 */
	public static final float OWNER_HP_RATIO = 0.50F;
	/** 额外嘲讽的半径（格）。 */
	public static final double TAUNT_RADIUS = 8.0;
	/** 冷却：10 秒。 */
	public static final int COOLDOWN = 200;

	private static final String KEY_COOLDOWN = "GuardianRescueCd";

	public GuardianRescueModifier(StatFilterType type, int maxLevel) {
		super(type, maxLevel);
	}

	/** 由 {@code ModifierEventListeners} 在"所有者受击"时调用（服务端）。 */
	public static void onOwnerAttacked(Player owner, LivingAttackEvent event) {
		if (owner.level().isClientSide()) return;
		if (owner.getHealth() >= owner.getMaxHealth() * OWNER_HP_RATIO) return;
		Entity attacker = event.getSource().getEntity();
		if (attacker == null || attacker == owner) return;
		long time = owner.level().getGameTime();
		var aabb = owner.getBoundingBox().inflate(64);
		for (Object o : owner.level().getEntitiesOfClass(AbstractGolemEntity.class, aabb, g -> g.getOwner() == owner)) {
			if (!(o instanceof AbstractGolemEntity<?, ?> golem)) continue;
			for (var entry : golem.getModifiersExtended().entrySet()) {
				if (entry.getKey() instanceof GuardianRescueModifier mod) {
					mod.tryRescue(golem, owner, attacker, time);
				}
			}
		}
	}

	private void tryRescue(AbstractGolemEntity<?, ?> golem, Player owner, Entity attacker, long time) {
		var data = golem.getPersistentData();
		if (time < data.getLong(KEY_COOLDOWN)) return;
		if (!teleportToOwner(golem, owner)) return;
		data.putLong(KEY_COOLDOWN, time + COOLDOWN);
		taunt(golem, attacker);
	}

	private static boolean teleportToOwner(AbstractGolemEntity<?, ?> golem, Player owner) {
		if (owner.level() != golem.level()) return false;
		golem.level().playSound(null, golem.getX(), golem.getY(), golem.getZ(),
				SoundEvents.ENDERMAN_TELEPORT, golem.getSoundSource(), 1.0F, 1.0F);
		boolean ok = golem.randomTeleport(owner.getX(), owner.getY(), owner.getZ(), true);
		if (ok) golem.getNavigation().stop();
		return ok;
	}

	/**
	 * 先嘲讽攻击者，再嘲讽半径内的敌人。
	 * <p>
	 * 选谁用 {@link GolemTargetUtil#canTarget}（两道闸门：敌我 + 傀儡自身的配置卡过滤），
	 * 不能用裸 {@code golem.canAttack}：那样配置卡设为"不打某类目标"时会照样拉仇恨。
	 */
	private static void taunt(AbstractGolemEntity<?, ?> golem, Entity attacker) {
		if (attacker instanceof LivingEntity le && GolemTargetUtil.canTarget(golem, le)) {
			tauntMob(golem, le);
		}
		var aabb = golem.getBoundingBox().inflate(TAUNT_RADIUS);
		for (var e : golem.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
			if (!(e instanceof Enemy) || e instanceof Creeper) continue;
			if (e == attacker) continue;
			if (!GolemTargetUtil.canTarget(golem, e)) continue;
			tauntMob(golem, e);
		}
		golem.playSound(SoundEvents.SCULK_CLICKING, 1.0F, 1.0F);
	}

	private static void tauntMob(AbstractGolemEntity<?, ?> golem, LivingEntity target) {
		if (!(target instanceof Mob mob)) return;
		// 对方也得真的敌视傀儡，否则 setTarget 毫无意义
		if (!mob.canAttack(golem)) return;
		// 高亮嘲讽对象（同钟升级）
		EffectUtil.addEffect(mob, new MobEffectInstance(MobEffects.GLOWING, 200), EffectUtil.AddReason.NONE, golem);
		if (!(mob.getTarget() instanceof AbstractGolemEntity<?, ?>)) {
			mob.setTarget(golem);
		}
	}

	@Override
	public int getRemainingCooldown(AbstractGolemEntity<?, ?> golem) {
		long time = golem.level().getGameTime();
		return (int) Math.max(0, golem.getPersistentData().getLong(KEY_COOLDOWN) - time);
	}

	@Override
	public String getCooldownNameKey() {
		return getDescriptionId();
	}

	@Override
	public List<MutableComponent> getDetail(int v) {
		var pct = Component.literal("" + Math.round(OWNER_HP_RATIO * 100) + "%").withStyle(ChatFormatting.AQUA);
		var radius = Component.literal("" + (int) TAUNT_RADIUS).withStyle(ChatFormatting.AQUA);
		return List.of(Component.translatable(getDescriptionId() + ".desc", pct, radius).withStyle(ChatFormatting.GREEN));
	}

}
