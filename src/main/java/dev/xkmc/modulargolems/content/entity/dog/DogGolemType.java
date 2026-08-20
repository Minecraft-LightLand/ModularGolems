package dev.xkmc.modulargolems.content.entity.dog;

import com.tterrag.registrate.util.entry.EntityEntry;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.GolemMenuControl;
import dev.xkmc.modulargolems.content.core.ModelProvider;
import dev.xkmc.modulargolems.content.core.GolemOverlayControl;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import dev.xkmc.modulargolems.init.data.MGConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

public class DogGolemType extends GolemType<DogGolemEntity, DogGolemPartType> {

	public DogGolemType(EntityEntry<DogGolemEntity> type, Supplier<ModelProvider<DogGolemEntity, DogGolemPartType>> model) {
		super(type, DogGolemPartType::values, DogGolemPartType.BODY, model);
	}

	@Override
	public GolemMenuControl<DogGolemEntity> menuControl(EquipmentsMenu menu, DogGolemEntity golem) {
		return new DogGolemMenuControl(menu, golem);
	}

	@Override
	public Supplier<Supplier<GolemOverlayControl<DogGolemEntity>>> overlayControl(DogGolemEntity golem) {
		return () -> () -> new DogGolemOverlayControl(golem);
	}

	public ItemStack getMenuIcon(DogGolemEntity golem) {
		return Items.WOLF_ARMOR.getDefaultInstance();
	}

	@Override
	public int getUpgradeSlots() {
		return MGConfig.COMMON.dogGolemSlot.get();
	}

}
