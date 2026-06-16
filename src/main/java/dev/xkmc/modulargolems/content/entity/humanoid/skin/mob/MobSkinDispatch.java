package dev.xkmc.modulargolems.content.entity.humanoid.skin.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemModel;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemRenderState;
import dev.xkmc.modulargolems.content.entity.skin.PlayerSkinRenderer;
import dev.xkmc.modulargolems.content.entity.skin.SpecialRenderSkin;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.util.TransformationHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public record MobSkinDispatch(
		PlayerSkinRenderer renderer, Identifier texture,
		List<IMobCloth> extra
) implements SpecialRenderSkin {

	public static final Map<EntityType<?>, MobSkinDispatch> MAP = new LinkedHashMap<>();

	@SafeVarargs
	public synchronized static void register(
			EntityRendererProvider.Context ctx, EntityType<?> type, ModelPart part, Identifier texture,
			Function<RenderLayerParent<HumanoidGolemRenderState, HumanoidGolemModel>, RenderLayer<HumanoidGolemRenderState, HumanoidGolemModel>>... layers) {
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

	public static final Identifier ZOMBIE = Identifier.withDefaultNamespace("textures/entity/zombie/zombie.png");
	public static final Identifier HUSK = Identifier.withDefaultNamespace("textures/entity/zombie/husk.png");
	public static final Identifier DROWNED = Identifier.withDefaultNamespace("textures/entity/zombie/drowned.png");
	public static final Identifier SKELETON = Identifier.withDefaultNamespace("textures/entity/skeleton/skeleton.png");
	public static final Identifier WITHER_SKELETON = Identifier.withDefaultNamespace("textures/entity/skeleton/wither_skeleton.png");
	public static final Identifier STRAY = Identifier.withDefaultNamespace("textures/entity/skeleton/stray.png");
	public static final Identifier BOGGED = Identifier.withDefaultNamespace("textures/entity/skeleton/bogged.png");
	public static final Identifier PIGLIN = Identifier.withDefaultNamespace("textures/entity/piglin/piglin.png");
	public static final Identifier PIGLIN_BRUTE = Identifier.withDefaultNamespace("textures/entity/piglin/piglin_brute.png");
	public static final Identifier ZOMBIFIED_PIGLIN = Identifier.withDefaultNamespace("textures/entity/piglin/zombified_piglin.png");

	private static final Identifier STRAY_CLOTH = Identifier.withDefaultNamespace("textures/entity/skeleton/stray_overlay.png");
	private static final Identifier BOGGED_CLOTH = Identifier.withDefaultNamespace("textures/entity/skeleton/bogged_overlay.png");

	public static void setup(EntityRendererProvider.Context ctx) {
		register(ctx, EntityType.ZOMBIE, ctx.bakeLayer(ModelLayers.ZOMBIE), ZOMBIE);
		register(ctx, EntityType.HUSK, ctx.bakeLayer(ModelLayers.HUSK), HUSK);
		register(ctx, EntityType.DROWNED, ctx.bakeLayer(ModelLayers.DROWNED), DROWNED, r ->
				new DrownedOuterLayer(r, make(ctx, ModelLayers.DROWNED_OUTER_LAYER)));

		register(ctx, EntityType.SKELETON, ctx.bakeLayer(ModelLayers.SKELETON), SKELETON);
		register(ctx, EntityType.BOGGED, ctx.bakeLayer(ModelLayers.BOGGED), BOGGED);
		register(ctx, EntityType.WITHER_SKELETON, ctx.bakeLayer(ModelLayers.WITHER_SKELETON), WITHER_SKELETON);
		register(ctx, EntityType.STRAY, ctx.bakeLayer(ModelLayers.STRAY), STRAY, r ->
				new SkeletonClothingLayer(r, make(ctx, ModelLayers.STRAY_OUTER_LAYER), STRAY_CLOTH));
		register(ctx, EntityType.BOGGED, ctx.bakeLayer(ModelLayers.BOGGED), BOGGED, r ->
				new SkeletonClothingLayer(r, make(ctx, ModelLayers.BOGGED_OUTER_LAYER), BOGGED_CLOTH));
		register(ctx, EntityType.PIGLIN, ctx.bakeLayer(ModelLayers.PIGLIN), PIGLIN);
		register(ctx, EntityType.PIGLIN_BRUTE, ctx.bakeLayer(ModelLayers.PIGLIN_BRUTE), PIGLIN_BRUTE);
		register(ctx, EntityType.ZOMBIFIED_PIGLIN, ctx.bakeLayer(ModelLayers.ZOMBIFIED_PIGLIN), ZOMBIFIED_PIGLIN);


	}

	public static @Nullable MobSkinDispatch of(EntityType<?> type) {
		return MAP.get(type);
	}

	private static ModelPart validatePart(ModelPart part) {
		validateChild(part, "hat");
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
		return new HumanoidGolemModel(validatePart(ctx.getModelSet().bakeLayer(layer)));
	}

	@Override
	public void submit(HumanoidGolemRenderState entity, PoseStack pose, SubmitNodeCollector source, CameraRenderState cam) {
		try {
			if (true | entity.headOnly) {
				pose.pushPose();
				pose.scale(-1.0F, -1.0F, 1.0F);
				float diff = renderer.getModel().root().y + renderer.getModel().head.y;
				pose.translate(0.0F, -1.501F , 0.0F);
				var rt = RenderTypes.entityCutout(texture());
				source.submitModel(renderer.getModel(), entity, pose, rt,
						LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
						-1, null, 0, null);
				for (var e : extra)
					e.renderHead(entity, pose, source, cam);
				pose.popPose();
			} else renderer.submitImpl(entity, pose, source, cam);
		} catch (Throwable e) {
			if (!FMLEnvironment.isProduction()) {
				ModularGolems.LOGGER.throwing(e);
			}
		}
	}

	public void renderSkullIcon(Level level, GuiGraphicsExtractor g, float pt, int x, int y) {
		var state = new HumanoidGolemRenderState();
		state.skinProfile = this;
		state.headOnly = true;
		state.entityType = GolemTypes.ENTITY_HUMANOID.get();
		var quat = TransformationHelper.quatFromXYZ(30, -45, 180, true);
		g.entity(state, 20, new Vector3f(0, 1, 0), quat, null, x - 20, y - 20, x + 20, y + 40);
	}

}
