package dev.xkmc.modulargolems.content.modifier;

import dev.xkmc.l2library.util.math.MathHelper;
import dev.xkmc.modulargolems.content.core.GolemModifier;
import dev.xkmc.modulargolems.content.core.StatFilterType;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.util.Lazy;

import java.util.UUID;
import java.util.function.Supplier;

public class AttributeGolemModifier extends GolemModifier {

	public final Supplier<Attribute> attributes;
	public final AttributeModifier.Operation operation;
	public final boolean usePercent;
	public final double value;

	private final Lazy<UUID> id = Lazy.of(() -> MathHelper.getUUIDFromString(getID()));

	public AttributeGolemModifier(StatFilterType type, Supplier<Attribute> attributes, AttributeModifier.Operation operation,
								  double value, boolean usePercent) {
		super(type, MAX_LEVEL);
		this.attributes = attributes;
		this.operation = operation;
		this.usePercent = usePercent;
		this.value = value;
	}

	@Override
	public void onGolemSpawn(AbstractGolemEntity<?, ?> entity, int level) {
		AttributeInstance instance = entity.getAttribute(attributes.get());
		if (instance == null) return;
		instance.addPermanentModifier(new AttributeModifier(id.get(), getID(), value, operation));
	}
}
