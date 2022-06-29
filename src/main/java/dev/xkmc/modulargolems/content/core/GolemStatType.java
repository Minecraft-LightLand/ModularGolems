package dev.xkmc.modulargolems.content.core;

import dev.xkmc.l2library.base.NamedEntry;
import dev.xkmc.modulargolems.init.registrate.GolemTypeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.function.Supplier;

import static net.minecraft.world.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

public class GolemStatType extends NamedEntry<GolemStatType> {

	public enum Kind {
		BASE, ADD, PERCENT;
	}

	private final Supplier<Attribute> attribute;
	private final Kind kind;

	public GolemStatType(Supplier<Attribute> attribute, Kind kind) {
		super(GolemTypeRegistry.STAT_TYPES);
		this.attribute = attribute;
		this.kind = kind;
	}

	public Component getAdderTooltip(double val) {
		return MutableComponent.create(new TranslatableContents(
				"attribute.modifier.plus." + (kind == Kind.PERCENT ? 1 : 0),
				ATTRIBUTE_MODIFIER_FORMAT.format(kind == Kind.PERCENT ? val * 100 : val),
				MutableComponent.create(new TranslatableContents(attribute.get().getDescriptionId())))).withStyle(ChatFormatting.BLUE);
	}

	public Component getTotalTooltip(double val) {
		String key = "attribute.modifier." + (kind == Kind.BASE ? "equals." : "plus.") + (kind == Kind.PERCENT ? 1 : 0);
		return MutableComponent.create(new TranslatableContents(key,
				ATTRIBUTE_MODIFIER_FORMAT.format(kind == Kind.PERCENT ? val * 100 : val),
				MutableComponent.create(new TranslatableContents(attribute.get().getDescriptionId())))).withStyle(ChatFormatting.BLUE);
	}


	public void applyToEntity(LivingEntity e, double v) {
		AttributeInstance ins = e.getAttribute(attribute.get());
		if (ins == null) return;
		switch (kind) {
			case BASE -> ins.setBaseValue(v);
			case ADD -> ins.setBaseValue(ins.getValue() + v);
			case PERCENT -> ins.setBaseValue(ins.getValue() * (1 + v));
		}
	}

}
