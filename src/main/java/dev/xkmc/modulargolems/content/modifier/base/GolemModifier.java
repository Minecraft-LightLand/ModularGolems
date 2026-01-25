package dev.xkmc.modulargolems.content.modifier.base;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.modulargolems.content.config.GolemPartConfig;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.item.upgrade.IUpgradeItem;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GolemModifier extends NamedEntry<GolemModifier> {

	public static final int MAX_LEVEL = 5;

	public final StatFilterType type;
	public final int maxLevel;

	// 构造函数
	public GolemModifier(StatFilterType type, int maxLevel) {
		super(GolemTypes.MODIFIERS);
		this.type = type;
		this.maxLevel = maxLevel;
	}

	// 获取工具提示信息
	public Component getTooltip(int v) {
		MutableComponent ans = getDesc();
		if (maxLevel > 1)
			ans = ans.append(" ").append(Component.translatable("potion.potency." + (v - 1)));
		return ans.withStyle(ChatFormatting.LIGHT_PURPLE);
	}

	// 似乎是shift获取细节的提示内容
	public List<MutableComponent> getDetail(int v) {
		return List.of(Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.GREEN));
	}

	public void onGolemSpawn(AbstractGolemEntity<?, ?> entity, int level) {

	}

	/**
	 * fires when this golem attacks others
	 */
	public void onAttackTarget(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {

	}

	/**
	 * fires when this golem attacks others
	 */
	public void onHurtTarget(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {

	}

	/**
	 * fires when this golem is attacked. Damage cancellation phase
	 */
	public void onAttacked(AbstractGolemEntity<?, ?> entity, LivingAttackEvent event, int level) {

	}

	/**
	 * fires when this golem is attacked. Damage calculation phase
	 */
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {

	}

	/**
	 * fires when this golem is attacked. Damage taking phase
	 */
	public void onDamaged(AbstractGolemEntity<?, ?> entity, LivingDamageEvent event, int level) {

	}

	/**
	 * fires when this golem is attacked. Damage taking phase
	 */
	public void onDamaged(AttackCache cache, AbstractGolemEntity<?, ?> entity, int level) {

	}

	/**
	 * modify healing
	 */
	public double onHealTick(double heal, AbstractGolemEntity<?, ?> entity, int level) {
		return onInventoryHealTick(heal, new HealingContext(entity.getHealth(), entity.getMaxHealth(), entity), level);
	}

	/**
	 * modify healing
	 */
	public double onInventoryHealTick(double heal, HealingContext ctx, int level) {
		return heal;
	}

	/**
	 * modify damage
	 */
	public void modifyDamage(AttackCache cache, AbstractGolemEntity<?, ?> entity, int level) {
	}

	/**
	 * provide more slots
	 */
	public int addSlot(List<IUpgradeItem> upgrades, int lv) {
		return 0;
	}

	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {
	}

	public void onRegisterFlag(Consumer<GolemFlags> addFlag) {

	}

	public void onRegisterGoals(AbstractGolemEntity<?, ?> entity, int lv, BiConsumer<Integer, Goal> addGoal) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onClientTick(AbstractGolemEntity<?, ?> entity, int level) {
	}

	public boolean canExistOn(GolemPart<?, ?> part) {
		// 通过 GolemPartConfig 获取配置，并判断该零件是否允许当前修饰符类型
		return GolemPartConfig.get().getFilter(part).getOrDefault(type, 0d) > 0;
	}

	public void onSetTarget(AbstractGolemEntity<?, ?> golem, Mob mob, int level) {
	}

	public boolean fitsOn(GolemType<?, ?> type) {
		return true;
	}

	public void modifySource(AbstractGolemEntity<?, ?> golem, CreateSourceEvent event, int value) {
	}

	public void handleEvent(AbstractGolemEntity<?, ?> golem, int value, byte event) {
	}

	public InteractionResult interact(Player player, AbstractGolemEntity<?, ?> golem, InteractionHand hand, int value) {
		// 返回交互结果PASS
		return InteractionResult.PASS;
	}

	public void onKillTarget(AbstractGolemEntity<?, ?> golem, LivingEntity entity, LivingDeathEvent event, int level) {
	}

	public void finalizeHurtTarget(AttackCache cache, AbstractGolemEntity<?, ?> golem, int value) {
	}

	public float onHealPre(float heal, AbstractGolemEntity<?, ?> golem, int value) {
		return heal;
	}

	public void onHealPost(float heal, AbstractGolemEntity<?, ?> golem, int value) {
	}

	public record HealingContext(float health, float maxHealth, Entity owner) {

	}

}
