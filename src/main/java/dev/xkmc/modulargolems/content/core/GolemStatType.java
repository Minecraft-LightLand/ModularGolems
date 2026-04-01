package dev.xkmc.modulargolems.content.core;

import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

import static net.minecraft.world.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

public class GolemStatType extends NamedEntry<GolemStatType> implements Comparable<GolemStatType> {

	public enum Kind {
		BASE, ADD, PERCENT
	}

	private final Supplier<Attribute> attribute;

	public final Kind kind;
	public final StatFilterType type;
	private final boolean showAsPercent;

	public GolemStatType(Supplier<Attribute> attribute, Kind kind, StatFilterType type) {
		this(attribute, kind, type, false);
	}

	public GolemStatType(Supplier<Attribute> attribute, Kind kind, StatFilterType type, boolean showAsPercent) {
		super(GolemTypes.STAT_TYPES);
		this.attribute = attribute;
		this.kind = kind;
		this.type = type;
		this.showAsPercent = showAsPercent;
	}

	public boolean percentDisplay() {
		return kind == Kind.PERCENT || showAsPercent;
	}

	public Attribute getAttribute() {
		return attribute.get();
	}

	public MutableComponent getAdderTooltip(double val) {
		if (percentDisplay()) {
			val = val * 100;
		}
		String key = "attribute.modifier." + (val < 0 ? "take." : "plus.") + (percentDisplay() ? 1 : 0);
		return Component.translatable(key,
				ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(val)),
				Component.translatable(attribute.get().getDescriptionId())).withStyle(ChatFormatting.BLUE);
	}

	public MutableComponent getTotalTooltip(double val) {
		if (percentDisplay()) {
			val = val * 100;
		}
		String key = "attribute.modifier." + (val < 0 ? "take." : kind == Kind.BASE ? "equals." : "plus.") + (percentDisplay() ? 1 : 0);
		return Component.translatable(key,
				ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(val)),
				Component.translatable(attribute.get().getDescriptionId())).withStyle(ChatFormatting.BLUE);
	}

	public MutableComponent getDiffTooltip(double val) {
		if (percentDisplay()) {
			val = val * 100;
		}
		String key = "attribute.modifier." + (val < 0 ? "take." : "plus.") + (percentDisplay() ? 1 : 0);
		return Component.translatable(key,
				ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(val)),
				Component.translatable(attribute.get().getDescriptionId())).withStyle(val > 0 ? ChatFormatting.BLUE : ChatFormatting.RED);
	}

	/**
	 * collected value, will only call once per stat
	 */
	public void applyToEntity(LivingEntity e, double v) {
		AttributeInstance ins = e.getAttribute(attribute.get());
		if (ins == null) return;
		switch (kind) {
			case BASE -> ins.setBaseValue(v);
			case ADD -> ins.setBaseValue(ins.getValue() + v);
			case PERCENT -> ins.setBaseValue(ins.getValue() * (1 + v));
		}
	}

	private Set<GolemStatType> conflicting;

	public Set<GolemStatType> hasConflict() {
		if (kind == Kind.BASE) return Set.of();
		if (conflicting == null) {
			conflicting = new LinkedHashSet<>();
			for (var e : GolemTypes.STAT_TYPES.get()) {
				if (e == this) continue;
				if (e.attribute.get() == attribute.get()) {
					if (e.kind == Kind.BASE) {
						conflicting.clear();
						return Set.of();
					}
					if (e.kind != kind) {
						conflicting.add(e);
					}
				}
			}
		}
		return conflicting;
	}

	@Override
	public int compareTo(@NotNull GolemStatType other) {
		return getRegistryName().compareTo(other.getRegistryName());
	}

}
