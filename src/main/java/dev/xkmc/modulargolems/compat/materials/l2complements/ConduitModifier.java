package dev.xkmc.modulargolems.compat.materials.l2complements;

import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import java.util.List;

public class ConduitModifier extends GolemModifier {

	private static final String STR_ATK = "conduit_attack";
	private static final String STR_SPEED = "conduit_speed";
	private static final String STR_ARMOR = "conduit_armor";
	private static final String STR_TOUGH = "conduit_toughness";

	private static final ResourceLocation ID_ATK = ModularGolems.loc(STR_ATK);
	private static final ResourceLocation ID_SPEED = ModularGolems.loc(STR_SPEED);
	private static final ResourceLocation ID_ARMOR = ModularGolems.loc(STR_ARMOR);
	private static final ResourceLocation ID_TOUGH = ModularGolems.loc(STR_TOUGH);

	public ConduitModifier() {
		super(StatFilterType.MASS, 4);
	}

	@Override
	public void onDamaged(AbstractGolemEntity<?, ?> entity, DamageData.Defence event, int level) {
		if (event.getSource().is(DamageTypeTags.BYPASSES_EFFECTS) ||
				!entity.isInWaterRainOrBubble())
			return;
		event.addDealtModifier(DamageModifier.multTotal((float) Math.pow(1 - MGConfig.COMMON.conduitBoostReduction.get(), level), getRegistryName()));
	}

	@Override
	public List<MutableComponent> getDetail(int level) {
		int red = (int) Math.round(100 * Math.pow(1 - MGConfig.COMMON.conduitBoostReduction.get(), level));
		int atk = (int) Math.round(MGConfig.COMMON.conduitBoostAttack.get() * level * 100);
		int spe = (int) Math.round(MGConfig.COMMON.conduitBoostSpeed.get() * level * 100);
		int armor = MGConfig.COMMON.conduitBoostArmor.get() * level;
		int tough = MGConfig.COMMON.conduitBoostToughness.get() * level;
		int damage = MGConfig.COMMON.conduitDamage.get() * level;
		int freq = MGConfig.COMMON.conduitCooldown.get() / 20;
		return List.of(Component.translatable(getDescriptionId() + ".desc", red, freq, damage).withStyle(ChatFormatting.GREEN),
				Component.translatable(Attributes.ATTACK_DAMAGE.value().getDescriptionId()).append(": +" + atk + "%").withStyle(ChatFormatting.BLUE),
				Component.translatable(Attributes.MOVEMENT_SPEED.value().getDescriptionId()).append(": +" + spe + "%").withStyle(ChatFormatting.BLUE),
				Component.translatable(Attributes.ARMOR.value().getDescriptionId()).append(": +" + armor).withStyle(ChatFormatting.BLUE),
				Component.translatable(Attributes.ARMOR_TOUGHNESS.value().getDescriptionId()).append(": +" + tough).withStyle(ChatFormatting.BLUE));
	}

	@Override
	public void onAiStep(AbstractGolemEntity<?, ?> golem, int level) {

		var gatk = golem.getAttribute(Attributes.ATTACK_DAMAGE);
		var gspe = golem.getAttribute(Attributes.MOVEMENT_SPEED);
		var garm = golem.getAttribute(Attributes.ARMOR);
		var gtgh = golem.getAttribute(Attributes.ARMOR_TOUGHNESS);

		if (!golem.isInWaterRainOrBubble()) {
			if (gatk != null && gatk.getModifier(ID_ATK) != null) {
				gatk.removeModifier(ID_ATK);
			}
			if (gspe != null && gspe.getModifier(ID_SPEED) != null) {
				gspe.removeModifier(ID_SPEED);
			}
			if (garm != null && garm.getModifier(ID_ARMOR) != null) {
				garm.removeModifier(ID_ARMOR);
			}
			if (gtgh != null && gtgh.getModifier(ID_TOUGH) != null) {
				gtgh.removeModifier(ID_TOUGH);
			}

			return;
		}

		double atk = MGConfig.COMMON.conduitBoostAttack.get() * level;
		double spe = MGConfig.COMMON.conduitBoostSpeed.get() * level;
		int armor = MGConfig.COMMON.conduitBoostArmor.get() * level;
		double tough = MGConfig.COMMON.conduitBoostToughness.get() * level;

		if (gatk != null && gatk.getModifier(ID_ATK) == null) {
			gatk.addTransientModifier(new AttributeModifier(ID_ATK, atk, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
		}
		if (gspe != null && gspe.getModifier(ID_SPEED) == null) {
			gspe.addTransientModifier(new AttributeModifier(ID_SPEED, spe, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
		}
		if (garm != null && garm.getModifier(ID_ARMOR) == null) {
			garm.addTransientModifier(new AttributeModifier(ID_ARMOR, armor, AttributeModifier.Operation.ADD_VALUE));
		}
		if (gtgh != null && gtgh.getModifier(ID_TOUGH) == null) {
			gtgh.addTransientModifier(new AttributeModifier(ID_TOUGH, tough, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
		}

		LivingEntity target = golem.getTarget();
		if (level > 0 && target != null && target.hurtTime == 0 &&
				target.isInWaterRainOrBubble() &&
				golem.tickCount % MGConfig.COMMON.conduitCooldown.get() == 0) {
			int damage = MGConfig.COMMON.conduitDamage.get() * level;
			Level pLevel = golem.level();
			pLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
					SoundEvents.CONDUIT_ATTACK_TARGET, SoundSource.NEUTRAL,
					1.0F, 1.0F);
			golem.getTarget().hurt(pLevel.damageSources().indirectMagic(golem, golem), damage);
		}
	}

}
