package dev.xkmc.modulargolems.content.item.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.render.IGolemModel;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class GolemPartRenderer<P extends IGolemPart<P>, M extends EntityModel<?> & IGolemModel<?, ?, P, M>>
		implements SpecialModelRenderer<GolemPartRenderer.Data<P>> {

	private final M model;

	public GolemPartRenderer(M model) {
		this.model = model;
	}

	public @Nullable Data<P> extractArgument(ItemStack stack) {
		return stack.getItem() instanceof GolemPart<?, ?> part ? new Data<>(
				GolemItems.DC_PART_MAT.getOrDefault(stack, ModularGolems.loc("empty")),
				Wrappers.cast(part.getPart())) : null;
	}

	public void submit(
			@Nullable Data<P> data, PoseStack pose, SubmitNodeCollector col,
			int light, int overlay, boolean hasFoil, int outlineCol
	) {
		if (data == null) return;
		var handle = new GolemRenderHandle(pose, col, light, overlay, hasFoil, outlineCol);
		//TODO transform
		handle.renderPart(model, data.data(), data.part());
	}

	@Override
	public void getExtents(Consumer<Vector3fc> output) {
		PoseStack poseStack = new PoseStack();
		this.model.root().getExtentsForGui(poseStack, output);
	}

	public record Data<P extends IGolemPart<P>>(Identifier data, P part) {
	}

	public record Unbaked(Identifier golemType) implements SpecialModelRenderer.Unbaked<Data<?>> {

		public static final MapCodec<GolemPartRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				Identifier.CODEC.fieldOf("golem_type").forGetter(Unbaked::golemType)
		).apply(i, Unbaked::new));

		@Override
		public MapCodec<GolemPartRenderer.Unbaked> type() {
			return MAP_CODEC;
		}

		public GolemPartRenderer<?, ?> bake(BakingContext context) {
			return new GolemPartRenderer<>(Wrappers.cast(GolemType.GOLEM_TYPE_TO_MODEL.get(golemType).get()
					.generateModel(context.entityModelSet()).getThis()));
		}

	}
}
