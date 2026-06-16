package dev.xkmc.modulargolems.content.entity.metalgolem;

import com.tterrag.registrate.util.entry.EntityEntry;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.MenuControl;
import dev.xkmc.modulargolems.content.core.ModelProvider;
import dev.xkmc.modulargolems.content.core.OverlayControl;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class MetalGolemType extends GolemType<MetalGolemEntity, MetalGolemPartType> {

	public MetalGolemType(EntityEntry<MetalGolemEntity> type, Supplier<ModelProvider<MetalGolemEntity, MetalGolemPartType>> model) {
		super(type, MetalGolemPartType::values, MetalGolemPartType.BODY, model);
	}

	@Override
	public MenuControl<MetalGolemEntity> menuControl(EquipmentsMenu menu, MetalGolemEntity golem) {
		return new MetalGolemMenuControl(menu, golem);
	}

	@Override
	public Supplier<Supplier<OverlayControl<MetalGolemEntity>>> overlayControl(MetalGolemEntity golem) {
		return () -> () -> new MetalGolemOverlayControl(golem);
	}

	public ItemStack getMenuIcon(MetalGolemEntity golem) {
		return GolemItems.WINDSPIRIT_CHESTPLATE.asStack();
	}

}
