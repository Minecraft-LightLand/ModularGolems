package dev.xkmc.modulargolems.compat.materials.l2complements.modifiers;

import dev.xkmc.l2library.util.math.MathHelper;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.init.data.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;
import java.util.UUID;

public class ConduitModifier extends GolemModifier {

	private static final String STR_ATK = "l2complements:conduit_attack";
	private static final String STR_SPEED = "l2complements:conduit_speed";
	private static final String STR_ARMOR = "l2complements:conduit_armor";
	private static final String STR_TOUGH = "l2complements:conduit_toughness";

	private static final UUID ID_ATK = MathHelper.getUUIDFromString(STR_ATK);
	private static final UUID ID_SPEED = MathHelper.getUUIDFromString(STR_SPEED);
	private static final UUID ID_ARMOR = MathHelper.getUUIDFromString(STR_ARMOR);
	private static final UUID ID_TOUGH = MathHelper.getUUIDFromString(STR_TOUGH);

	public ConduitModifier() {
		super(StatFilterType.MASS, 4);
	}

	@Override
	public void onHurt(AbstractGolemEntity<?, ?> entity, LivingHurtEvent event, int level) {
		if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) ||
				event.getSource().is(DamageTypeTags.BYPASSES_EFFECTS) ||
				!entity.isInWaterRainOrBubble())
			return;
		event.setAmount((float) (event.getAmount() * Math.pow(1 - ModConfig.COMMON.conduitBoostReduction.get(), level)));
	}

	@Override
	public List<MutableComponent> getDetail(int level) {
		int red = (int) Math.round(100 * Math.pow(1 - ModConfig.COMMON.conduitBoostReduction.get(), level));
		int atk = (int) Math.round(ModConfig.COMMON.conduitBoostAttack.get() * level * 100);
		int spe = (int) Math.round(ModConfig.COMMON.conduitBoostSpeed.get() * level * 100);
		int armor = ModConfig.COMMON.conduitBoostArmor.get() * level;
		int tough = ModConfig.COMMON.conduitBoostToughness.get() * level;
		int damage = ModConfig.COMMON.conduitDamage.get() * level;
		int freq = ModConfig.COMMON.conduitCooldown.get() / 20;
		return List.of(Component.translatable(getDescriptionId() + ".desc", red, freq, damage).withStyle(ChatFormatting.GREEN),
				Component.translatable(Attributes.ATTACK_DAMAGE.getDescriptionId()).append(": +" + atk + "%").withStyle(ChatFormatting.BLUE),
				Component.translatable(Attributes.MOVEMENT_SPEED.getDescriptionId()).append(": +" + spe + "%").withStyle(ChatFormatting.BLUE),
				Component.translatable(Attributes.ARMOR.getDescriptionId()).append(": +" + armor).withStyle(ChatFormatting.BLUE),
				Component.translatable(Attributes.ARMOR_TOUGHNESS.getDescriptionId()).append(": +" + tough).withStyle(ChatFormatting.BLUE));
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

		double atk = ModConfig.COMMON.conduitBoostAttack.get() * level;
		double spe = ModConfig.COMMON.conduitBoostSpeed.get() * level;
		int armor = ModConfig.COMMON.conduitBoostArmor.get() * level;
		double tough = ModConfig.COMMON.conduitBoostToughness.get() * level;

		if (gatk != null && gatk.getModifier(ID_ATK) == null) {
			gatk.addTransientModifier(new AttributeModifier(ID_ATK, STR_ATK, atk, AttributeModifier.Operation.MULTIPLY_BASE));
		}
		if (gspe != null && gspe.getModifier(ID_SPEED) == null) {
			gspe.addTransientModifier(new AttributeModifier(ID_SPEED, STR_SPEED, spe, AttributeModifier.Operation.MULTIPLY_BASE));
		}
		if (garm != null && garm.getModifier(ID_ARMOR) == null) {
			garm.addTransientModifier(new AttributeModifier(ID_ARMOR, STR_ARMOR, armor, AttributeModifier.Operation.ADDITION));
		}
		if (gtgh != null && gtgh.getModifier(ID_TOUGH) == null) {
			gtgh.addTransientModifier(new AttributeModifier(ID_TOUGH, STR_TOUGH, tough, AttributeModifier.Operation.ADDITION));
		}

		LivingEntity target = golem.getTarget();
		if (level > 0 && target != null && target.hurtTime == 0 &&
				target.isInWaterRainOrBubble() &&
				golem.tickCount % ModConfig.COMMON.conduitCooldown.get() == 0) {
			int damage = ModConfig.COMMON.conduitDamage.get() * level;
			Level pLevel = golem.level();
			pLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
					SoundEvents.CONDUIT_ATTACK_TARGET, SoundSource.NEUTRAL,
					1.0F, 1.0F);
			golem.getTarget().hurt(pLevel.damageSources().indirectMagic(golem, golem), damage);
		}
	}

}
