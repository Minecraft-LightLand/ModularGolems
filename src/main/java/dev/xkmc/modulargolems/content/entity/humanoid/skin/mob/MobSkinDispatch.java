package dev.xkmc.modulargolems.content.entity.humanoid.skin.mob;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemModel;
import dev.xkmc.modulargolems.content.entity.humanoid.skin.PlayerSkinRenderer;
import dev.xkmc.modulargolems.content.entity.humanoid.skin.SpecialRenderSkin;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("removal")
public record MobSkinDispatch(PlayerSkinRenderer renderer, ResourceLocation texture) implements SpecialRenderSkin {

	public static final Map<EntityType<?>, MobSkinDispatch> MAP = new LinkedHashMap<>();

	@SafeVarargs
	public synchronized static void register(
			EntityRendererProvider.Context ctx, EntityType<?> type, ModelPart part, ResourceLocation texture,
			Function<RenderLayerParent<HumanoidGolemEntity, HumanoidGolemModel>, RenderLayer<HumanoidGolemEntity, HumanoidGolemModel>>... layers) {
		validatePart(part);
		PlayerSkinRenderer ans = new PlayerSkinRenderer(ctx, part, false);
		for (var e : layers) {
			ans.addLayer(e.apply(ans));
		}
		MAP.put(type, new MobSkinDispatch(ans, texture));
	}

	public static final ResourceLocation ZOMBIE = ResourceLocation.withDefaultNamespace("textures/entity/zombie/zombie.png");
	public static final ResourceLocation HUSK = ResourceLocation.withDefaultNamespace("textures/entity/zombie/husk.png");
	public static final ResourceLocation DROWNED = ResourceLocation.withDefaultNamespace("textures/entity/zombie/drowned.png");
	public static final ResourceLocation SKELETON = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/skeleton.png");
	public static final ResourceLocation WITHER_SKELETON = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/wither_skeleton.png");
	public static final ResourceLocation STRAY = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/stray.png");
	public static final ResourceLocation PIGLIN = ResourceLocation.withDefaultNamespace("textures/entity/piglin/piglin.png");
	public static final ResourceLocation PIGLIN_BRUTE = ResourceLocation.withDefaultNamespace("textures/entity/piglin/piglin_brute.png");
	public static final ResourceLocation ZOMBIFIED_PIGLIN = ResourceLocation.withDefaultNamespace("textures/entity/piglin/zombified_piglin.png");

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

	public static @Nullable SpecialRenderSkin of(EntityType<?> type) {
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

}
