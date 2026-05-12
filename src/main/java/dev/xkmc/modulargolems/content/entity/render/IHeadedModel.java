package dev.xkmc.modulargolems.content.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HeadedModel;

public interface IHeadedModel extends HeadedModel {

	void translateToHead(PoseStack pose);

}
