package dev.xkmc.modulargolems.compat.misc;

import com.simibubi.create.foundation.tileEntity.behaviour.fluid.SmartFluidTankBehaviour;
import dev.xkmc.modulargolems.events.event.GolemHandleExpEvent;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import plus.dragons.createenchantmentindustry.content.contraptions.enchanting.disenchanter.DisenchanterBlockEntity;
import plus.dragons.createenchantmentindustry.entry.CeiFluids;

import java.util.List;

public class CEICompat {

	public static void register() {
		MinecraftForge.EVENT_BUS.register(CEICompat.class);
	}

	@SubscribeEvent
	public static void onHandleExp(GolemHandleExpEvent event) {
		if (event.getOrb().isRemoved()) return;
		int val = event.getOrb().getValue();
		if (val <= 0) return;
		BlockPos pos = event.getEntity().blockPosition();
		List<BlockPos> list = List.of(pos, pos.above());
		for (BlockPos i : list) {
			if (event.getEntity().level.getBlockEntity(i) instanceof DisenchanterBlockEntity be) {
				FluidStack fluidStack = new FluidStack(CeiFluids.EXPERIENCE.get().getSource(), val);
				var lazyOpt = be.getCapability(ForgeCapabilities.FLUID_HANDLER);
				if (lazyOpt.resolve().isPresent()) {
					var cap = lazyOpt.resolve().get();
					if (cap instanceof SmartFluidTankBehaviour.InternalFluidHandler tank) {
						int fill = tank.forceFill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
						val -= fill;
					}

				}
			}
		}
		event.getOrb().value = val;
	}

}
