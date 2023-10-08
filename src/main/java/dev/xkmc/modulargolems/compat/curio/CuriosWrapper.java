package dev.xkmc.modulargolems.compat.curio;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.Slot;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.ArrayList;
import java.util.Optional;

public class CuriosWrapper {

	private final ArrayList<CuriosSlotWrapper> list = new ArrayList<>();

	public final int total, page;
	public final AbstractGolemEntity<?, ?> golem;

	public CuriosWrapper(AbstractGolemEntity<?, ?> player, int page) {
		this.golem = player;
		int max = 6;//TODO
		Optional<ICuriosItemHandler> opt = player.getCapability(CuriosCapability.INVENTORY).resolve();
		this.page = page;
		if (opt.isEmpty()) {
			total = 0;
			return;
		}
		var cap = opt.get();
		int offset = page * max * 9;
		int count = 0;
		for (var ent : cap.getCurios().entrySet()) {
			var stack = ent.getValue();
			for (int i = 0; i < stack.getSlots(); i++) {
				count++;
				if (offset > 0) {
					offset--;
				} else {
					if (list.size() < max * 9) {
						list.add(new CuriosSlotWrapper(player, stack, i, ent.getKey()));
					}
				}
			}
		}
		this.total = (count - 1) / (max * 9) + 1;
	}

	public int getSize() {
		return list.size();
	}

	public CuriosSlotWrapper get(int i) {
		return list.get(i);
	}

	public record CuriosSlotWrapper(LivingEntity player, ICurioStacksHandler cap, int index, String identifier) {

		public Slot toSlot(int x, int y) {
			return new CurioSlot(player, cap.getStacks(), index, identifier, x, y, cap.getRenders(), false);
		}

	}

}
