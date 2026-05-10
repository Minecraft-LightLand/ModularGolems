package dev.xkmc.modulargolems.compat.materials.create.automation;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.conditions.ICondition;

public class ConditionalSARecipeBuilder extends SequencedAssemblyRecipeBuilder {

	public ConditionalSARecipeBuilder(Identifier id) {
		super(id);
	}

	public ConditionalSARecipeBuilder withCondition(ICondition condition) {
		this.recipeConditions.add(condition);
		return this;
	}

}
