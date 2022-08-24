package dev.xkmc.modulargolems.content.item;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xkmc.l2library.util.code.Wrappers;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.common.IGolemModel;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;

public class GolemBEWLR extends BlockEntityWithoutLevelRenderer {

	public static final Supplier<BlockEntityWithoutLevelRenderer> INSTANCE = Suppliers.memoize(() ->
			new GolemBEWLR(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()));

	public static final IClientItemExtensions EXTENSIONS = new IClientItemExtensions() {

		@Override
		public BlockEntityWithoutLevelRenderer getCustomRenderer() {
			return INSTANCE.get();
		}

	};

	private static final ResourceLocation EMPTY = new ResourceLocation(ModularGolems.MODID, "empty");

	private final EntityModelSet entityModelSet;
	private final HashMap<ResourceLocation, IGolemModel<?, ?, ?>> map = new HashMap<>();

	public GolemBEWLR(BlockEntityRenderDispatcher dispatcher, EntityModelSet set) {
		super(dispatcher, set);
		entityModelSet = set;
	}

	public void onResourceManagerReload(ResourceManager p_172555_) {
		map.clear();
		GolemType.GOLEM_TYPE_TO_MODEL.forEach((k, v) -> map.put(k, v.get().generateModel(entityModelSet)));
	}

	@Override
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType type, PoseStack poseStack,
							 MultiBufferSource bufferSource, int light, int overlay) {
		BEWLRHandle handle = new BEWLRHandle(stack, type, poseStack, bufferSource, light, overlay);
		if (stack.getItem() instanceof GolemPart<?, ?> part) {
			render(handle, part);
		}
		if (stack.getItem() instanceof GolemHolder<?, ?> holder) {
			render(handle, holder);
		}
	}

	private <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>> void render(BEWLRHandle handle, GolemHolder<T, P> item) {
		ArrayList<GolemMaterial> list = GolemHolder.getMaterial(handle.stack());
		P[] parts = item.getEntityType().values();
		PoseStack stack = handle.poseStack();
		stack.pushPose();
		if (parts.length > 0) {
			parts[0].setupItemRender(stack, null);
		}
		for (int i = 0; i < parts.length; i++) {
			ResourceLocation id = list.size() > i ? list.get(i).id() : EMPTY;
			renderPart(handle, id, item.getEntityType(), parts[i]);
		}
		stack.popPose();
	}

	private <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>> void render(BEWLRHandle handle, GolemPart<T, P> item) {
		PoseStack stack = handle.poseStack();
		stack.pushPose();
		P part = item.getPart();
		part.setupItemRender(stack, part);
		Optional<ResourceLocation> id = GolemPart.getMaterial(handle.stack());
		renderPart(handle, id.orElse(EMPTY), item.getEntityType(), part);
		stack.popPose();
	}

	private <T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>, M extends HierarchicalModel<T> & IGolemModel<T, P, M>>
	void renderPart(BEWLRHandle handle, ResourceLocation id, GolemType<T, P> type, P part) {
		M model = Wrappers.cast(map.get(type.getRegistryName()));
		RenderType render = model.renderType(model.getTextureLocationInternal(id));
		VertexConsumer vc = ItemRenderer.getFoilBufferDirect(handle.bufferSource(), render, false, handle.stack().hasFoil());
		model.renderToBufferInternal(part, handle.poseStack(), vc, handle.light(), handle.overlay(), 1.0F, 1.0F, 1.0F, 1.0F);
	}

}
