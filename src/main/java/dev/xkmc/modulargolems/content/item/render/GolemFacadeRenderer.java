package dev.xkmc.modulargolems.content.item.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import dev.xkmc.modulargolems.content.client.armor.GolemEquipmentModels;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemModel;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class GolemFacadeRenderer implements SpecialModelRenderer<Identifier> {

	private final MetalGolemModel model;

	public GolemFacadeRenderer(EntityModelSet set) {
		this.model = new MetalGolemModel(set.bakeLayer(GolemEquipmentModels.METALGOLEM));
	}

	public Identifier extractArgument(ItemStack stack) {
		return GolemItems.DC_PART_MAT.getOrDefault(stack, ModularGolems.loc("empty"));
	}

	public void submit(
			@Nullable Identifier id, PoseStack pose, SubmitNodeCollector col,
			int light, int overlay, boolean hasFoil, int outlineCol
	) {
		var handle = new GolemRenderHandle(pose, col, light, overlay, hasFoil, outlineCol);
		pose.pushPose();
		pose.translate(0.5f, -0.375f, 0.5f);
		pose.mulPose(Axis.YP.rotationDegrees(180));
		pose.scale(1, -1, 1);
		if (id == null) id = ModularGolems.loc("empty");
		var type = model.renderType(model.getTextureLocationInternal(id));
		handle.render(type, model.getHead());
		pose.popPose();
	}

	@Override
	public void getExtents(Consumer<Vector3fc> output) {
		PoseStack poseStack = new PoseStack();
		this.model.root().getExtentsForGui(poseStack, output);
	}

	public record Unbaked() implements SpecialModelRenderer.Unbaked<Identifier> {
		public static final GolemFacadeRenderer.Unbaked INSTANCE = new GolemFacadeRenderer.Unbaked();
		public static final MapCodec<GolemFacadeRenderer.Unbaked> MAP_CODEC = MapCodec.unit(INSTANCE);

		@Override
		public MapCodec<GolemFacadeRenderer.Unbaked> type() {
			return MAP_CODEC;
		}

		public GolemFacadeRenderer bake(BakingContext context) {
			return new GolemFacadeRenderer(context.entityModelSet());
		}

	}
}
