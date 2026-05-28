package dev.xkmc.modulargolems.compat.maid.jade;

import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum GolemPlayerSkinProvider implements IEntityComponentProvider {

	INSTANCE;

	private static final ResourceLocation UID = new ResourceLocation(ModularGolems.MODID, "player_skin");

	@Override
	public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
		if (accessor.getEntity() instanceof HumanoidGolemEntity golem) {
			String skin = golem.getPlayerSkin();
			if (!skin.isEmpty()) {
				if (skin.contains("/") || skin.contains("\\") || skin.contains(":")) {
					String name = skin.replace('\\', '/');
					name = name.substring(name.lastIndexOf('/') + 1);
					if (name.contains(".")) {
						name = name.substring(0, name.lastIndexOf('.'));
					}
					tooltip.add(Component.translatable("top." + ModularGolems.MODID + ".file_skin", name));
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
