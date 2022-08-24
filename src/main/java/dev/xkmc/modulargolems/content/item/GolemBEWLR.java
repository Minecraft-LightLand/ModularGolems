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
			render(handle, part, stack);
		}
		if (stack.getItem() instanceof GolemHolder<?, ?> holder) {
			render(handle, holder, stack);
		}
	}

	private <T extends AbstractGolemEntity<T, P>, P extends IGolemPart> void render(BEWLRHandle handle, GolemHolder<T, P> item, ItemStack stack) {
		ArrayList<GolemMaterial> list = GolemHolder.getMaterial(stack);
		P[] parts = item.getEntityType().values();
		for (int i = 0; i < parts.length; i++) {
			ResourceLocation id = list.size() > i ? list.get(i).id() : EMPTY;
			renderPart(handle, id, item.getEntityType(), parts[i]);
		}
	}

	private <T extends AbstractGolemEntity<T, P>, P extends IGolemPart> void render(BEWLRHandle handle, GolemPart<T, P> item, ItemStack stack) {
		Optional<ResourceLocation> id = GolemPart.getMaterial(stack);
		renderPart(handle, id.orElse(EMPTY), item.getEntityType(), item.getPart());
	}

	private <T extends AbstractGolemEntity<T, P>, P extends IGolemPart, M extends HierarchicalModel<T> & IGolemModel<T, P, M>>
	void renderPart(BEWLRHandle handle, ResourceLocation id, GolemType<T, P> type, P part) {
		PoseStack stack = handle.poseStack();
		stack.pushPose();
		stack.scale(1.0F, -1.0F, -1.0F);
		M model = Wrappers.cast(map.get(type.getRegistryName()));
		RenderType render = model.renderType(model.getTextureLocationInternal(id));
		VertexConsumer vc = ItemRenderer.getFoilBufferDirect(handle.bufferSource(), render, false, handle.stack().hasFoil());
		model.renderToBufferInternal(part, stack, vc, handle.light(), handle.overlay(), 1.0F, 1.0F, 1.0F, 1.0F);
		stack.popPose();
	}

}
