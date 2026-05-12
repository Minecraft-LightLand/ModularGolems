package dev.xkmc.modulargolems.content.entity.common;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.content.client.override.ModelOverride;
import dev.xkmc.modulargolems.content.client.override.ModelOverrides;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.item.golem.GolemFacade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractGolemRenderer<
		E extends AbstractGolemEntity<E, P>,
		S extends LivingEntityRenderState & AbstractGolemRenderState<E, S, P>,
		P extends IGolemPart<P>,
		M extends EntityModel<S> & IGolemModel<E, S, P, M>
		> extends MobRenderer<E, S, M> {

	public static final List<Function<AbstractGolemRenderer<?, ?, ?, ?>, RenderLayer<? extends AbstractGolemRenderState<?, ?, ?>, ?>>> LIST = new ArrayList<>();

	private static final Identifier GOLEM_LOCATION = Identifier.withDefaultNamespace("textures/entity/iron_golem/iron_golem.png");

	private final Supplier<P[]> list;

	public AbstractGolemRenderer(EntityRendererProvider.Context ctx, M model, float f, Supplier<P[]> list) {
		super(ctx, model, f);
		this.list = list;
		addLayer(new GolemDefaultLayer<>(this));
		LIST.forEach(e -> this.addLayer(Wrappers.cast(e.apply(this))));
	}

	public Identifier getTextureLocation(S entity) {
		return GOLEM_LOCATION;
	}

	protected boolean delegated(S entity) {
		return false;
	}

	@Override
	public void render(S entity, float f1, float f2, PoseStack stack, MultiBufferSource source, int i) {
		if (model instanceof IHeadedModel headed) headed.getHead().visible = true;
		super.render(entity, f1, f2, stack, source, i);
	}

	@Nullable
	@Override
	protected RenderType getRenderType(S entity, boolean b1, boolean b2, boolean b3) {
		if (delegated(entity))
			return super.getRenderType(entity, b1, b2, b3);
		return null;
	}

	protected void renderAllParts(PoseStack pose, SubmitNodeCollector buffer, int light, S entity) {
		if (delegated(entity)) return;
		var player = Proxy.getClientPlayer();
		boolean visible = isBodyVisible(entity);
		boolean ghost = !visible && player != null && !entity.isInvisibleTo(player);
		boolean glowing = Minecraft.getInstance().shouldEntityAppearGlowing(entity);
		pose.pushPose();
		Identifier facade = null;
		if (ModList.get().isLoaded("curios")) {
			var opt = CurioCompatRegistry.getItem(entity, "golem_skin");
			if (opt.isPresent() && opt.get().getItem() instanceof GolemFacade)
				facade = GolemFacade.getMaterial(opt.get());
		}
		var materials = entity.getMaterials();
		for (P part : list.get()) {
			Identifier rl = facade;
			if (rl == null) {
				int index = part.ordinal();
				rl = materials.size() > index ? materials.get(index).id() : GolemMaterial.EMPTY;
			}

			ModelOverride override = ModelOverrides.getOverride(rl);
			override.renderAll(this, entity, part, pose, buffer, rl, light, pTick, visible, ghost, glowing);
		}
		pose.popPose();
	}

	public void renderPartModel(S entity, P part, PoseStack pose, VertexConsumer vc, int light, float pTick, boolean ghost) {
		int overlay = getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, pTick));
		this.model.renderToBufferInternal(part, pose, vc, light, overlay, ghost ? 0x26FFFFFF : -1);
	}

}