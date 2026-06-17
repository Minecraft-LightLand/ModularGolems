package dev.xkmc.modulargolems.content.entity.humanoid.skin.mob;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemModel;
import dev.xkmc.modulargolems.content.entity.humanoid.skin.PlayerSkinRenderer;
import dev.xkmc.modulargolems.content.entity.humanoid.skin.SpecialRenderSkin;
import dev.xkmc.modulargolems.init.ModularGolems;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("removal")
public record MobSkinDispatch(
		PlayerSkinRenderer renderer, ResourceLocation texture,
		List<IMobCloth> extra
) implements SpecialRenderSkin {

	public static final Map<EntityType<?>, MobSkinDispatch> MAP = new LinkedHashMap<>();

	@SafeVarargs
	public synchronized static void register(
			EntityRendererProvider.Context ctx, EntityType<?> type, ModelPart part, ResourceLocation texture,
			Function<RenderLayerParent<HumanoidGolemEntity, HumanoidGolemModel>, RenderLayer<HumanoidGolemEntity, HumanoidGolemModel>>... layers) {
		validatePart(part);
		PlayerSkinRenderer ans = new PlayerSkinRenderer(ctx, part, false);
		var clothes = new ArrayList<IMobCloth>();
		for (var e : layers) {
			var layer = e.apply(ans);
			ans.addLayer(layer);
			if (layer instanceof IMobCloth cloth)
				clothes.add(cloth);
		}
		MAP.put(type, new MobSkinDispatch(ans, texture, clothes));
	}

	public static final ResourceLocation ZOMBIE = new ResourceLocation("textures/entity/zombie/zombie.png");
	public static final ResourceLocation HUSK = new ResourceLocation("textures/entity/zombie/husk.png");
	public static final ResourceLocation DROWNED = new ResourceLocation("textures/entity/zombie/drowned.png");
	public static final ResourceLocation SKELETON = new ResourceLocation("textures/entity/skeleton/skeleton.png");
	public static final ResourceLocation WITHER_SKELETON = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
	public static final ResourceLocation STRAY = new ResourceLocation("textures/entity/skeleton/stray.png");
	public static final ResourceLocation PIGLIN = new ResourceLocation("textures/entity/piglin/piglin.png");
	public static final ResourceLocation PIGLIN_BRUTE = new ResourceLocation("textures/entity/piglin/piglin_brute.png");
	public static final ResourceLocation ZOMBIFIED_PIGLIN = new ResourceLocation("textures/entity/piglin/zombified_piglin.png");

	public static void setup(EntityRendererProvider.Context ctx) {
		register(ctx, EntityType.ZOMBIE, ctx.bakeLayer(ModelLayers.ZOMBIE), ZOMBIE);
		register(ctx, EntityType.HUSK, ctx.bakeLayer(ModelLayers.HUSK), HUSK);
		register(ctx, EntityType.DROWNED, ctx.bakeLayer(ModelLayers.DROWNED), DROWNED, r ->
				new DrownedOuterLayer(r, make(ctx, ModelLayers.DROWNED_OUTER_LAYER)));

		register(ctx, EntityType.SKELETON, ctx.bakeLayer(ModelLayers.SKELETON), SKELETON);
		register(ctx, EntityType.WITHER_SKELETON, ctx.bakeLayer(ModelLayers.WITHER_SKELETON), WITHER_SKELETON);
		register(ctx, EntityType.STRAY, ctx.bakeLayer(ModelLayers.STRAY), STRAY, r ->
				new StrayClothingLayer(r, make(ctx, ModelLayers.STRAY_OUTER_LAYER)));

		register(ctx, EntityType.PIGLIN, ctx.bakeLayer(ModelLayers.PIGLIN), PIGLIN);
		register(ctx, EntityType.PIGLIN_BRUTE, ctx.bakeLayer(ModelLayers.PIGLIN_BRUTE), PIGLIN_BRUTE);
		register(ctx, EntityType.ZOMBIFIED_PIGLIN, ctx.bakeLayer(ModelLayers.ZOMBIFIED_PIGLIN), ZOMBIFIED_PIGLIN);

	}

	public static @Nullable MobSkinDispatch of(EntityType<?> type) {
		return MAP.get(type);
	}

	private static ModelPart validatePart(ModelPart part) {
		validateChild(part, "hat");
		validateChild(part, "ear");
		validateChild(part, "cloak");
		validateChild(part, "left_sleeve");
		validateChild(part, "right_sleeve");
		validateChild(part, "left_pants");
		validateChild(part, "right_pants");
		validateChild(part, "jacket");
		return part;
	}

	private static void validateChild(ModelPart part, String child) {
		if (part.hasChild(child)) return;
		if (!(part.children instanceof Object2ObjectArrayMap)) {
			part.children = new Object2ObjectArrayMap<>(part.children);
		}
		part.children.put(child, new ModelPart(List.of(), Map.of()));
	}

	private static HumanoidGolemModel make(EntityRendererProvider.Context ctx, ModelLayerLocation layer) {
		return new HumanoidGolemModel(validatePart(ctx.getModelSet().bakeLayer(layer)), false);
	}

	@Override
	public void render(HumanoidGolemEntity entity, float f1, float f2, PoseStack stack, MultiBufferSource source, int i) {
		try {
			renderer.render(entity, f1, f2, stack, source, i);
		} catch (Throwable e) {
			if (!FMLEnvironment.production) {
				ModularGolems.LOGGER.throwing(e);
			}
		}
	}

	private static final ItemTransform SKULL = new ItemTransform(
			new Vector3f(30, 45, 0),
			new Vector3f(0, 3, 0),
			new Vector3f(1, 1, 1)
	);

	public void renderSkullIcon(Level level, GuiGraphics g, float pt) {
		var pose = g.pose();
		pose.pushPose();
		int r = 20;
		pose.translate(8f, -48F, 16);
		pose.scale(-r, r, -r);
		SKULL.apply(false, pose);
		var head = renderer().getModel().getHead();
		head.resetPose();
		var vc = g.bufferSource().getBuffer(RenderType.entityCutoutNoCull(texture()));
		head.render(g.pose(), vc, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
		for (var e : extra) {
			e.renderHead(g.pose(), g.bufferSource(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
		}
		pose.popPose();
	}

}
