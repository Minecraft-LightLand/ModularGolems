package dev.xkmc.modulargolems.content.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class GolemAfterimageParticle extends Particle {

	private static final ThreadLocal<Boolean> RENDERING = ThreadLocal.withInitial(() -> false);

	public static boolean isRendering() {
		return RENDERING.get();
	}

	private final int entityId;

	protected GolemAfterimageParticle(ClientLevel level, double x, double y, double z,
			int r, int g, int b, int entityId, int lifetime) {
		super(level, x, y, z);
		this.setSize(6.0F, 6.0F);
		this.x = x;
		this.y = y;
		this.z = z;
		this.rCol = r;
		this.gCol = g;
		this.bCol = b;
		this.entityId = entityId;
		this.lifetime = lifetime;
	}

	public Entity getFromEntity() {
		return entityId == -1 ? null : level.getEntity(entityId);
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.age++ >= this.lifetime) {
			this.remove();
		}
		if (getFromEntity() == null) {
			remove();
		}
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void render(@NotNull VertexConsumer vertex, @NotNull Camera camera, float tick) {
		MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
		Vec3 vec3 = camera.getPosition();
		EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		double lerpX = Mth.lerp(tick, this.xo, this.x);
		double lerpY = Mth.lerp(tick, this.yo, this.y);
		double lerpZ = Mth.lerp(tick, this.zo, this.z);
		float colorR = this.rCol / 255.0F;
		float colorG = this.gCol / 255.0F;
		float colorB = this.bCol / 255.0F;
		float colorA = 0.5F / Math.abs((float) age + 1);
		PoseStack stack = new PoseStack();
		Entity entity = getFromEntity();
		if (entity instanceof LivingEntity living) {
			EntityRenderer<? super LivingEntity> raw = dispatcher.getRenderer(living);
			if (raw instanceof LivingEntityRenderer renderer) {

				MultiBufferSource tinted = requested -> {
					VertexConsumer original = source.getBuffer(requested);
					return tint(original, colorR, colorG, colorB, colorA);
				};
				RENDERING.set(true);
				try {
					dispatcher.render(living,
							lerpX - vec3.x(), lerpY - vec3.y(), lerpZ - vec3.z(),
							living.getYRot(), tick, stack, tinted, dispatcher.getPackedLightCoords(living, tick));
				} finally {
					RENDERING.set(false);
				}
			}
		}
	}

	private static VertexConsumer tint(VertexConsumer original, float colorR, float colorG, float colorB, float colorA) {
		return new VertexConsumer() {
			@Override
			public VertexConsumer vertex(double x, double y, double z) {
				original.vertex(x, y, z);
				return this;
			}

			@Override
			public VertexConsumer color(int r, int g, int b, int a) {
				original.color((int) (r * colorR), (int) (g * colorG), (int) (b * colorB), (int) (a * colorA));
				return this;
			}

			@Override
			public VertexConsumer uv(float u, float v) {
				original.uv(u, v);
				return this;
			}

			@Override
			public VertexConsumer overlayCoords(int u, int v) {
				original.overlayCoords(u, v);
				return this;
			}

			@Override
			public VertexConsumer uv2(int u, int v) {
				original.uv2(u, v);
				return this;
			}

			@Override
			public VertexConsumer normal(float x, float y, float z) {
				original.normal(x, y, z);
				return this;
			}

			@Override
			public void endVertex() {
				original.endVertex();
			}

			@Override
			public void defaultColor(int r, int g, int b, int a) {
				original.defaultColor((int) (r * colorR), (int) (g * colorG), (int) (b * colorB), (int) (a * colorA));
			}

			@Override
			public void unsetDefaultColor() {
				original.unsetDefaultColor();
			}
		};
	}

	@Override
	public @NotNull ParticleRenderType getRenderType() {
		return ParticleRenderType.CUSTOM;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements ParticleProvider<GolemAfterimageOptions> {

		@Override
		public Particle createParticle(@NotNull GolemAfterimageOptions data, @NotNull ClientLevel level,
				double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new GolemAfterimageParticle(level, x, y, z,
					data.r(), data.g(), data.b(), data.entityId(), data.lifeTicks());
		}

	}

}
