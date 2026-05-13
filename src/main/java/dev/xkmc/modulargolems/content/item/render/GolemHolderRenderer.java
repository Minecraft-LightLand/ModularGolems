package dev.xkmc.modulargolems.content.item.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.render.AbstractGolemRenderState;
import dev.xkmc.modulargolems.content.entity.render.IGolemModel;
import dev.xkmc.modulargolems.content.item.golem.ClientHolderManager;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GolemHolderRenderer<
		E extends AbstractGolemEntity<E, P>,
		S extends LivingEntityRenderState & AbstractGolemRenderState<E, S, P>,
		P extends IGolemPart<P>,
		M extends EntityModel<S> & IGolemModel<E, S, P, M>>
		implements SpecialModelRenderer<GolemHolderRenderer.Data<S>> {

	private final M model;
	private final GolemType<?, P> type;

	public GolemHolderRenderer(M model, GolemType<?, P> type) {
		this.model = model;
		this.type = type;
	}

	public @Nullable Data<S> extractArgument(ItemStack stack) {
		ArrayList<GolemMaterial> list = GolemHolder.getMaterial(stack);
		if (!stack.has(GolemItems.ENTITY) &&
				!stack.has(GolemItems.DC_ICON) &&
				!stack.has(GolemItems.EQUIPMENTS))
			return new Data<>(list, null);
		if (stack.getItem() instanceof GolemHolder<?, ?> holder) {
			var golem = ClientHolderManager.getEntityForDisplay(holder, stack);
			if (golem == null) return null;
			var renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(golem);
			var state = renderer.createRenderState(Wrappers.cast(golem), 0);
			return new Data<>(list, Wrappers.cast(state));
		}
		return null;
	}

	public void submit(
			@Nullable Data<S> data, PoseStack pose, SubmitNodeCollector col,
			int light, int overlay, boolean hasFoil, int outlineCol
	) {
		if (data == null) return;
		var handle = new GolemRenderHandle(pose, col, light, overlay, hasFoil, outlineCol);
		if (data.state() == null) render(handle, data.mats());
		else renderEntity(handle, data.state());
	}

	private void render(GolemRenderHandle handle, List<GolemMaterial> list) {
		P[] parts = type.values();
		// parts[0].setupItemRender(stack, handle.type(), null); TODO
		for (int i = 0; i < parts.length; i++) {
			Identifier id = list.size() > i ? list.get(i).id() : GolemMaterial.EMPTY;
			handle.renderPart(model, id, parts[i]);
		}
	}

	private void renderEntity(GolemRenderHandle handle, S state) {
		PoseStack stack = handle.pose();
		stack.pushPose();
		// parts[0].setupItemRender(stack, handle.type(), null); TODO
		stack.translate(0, 1.501, 0);
		stack.scale(1, -1, -1);
		var renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(state);
		var cam = new CameraRenderState();
		renderer.submit(state, stack, handle.col(), cam);
		stack.popPose();
	}

	@Override
	public void getExtents(Consumer<Vector3fc> output) {
		PoseStack poseStack = new PoseStack();
		this.model.root().getExtentsForGui(poseStack, output);
	}

	public record Data<S extends LivingEntityRenderState & AbstractGolemRenderState<?, S, ?>>(
			List<GolemMaterial> mats, @Nullable S state) {
	}

	public record Unbaked(Identifier golemType) implements SpecialModelRenderer.Unbaked<Data<?>> {

		public static final MapCodec<GolemHolderRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				Identifier.CODEC.fieldOf("golem_type").forGetter(Unbaked::golemType)
		).apply(i, Unbaked::new));

		@Override
		public MapCodec<GolemHolderRenderer.Unbaked> type() {
			return MAP_CODEC;
		}

		public GolemHolderRenderer<?, ?, ?, ?> bake(BakingContext context) {
			return new GolemHolderRenderer<>(Wrappers.cast(GolemType.GOLEM_TYPE_TO_MODEL.get(golemType).get()
					.generateModel(context.entityModelSet()).getThis()),
					Wrappers.cast(GolemType.GOLEM_TYPE_TO_ITEM.get(golemType).getEntityType()));
		}

	}
}
