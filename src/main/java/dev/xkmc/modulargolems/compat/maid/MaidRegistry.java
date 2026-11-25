package dev.xkmc.modulargolems.compat.maid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.events.event.GolemRidingOffsetEvent;
import dev.xkmc.modulargolems.events.event.GolemToOwnerEvent;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.MGTagGen;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MaidRegistry {

	public static final Val<MemoryModuleType<List<UUID>>> GOLEMS = new Val.Registrate<>(ModularGolems.REGISTRATE.simple(
			"golem_ids", Registries.MEMORY_MODULE_TYPE, () -> new MemoryModuleType<>(Optional.of(UUIDUtil.CODEC.listOf()))));

	@SubscribeEvent
	public static void onGolemReturn(GolemToOwnerEvent event) {
		if (event.getOwner() instanceof EntityMaid maid) {
			if (GolemSummonUtils.returnToInv(maid, event.getStack())) {
				event.setCanceled(true);
			}
		}
	}

	/**
	 * 女仆模型比普通皮肤矮，骑乘时座位高度要单独修正。
	 * <p>
	 * 这个偏移原本写在 {@code HumanoidGolemEntity#getMyRidingOffset} 里，改成发
	 * {@link GolemRidingOffsetEvent} 后一直没有监听者，等于失效；这里把监听补回来。
	 */
	@SubscribeEvent
	public static void maidOffset(GolemRidingOffsetEvent event) {
		if (!(event.getGolem() instanceof HumanoidGolemEntity golem)) return;
		if (golem.getMaidModelId().isEmpty()) return;
		event.setOffsetY(-golem.getBbHeight() * 0.005 + 0.05);
	}

	@SubscribeEvent
	public static void maidTick(EntityTickEvent.Post event) {
		if (event.getEntity() instanceof EntityMaid maid) {
			var inv = maid.getAvailableInv(false);
			for (int i = 0; i < inv.getSlots(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if (!stack.is(MGTagGen.GOLEM_HOLDERS)) continue;
				stack.inventoryTick(maid.level(), maid, i, false);
				inv.setStackInSlot(i, stack);
			}
		}
	}

	public static void register() {
		NeoForge.EVENT_BUS.register(MaidRegistry.class);
	}

}
