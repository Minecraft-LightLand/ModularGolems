package dev.xkmc.modulargolems.content.entity.common;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xkmc.l2library.util.Proxy;
import dev.xkmc.l2serial.util.Wrappers;
import dev.xkmc.modulargolems.compat.curio.CurioCompatRegistry;
import dev.xkmc.modulargolems.compat.materials.alexscaves.client.AtomicBeamRenderer;
import dev.xkmc.modulargolems.content.client.override.ModelOverride;
import dev.xkmc.modulargolems.content.client.override.ModelOverrides;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.item.golem.GolemFacade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractGolemRenderer<T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>, M extends EntityModel<T> & IGolemModel<T, P, M>> extends MobRenderer<T, M> {

	public static final List<Function<AbstractGolemRenderer<?, ?, ?>, RenderLayer<? extends AbstractGolemEntity<?, ?>, ?>>> LIST = new ArrayList<>();

	private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");

	private final Supplier<P[]> list;

	public AbstractGolemRenderer(EntityRendererProvider.Context ctx, M model, float f, Supplier<P[]> list) {
		super(ctx, model, f);
		this.list = list;
		addLayer(new GolemDefaultLayer<>(this));
		addLayer(new RiptideEffectLayer<>(this, ctx));
		LIST.forEach(e -> this.addLayer(Wrappers.cast(e.apply(this))));
	}

	@Override
	protected void scale(T entity, PoseStack pose, float f) {
		float r = entity.getScale();
		pose.scale(r, r, r);
	}

	public ResourceLocation getTextureLocation(T entity) {
		return GOLEM_LOCATION;
	}

	protected boolean delegated(T entity) {
		return false;
	}

	@Override
	public void render(T entity, float f1, float f2, PoseStack stack, MultiBufferSource source, int i) {
		entity.validateData();
		if (model instanceof IHeadedModel headed) headed.getHead().visible = true;
		super.render(entity, f1, f2, stack, source, i);
		if (entity.isBeamActive()) AtomicBeamRenderer.render(entity, stack, source, f2);
	}

	@Nullable
	@Override
	protected RenderType getRenderType(T entity, boolean b1, boolean b2, boolean b3) {
		if (delegated(entity))
			return super.getRenderType(entity, b1, b2, b3);
		return null;
	}

	@Override
	protected void setupRotations(T e, PoseStack pose, float yaw, float bodyYaw, float partialTick) {
		e.validateData();
		if (!e.isAutoSpinAttack()) {
			super.setupRotations(e, pose, yaw, bodyYaw, partialTick);
			return;
		}
		// 波塞冬激流旋身：不调 super（否则与原版 SPIN_ATTACK 旋转叠加成双重），自写一份。
		pose.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
		pose.translate(0.0F, e.getBbHeight() * 0.5F, 0.0F);
		if (e instanceof DogGolemEntity) {
			// 犬形是四足水平身体：不做 -90° 放平，保持四足姿态，绕前进轴(前后轴) roll → 像钻头。
			// 去掉 XP -90 后 roll 轴会落在模型 pivot(脚底)，先平移到身体中心再转、转完平移回来，
			// 让旋转中心落在身体正中而不是脚下。
			pose.translate(0.0F, e.getBbHeight() * 0.7F, 0.0F);
			pose.mulPose(Axis.ZP.rotationDegrees((e.tickCount + partialTick) * 75.0F));
			pose.translate(0.0F, -e.getBbHeight() * 0.7F, 0.0F);
		} else {
			// 人形/大型：原版放平成横躺再绕长轴旋转
			pose.mulPose(Axis.XP.rotationDegrees(-90.0F - e.getXRot()));
			pose.mulPose(Axis.YP.rotationDegrees((e.tickCount + partialTick) * -75.0F));
		}
	}

	protected void renderAllParts(PoseStack pose, MultiBufferSource buffer, int light, T entity, float pTick) {
		if (delegated(entity)) return;
		var player = Proxy.getClientPlayer();
		boolean visible = isBodyVisible(entity);
		boolean ghost = !visible && player != null && !entity.isInvisibleTo(player);
		boolean glowing = Minecraft.getInstance().shouldEntityAppearGlowing(entity);
		pose.pushPose();
		ResourceLocation facade = null;
		if (ModList.get().isLoaded("curios")) {
			var opt = CurioCompatRegistry.getItem(entity, "golem_skin");
			if (opt.isPresent() && opt.get().getItem() instanceof GolemFacade)
				facade = GolemFacade.getMaterial(opt.get());
		}
		var materials = entity.getMaterials();
		for (P part : list.get()) {
			ResourceLocation rl = facade;
			if (rl != null && !GolemMaterialConfig.mayApply(part.toItem(), rl)) rl = null;
			if (rl == null) {
				int index = part.ordinal();
				rl = materials.size() > index ? materials.get(index).id() : GolemMaterial.EMPTY;
			}
			ModelOverride override = ModelOverrides.getOverride(rl);
			override.renderAll(this, entity, part, pose, buffer, rl, light, pTick, visible, ghost, glowing);
		}
		pose.popPose();
	}

	public void renderPartModel(T entity, P part, PoseStack pose, VertexConsumer vc, int light, float pTick, boolean ghost) {
		int overlay = getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, pTick));
		this.model.renderToBufferInternal(part, pose, vc, light, overlay, 1.0F, 1.0F, 1.0F, ghost ? 0.15F : 1.0F);

	}

}