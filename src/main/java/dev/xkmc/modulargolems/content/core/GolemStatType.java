package dev.xkmc.modulargolems.content.core;

import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.function.Supplier;

import static net.minecraft.world.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

public class GolemStatType extends NamedEntry<GolemStatType> {

	public enum Kind {
		BASE, ADD, PERCENT
	}

	private final Supplier<Attribute> attribute;

	public final Kind kind;
	public final StatFilterType type;

	public GolemStatType(Supplier<Attribute> attribute, Kind kind, StatFilterType type) {
		super(GolemTypeRegistry.STAT_TYPES);
		this.attribute = attribute;
		this.kind = kind;
		this.type = type;
	}

	public Attribute getAttribute() {
		return attribute.get();
	}

	public MutableComponent getAdderTooltip(double val) {
		if (kind == Kind.PERCENT) val = val * 100;
		String key = "attribute.modifier." + (val < 0 ? "take." : "plus.") + (kind == Kind.PERCENT ? 1 : 0);
		return Component.translatable(key,
				ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(val)),
				Component.translatable(attribute.get().getDescriptionId())).withStyle(ChatFormatting.BLUE);
	}

	public MutableComponent getTotalTooltip(double val) {
		if (kind == Kind.PERCENT) val = (val - 1) * 100;
		String key = "attribute.modifier." + (val < 0 ? "take." : kind == Kind.BASE ? "equals." : "plus.") + (kind == Kind.PERCENT ? 1 : 0);
		return Component.translatable(key,
				ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(val)),
				Component.translatable(attribute.get().getDescriptionId())).withStyle(ChatFormatting.BLUE);
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
			case PERCENT -> ins.setBaseValue(ins.getValue() * v);
		}
	}

}
