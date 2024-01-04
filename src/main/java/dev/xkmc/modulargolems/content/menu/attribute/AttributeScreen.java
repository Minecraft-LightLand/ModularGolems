package dev.xkmc.modulargolems.content.menu.attribute;

import dev.xkmc.l2tabs.tabs.contents.AttributeEntry;
import dev.xkmc.l2tabs.tabs.contents.BaseAttributeScreen;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Lazy;

import java.util.List;

public class AttributeScreen extends BaseAttributeScreen {

	public static final Lazy<List<AttributeEntry>> ATTRIBUTES = Lazy.of(() -> List.of(
			new AttributeEntry(Attributes.MAX_HEALTH, false, 0,0),
			new AttributeEntry(Attributes.ATTACK_DAMAGE, false, 1,0),
			new AttributeEntry(Attributes.ARMOR, false, 2,0),
			new AttributeEntry(Attributes.ARMOR_TOUGHNESS, false, 3,0),
			new AttributeEntry(Attributes.KNOCKBACK_RESISTANCE, false, 4,0),
			new AttributeEntry(Attributes.MOVEMENT_SPEED, false, 5,0),
			new AttributeEntry(ForgeMod.ENTITY_REACH.get(), false, 6,0),
			new AttributeEntry(GolemTypes.GOLEM_REGEN.get(), false, 7,0),
			new AttributeEntry(GolemTypes.GOLEM_SWEEP.get(), false, 8,0),
			new AttributeEntry(GolemTypes.GOLEM_SIZE.get(), false, 9,0),
			new AttributeEntry(GolemTypes.GOLEM_JUMP.get(), false, 10,0)
	));

	private final AbstractGolemEntity<?, ?> golem;

	public AttributeScreen(AbstractGolemEntity<?, ?> golem, Component title) {
		super(title);
		this.golem = golem;
	}

	public void init() {
		super.init();
		new TabManager<>(this, new EquipmentGroup(golem))
				.init(this::addRenderableWidget, GolemTabRegistry.ATTRIBUTE);
	}

	public void render(GuiGraphics g, int mx, int my, float ptick) {
		super.render(g, mx, my, ptick);
		render(g, mx, my, ptick, golem, ATTRIBUTES.get());

	}


}
