package dev.xkmc.modulargolems.compat.materials.tinker;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.compat.materials.tinker.automation.TinkerRecipeGen;
import dev.xkmc.modulargolems.compat.materials.tinker.behavior.TinkerBowBehavior;
import dev.xkmc.modulargolems.compat.materials.tinker.behavior.TinkerCrossbowBehavior;
import dev.xkmc.modulargolems.content.entity.humanoid.bow.BowBehaviorData;
import dev.xkmc.modulargolems.content.entity.humanoid.bow.BowBehaviorRegistry;
import dev.xkmc.modulargolems.content.entity.humanoid.crossbow.CrossbowBehaviorData;
import dev.xkmc.modulargolems.content.entity.humanoid.crossbow.CrossbowBehaviorRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableBowItem;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableCrossbowItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class TCDispatch extends ModDispatch {

	@Override
	protected void genLang(RegistrateLangProvider pvd) {

	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
		TinkerRecipeGen.genRecipe(pvd);
	}

	@Nullable
	@Override
	public ConfigDataProvider getDataGen(DataGenerator gen) {
		return new TinkerConfigGen(gen);
	}

	@Override
	public void commonSetup() {
		BowBehaviorRegistry.register(new ResourceLocation(TConstruct.MOD_ID, "bow"),
				stack -> stack.getItem() instanceof ModifiableBowItem && !ToolStack.from(stack).isBroken(),
				(golem, stack) -> new BowBehaviorData(
						(int) Math.ceil(20 / ConditionalStatModifierHook.getModifiedStat(ToolStack.from(stack), golem, ToolStats.DRAW_SPEED)),
						new TinkerBowBehavior()
				)
		);
		CrossbowBehaviorRegistry.register(new ResourceLocation(TConstruct.MOD_ID, "crossbow"),
				stack -> stack.getItem() instanceof ModifiableCrossbowItem && !ToolStack.from(stack).isBroken(),
				(golem, stack) -> new CrossbowBehaviorData(
						(int) Math.ceil(20 / ConditionalStatModifierHook.getModifiedStat(ToolStack.from(stack), golem, ToolStats.DRAW_SPEED)),
						new TinkerCrossbowBehavior()
				)
		);
	}

}
