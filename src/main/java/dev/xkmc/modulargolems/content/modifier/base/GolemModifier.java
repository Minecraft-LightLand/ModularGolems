package dev.xkmc.modulargolems.content.modifier.base;

import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.modulargolems.content.config.GolemPartConfig;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.GolemFlags;
import dev.xkmc.modulargolems.content.item.UpgradeItem;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GolemModifier extends NamedEntry<GolemModifier> {

	public static final int MAX_LEVEL = 5;

	public final StatFilterType type;
	public final int maxLevel;

	public GolemModifier(StatFilterType type, int maxLevel) {
		super(GolemTypes.MODIFIERS);
		this.type = type;
		this.maxLevel = maxLevel;
	}

	public Component getTooltip(int v) {
		MutableComponent ans = getDesc();
		if (maxLevel > 1)
			ans = ans.append(" ").append(Component.translatable("potion.potency." + (v - 1)));
		return ans.withStyle(ChatFormatting.LIGHT_PURPLE);
	}

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
	 * modify healing
	 */
	public double onHealTick(double heal, AbstractGolemEntity<?, ?> entity, int level) {
		return heal;
	}

	/**
	 * modify damage
	 */
	public float modifyDamage(float damage, AbstractGolemEntity<?, ?> entity, int level) {
		return damage;
	}

	/**
	 * provide more slots
	 */
	public int addSlot(List<UpgradeItem> upgrades, int lv) {
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
		return GolemPartConfig.get().getFilter(part).getOrDefault(type, 0d) > 0;
	}

	public void onSetTarget(AbstractGolemEntity<?, ?> golem, Mob mob, int level) {
	}

}
