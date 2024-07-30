package dev.xkmc.modulargolems.content.menu.attribute;

import dev.xkmc.l2tabs.tabs.contents.BaseAttributeScreen;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.menu.registry.EquipmentGroup;
import dev.xkmc.modulargolems.content.menu.registry.GolemTabRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class AttributeScreen extends BaseAttributeScreen {

	private final AbstractGolemEntity<?, ?> golem;

	public AttributeScreen(AbstractGolemEntity<?, ?> golem, Component title, int page) {
		super(title, page);
		this.golem = golem;
	}

	public void init() {
		super.init();
		new TabManager<>(this, new EquipmentGroup(golem))
				.init(this::addRenderableWidget, GolemTabRegistry.ATTRIBUTE.get());
	}

	@Override
	public LivingEntity getEntity() {
		return golem;
	}

	protected void click(int nextPage) {
		Minecraft.getInstance().setScreen(new AttributeScreen(golem, this.getTitle(), nextPage));
	}

}
