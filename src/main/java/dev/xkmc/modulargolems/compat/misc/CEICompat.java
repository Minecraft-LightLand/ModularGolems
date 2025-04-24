package dev.xkmc.modulargolems.compat.misc;

import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import dev.xkmc.modulargolems.events.event.GolemHandleExpEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import plus.dragons.createenchantmentindustry.common.kinetics.grindstone.GrindstoneDrainBlockEntity;
import plus.dragons.createenchantmentindustry.common.registry.CEIFluids;

import java.util.List;

public class CEICompat {

	public static void register() {
		NeoForge.EVENT_BUS.register(CEICompat.class);
	}

	@SubscribeEvent
	public static void onHandleExp(GolemHandleExpEvent event) {
		if (event.getOrb().isRemoved()) return;
		int val = event.getOrb().getValue();
		if (val <= 0) return;
		BlockPos pos = event.getEntity().blockPosition();
		List<BlockPos> list = List.of(pos, pos.above());
		var level = event.getEntity().level();
		for (BlockPos i : list) {
			if (level.getBlockEntity(i) instanceof GrindstoneDrainBlockEntity) {
				FluidStack fluidStack = new FluidStack(CEIFluids.EXPERIENCE.get().getSource(), val);
				for (var dir : Direction.values()) {
					var cap = level.getCapability(Capabilities.FluidHandler.BLOCK, i, dir);
					if (cap != null) {
						if (cap instanceof SmartFluidTankBehaviour.InternalFluidHandler tank) {
							int fill = tank.forceFill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
							val -= fill;
						}
					}
				}
			}
		}
		event.getOrb().value = val;
	}

}
