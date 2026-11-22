package dev.xkmc.modulargolems.content.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

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
	public void render(VertexConsumer vertex, Camera camera, float tick) {
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

	private static VertexConsumer tint(VertexConsumer o, float cr, float cg, float cb, float ca) {
		return new VertexConsumer() {
			@Override
			public VertexConsumer addVertex(float x, float y, float z) {
				o.addVertex(x, y, z);
				return this;
			}

			@Override
			public VertexConsumer addVertex(Matrix4f m, float x, float y, float z) {
				o.addVertex(m, x, y, z);
				return this;
			}

			@Override
			public VertexConsumer addVertex(PoseStack.Pose p, float x, float y, float z) {
				o.addVertex(p, x, y, z);
				return this;
			}

			@Override
			public VertexConsumer setColor(int r, int g, int b, int a) {
				o.setColor((int) (r * cr), (int) (g * cg), (int) (b * cb), (int) (a * ca));
				return this;
			}

			@Override
			public VertexConsumer setUv(float u, float v) {
				o.setUv(u, v);
				return this;
			}

			@Override
			public VertexConsumer setUv1(int u, int v) {
				o.setUv1(u, v);
				return this;
			}

			@Override
			public VertexConsumer setUv2(int u, int v) {
				o.setUv2(u, v);
				return this;
			}

			@Override
			public VertexConsumer setNormal(float x, float y, float z) {
				o.setNormal(x, y, z);
				return this;
			}
		};
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.CUSTOM;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements ParticleProvider<GolemAfterimageOptions> {

		@Override
		public Particle createParticle(GolemAfterimageOptions data, ClientLevel level,
				double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new GolemAfterimageParticle(level, x, y, z,
					data.r(), data.g(), data.b(), data.entityId(), data.lifeTicks());
		}

	}

}
