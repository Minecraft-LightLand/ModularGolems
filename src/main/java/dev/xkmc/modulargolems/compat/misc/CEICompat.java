package dev.xkmc.modulargolems.compat.misc;

import net.minecraftforge.common.MinecraftForge;

public class CEICompat {

	public static void register() {
		MinecraftForge.EVENT_BUS.register(CEICompat.class);
	}

	/*
	@SubscribeEvent
	public static void onHandleExp(GolemHandleExpEvent event) {
		if (event.getOrb().isRemoved()) return;
		int val = event.getOrb().getValue();
		if (val <= 0) return;
		BlockPos pos = event.getEntity().blockPosition();
		List<BlockPos> list = List.of(pos, pos.above());
		for (BlockPos i : list) {
			if (event.getEntity().level().getBlockEntity(i) instanceof DisenchanterBlockEntity be) {
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
	}TODO*/

}
