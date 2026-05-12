package dev.xkmc.modulargolems.content.client.override;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.render.AbstractGolemRenderState;
import dev.xkmc.modulargolems.content.entity.render.AbstractGolemRenderer;
import dev.xkmc.modulargolems.content.entity.render.IGolemModel;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ModelOverride {

	public static ModelOverride texturePredicate(Function<AbstractGolemRenderState<?, ?, ?>, String> modifier) {
		return new ModelOverride() {

			@Override
			public Identifier getTexture(AbstractGolemRenderState<?, ?, ?> golem, Identifier id) {
				return super.getTexture(golem, id).withSuffix(modifier.apply(golem));
			}

		};
	}

	public ModelOverride() {
	}

	public Identifier getTexture(AbstractGolemRenderState<?, ?, ?> golem, Identifier id) {
		return id;
	}

	public synchronized <
			E extends AbstractGolemEntity<E, P>,
			S extends LivingEntityRenderState & AbstractGolemRenderState<E, S, P>,
			M extends EntityModel<S> & IGolemModel<E, S, P, M>,
			P extends IGolemPart<P>
			> void renderAll(
			AbstractGolemRenderer<E, S, P, M> renderer, S entity, P part, PoseStack pose, SubmitNodeCollector buffer, Identifier mat,
			int light, boolean visible, boolean ghost, boolean glowing
	) {
		if (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
			var camera = Minecraft.getInstance().getCameraEntity();
			if (camera != null && entity.boundingBoxWidth >= 2) {
				var veh = camera.getVehicle();
				if (veh != null && veh.getId() == entity.getId())
					ghost = true;
			}
		}
		var model = renderer.getModel();
		Identifier tex = getTexture(entity, mat);
		RenderType rt = getRenderType(model, model.getTextureLocationInternal(tex), visible, ghost, glowing);
		if (rt != null) {
			renderer.renderPartModel(entity, part, pose, buffer, rt, light, ghost);
		}
		var etex = model.getTextureLocationInternal(tex.withSuffix("_emissive"));
		if (ModelOverrides.isValid(etex)) {
			rt = getRenderType(renderer.getModel(), etex, visible, ghost, glowing);
			if (rt != null) {
				renderer.renderPartModel(entity, part, pose, buffer, rt, LightCoordsUtil.FULL_BRIGHT, ghost);
			}
		}
	}

	@Nullable
	protected <M extends EntityModel<?> & IGolemModel<?, ?, ?, M>> RenderType getRenderType(
			M model, Identifier tex, boolean visible, boolean ghost, boolean glowing
	) {
		if (ghost) return RenderTypes.entityTranslucentCullItemTarget(tex);
		if (visible) return model.renderType(tex);
		return glowing ? RenderTypes.outline(tex) : null;
	}

}
