package dev.xkmc.modulargolems.compat.jade;

import com.github.tartaricacid.touhoulittlemaid.client.resource.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.util.ParseI18n;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum GolemMaidModelProvider implements IEntityComponentProvider {

	INSTANCE;

	private static final ResourceLocation UID = ModularGolems.loc("maid_model");

	@Override
	public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
		if (accessor.getEntity() instanceof HumanoidGolemEntity golem) {
			String modelId = golem.getMaidModelId();
			if (!modelId.isEmpty()) {
				Component name = CustomPackLoader.MAID_MODELS.getInfo(modelId)
						.map(info -> ParseI18n.parse(info.getName()))
						.orElse(Component.literal(modelId));
				tooltip.add(Component.translatable("top." + ModularGolems.MODID + ".maid_model", name));
			}
		}
	}

	@Override
	public ResourceLocation getUid() {
		return UID;
	}

}
