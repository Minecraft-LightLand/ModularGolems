package dev.xkmc.modulargolems.compat.jade;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum GolemPlayerSkinProvider implements IEntityComponentProvider {

	INSTANCE;

	private static final ResourceLocation UID = ModularGolems.loc("player_skin");

	@Override
	public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
		if (accessor.getEntity() instanceof HumanoidGolemEntity golem) {
			String skin = golem.getPlayerSkin();
			if (!skin.isEmpty()) {
				if (skin.contains("/") || skin.contains("\\") || skin.contains(":")) {
					var id = ResourceLocation.tryParse(skin);
					if (id != null && BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
						var type = BuiltInRegistries.ENTITY_TYPE.get(id);
						tooltip.add(Component.translatable("top." + ModularGolems.MODID + ".entity_skin", type.getDescription()));
					} else {
						String name = skin.replace('\\', '/');
						name = name.substring(name.lastIndexOf('/') + 1);
						if (name.contains(".")) {
							name = name.substring(0, name.lastIndexOf('.'));
						}
						tooltip.add(Component.translatable("top." + ModularGolems.MODID + ".file_skin", name));
					}
				} else {
					tooltip.add(Component.translatable("top." + ModularGolems.MODID + ".player_skin", skin));
				}
			}
		}
	}

	@Override
	public ResourceLocation getUid() {
		return UID;
	}

}
