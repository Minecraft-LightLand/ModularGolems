package dev.xkmc.modulargolems.content.entity.humanoid;

import com.tterrag.registrate.util.entry.EntityEntry;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.GolemMenuControl;
import dev.xkmc.modulargolems.content.core.ModelProvider;
import dev.xkmc.modulargolems.content.core.GolemOverlayControl;
import dev.xkmc.modulargolems.content.menu.equipment.EquipmentsMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

public class HumanoidGolemType extends GolemType<HumanoidGolemEntity, HumanoidGolemPartType> {

	public HumanoidGolemType(EntityEntry<HumanoidGolemEntity> type, Supplier<ModelProvider<HumanoidGolemEntity, HumanoidGolemPartType>> model) {
		super(type, HumanoidGolemPartType::values, HumanoidGolemPartType.BODY, model);
	}

	@Override
	public GolemMenuControl<HumanoidGolemEntity> menuControl(EquipmentsMenu menu, HumanoidGolemEntity golem) {
		return new HumanoidGolemMenuControl(menu, golem);
	}

	@Override
	public Supplier<Supplier<GolemOverlayControl<HumanoidGolemEntity>>> overlayControl(HumanoidGolemEntity golem) {
		return () -> () -> new HumanoidGolemOverlayControl(golem);
	}

	public ItemStack getMenuIcon(HumanoidGolemEntity golem) {
		return Items.DIAMOND_CHESTPLATE.getDefaultInstance();
	}

}
