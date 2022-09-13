package dev.xkmc.modulargolems.content.entity.dog;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.item.GolemPart;
import dev.xkmc.modulargolems.init.registrate.GolemItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum DogGolemPartType implements IGolemPart<DogGolemPartType> {
    BODY, LEGS;

    @Override
    public MutableComponent getDesc(MutableComponent desc) {
        return Component.translatable("golem_part.humanoid_golem." + name().toLowerCase(Locale.ROOT), desc).withStyle(ChatFormatting.GREEN);
    }

    @Override
    public GolemPart<?, DogGolemPartType> toItem() {
        return switch (this) {
            case BODY -> GolemItemRegistry.DOG_BODY.get();
            case LEGS -> GolemItemRegistry.DOG_LEGS.get();
        };
    }

    @Override
    public void setupItemRender(PoseStack stack, ItemTransforms.TransformType transform, @Nullable DogGolemPartType part) {
        switch (transform) {
            case GUI:
            case FIRST_PERSON_LEFT_HAND:
            case FIRST_PERSON_RIGHT_HAND:
                break;
            case THIRD_PERSON_LEFT_HAND:
            case THIRD_PERSON_RIGHT_HAND: {
                stack.translate(0.25, 0.4, 0.5);
                float size = 0.625f;
                stack.scale(size, size, size);
                break;
            }
            case GROUND: {
                stack.translate(0.25, 0, 0.5);
                float size = 0.625f;
                stack.scale(size, size, size);
                break;
            }
            case NONE:
            case HEAD:
            case FIXED: {
                stack.translate(0.5, 0.5, 0.5);
                float size = 0.5f;
                stack.scale(size, -size, size);
                stack.translate(0, -0.5, 0);
                return;
            }
        }
        stack.mulPose(Vector3f.ZP.rotationDegrees(135));
        stack.mulPose(Vector3f.YP.rotationDegrees(-155));
        if (part == null) {
            float size = 0.45f;
            stack.scale(size, size, size);
            stack.translate(0, -2, 0);
        } else if (part == BODY) {
            float size = 0.65f;
            stack.scale(size, size, size);
            stack.translate(0, -1.2, 0);
        } else if (part == LEGS) {
            float size = 0.8f;
            stack.scale(size, size, size);
            stack.translate(0, -2, 0);
        }
    }

}
