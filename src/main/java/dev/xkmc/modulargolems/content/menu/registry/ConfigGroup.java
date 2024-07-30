package dev.xkmc.modulargolems.content.menu.registry;

import dev.xkmc.l2tabs.tabs.core.TabGroupData;
import dev.xkmc.modulargolems.content.capability.GolemConfigEditor;

public class ConfigGroup extends TabGroupData<ConfigGroup> {

	public final GolemConfigEditor editor;

	public ConfigGroup(GolemConfigEditor editor) {
		super(GolemTabRegistry.CONFIG);
		this.editor = editor;
	}

}
