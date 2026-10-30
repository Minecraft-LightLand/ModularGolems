package dev.xkmc.modulargolems.compat.materials.alexscaves.client;

import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

/**
 * Render types of Alex's Caves' Tremorzilla beam, replicated so the golem beam blends, fades and
 * blooms the same way instead of using vanilla's additive energy swirl variant.
 * <p>
 * The set of textures is fixed, so the composite states are built once here rather than per beam per
 * frame: {@link RenderType#create} allocates a new state and re-runs the shard setup every call.
 * <p>
 * The glow variants mirror {@code ACRenderTypes.IRRADIATED_OUTPUT}, which is not public: the beam
 * is drawn into Citadel's irradiated render target so the post chain can bloom it, and the main
 * target is rebound afterwards. Citadel ships with Cataclysm rather than with Alex's Caves, so it
 * is looked up once and the plain variants are used when it is absent.
 */
final class AtomicBeamRenderTypes extends RenderType {

	private static final int END_FRAME_COUNT = 3;

	/**
	 * Citadel owns the post chain that produces the bloom. Without it there is no target to draw
	 * into, so the glow passes are skipped rather than silently drawing nowhere.
	 */
	private static final boolean GLOW_AVAILABLE = ModList.get().isLoaded("citadel");

	private static final ResourceLocation BEAM_INNER =
			new ResourceLocation("alexscaves", "textures/entity/tremorzilla/tremorzilla_beam_inner.png");
	private static final ResourceLocation BEAM_OUTER =
			new ResourceLocation("alexscaves", "textures/entity/tremorzilla/tremorzilla_beam_outer.png");
	private static final ResourceLocation[] BEAM_END = {
			new ResourceLocation("alexscaves", "textures/entity/tremorzilla/tremorzilla_beam_end_0.png"),
			new ResourceLocation("alexscaves", "textures/entity/tremorzilla/tremorzilla_beam_end_1.png"),
			new ResourceLocation("alexscaves", "textures/entity/tremorzilla/tremorzilla_beam_end_2.png")
	};

	/**
	 * Copy of {@code ACRenderTypes.IRRADIATED_OUTPUT}. The bodies are only resolved when the state
	 * is set up during rendering, so referencing Citadel here does not load it at class init.
	 * <p>
	 * Declared before every render type on purpose: {@link #beam} reads this field, and the glow
	 * types below are static initialisers, so anything that assigns them earlier would still see
	 * this as null and hand a null output state to {@code createCompositeState}.
	 */
	private static final RenderStateShard.OutputStateShard IRRADIATED_OUTPUT =
			new RenderStateShard.OutputStateShard("irradiated_target", () -> {
				RenderTarget target = PostEffectRegistry.getRenderTargetFor(ClientProxy.IRRADIATED_SHADER);
				if (target != null) {
					target.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
					target.bindWrite(false);
				}
			}, () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false));

	private AtomicBeamRenderTypes() {
		super("modulargolems_atomic_beam_types", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, () -> {
		}, () -> {
		});
	}

	static final RenderType INNER = beam(BEAM_INNER, false);
	static final RenderType OUTER = beam(BEAM_OUTER, false);
	private static final RenderType[] END_CAP = {
			beam(BEAM_END[0], false),
			beam(BEAM_END[1], false),
			beam(BEAM_END[2], false)
	};
	static final RenderType INNER_GLOW = beam(BEAM_INNER, true);
	static final RenderType OUTER_GLOW = beam(BEAM_OUTER, true);

	static RenderType endCap(int frame) {
		return END_CAP[Math.floorMod(frame, END_FRAME_COUNT)];
	}

	/**
	 * Queues Citadel's post chain so the layer just drawn into the irradiated target gets bloomed.
	 */
	static void queueGlowEffect() {
		if (GLOW_AVAILABLE) PostEffectRegistry.renderEffectForNextTick(ClientProxy.IRRADIATED_SHADER);
	}

	static boolean glowAvailable() {
		return GLOW_AVAILABLE;
	}

	private static RenderType beam(ResourceLocation texture, boolean glow) {
		return RenderType.create("modulargolems_atomic_beam", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
				RenderType.CompositeState.builder()
						.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
						.setShaderState(RenderStateShard.RENDERTYPE_ENERGY_SWIRL_SHADER)
						.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
						.setCullState(RenderStateShard.NO_CULL)
						.setLightmapState(RenderStateShard.LIGHTMAP)
						.setOutputState(glow && GLOW_AVAILABLE ? IRRADIATED_OUTPUT : RenderStateShard.ITEM_ENTITY_TARGET)
						.createCompositeState(false));
	}

}