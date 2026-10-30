package dev.xkmc.modulargolems.compat.materials.eeeab.client;

import com.eeeab.eeeabsmobs.client.ClientProxy;
import com.eeeab.eeeabsmobs.client.particle.ParticleDust;
import com.eeeab.eeeabsmobs.client.particle.ParticleRing;
import com.eeeab.eeeabsmobs.client.particle.lib.AdvancedBlockParticle;
import com.eeeab.eeeabsmobs.client.particle.lib.component.ParticleComponent;
import com.eeeab.eeeabsmobs.client.particle.util.ModParticleUtils;
import com.eeeab.eeeabsmobs.client.render.LightningBolt;
import com.eeeab.eeeabsmobs.sever.init.ParticleInit;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

import javax.annotation.Nullable;

/**
 * Client only half of the EEEAB realm warden skills.
 * <p>
 * Everything here touches {@code net.minecraft.client.*} through EEEAB's own particle and render
 * classes, so it must stay out of {@code EEEABProxyImpl}: that class is reachable from the server
 * (the jump smash runs its telegraph and damage on the server side) and loading it on a dedicated
 * server would fail on the client classes and take the whole EEEAB skill group down with it.
 * <p>
 * Callers must still be gated on {@code level.isClientSide} before entering, otherwise the
 * dedicated server loads this class and throws.
 */
public final class EEEABClientFx {

	private static final Vector4f[] BOLT_COLORS = {
			new Vector4f(0.85F, 0.9F, 1F, 1F),
			new Vector4f(0.52F, 0.94F, 1F, 1F)
	};

	private EEEABClientFx() {
	}

	public static void jumpSmash(LivingEntity golem, Vec3 pos, float scale, float yBlockPtcScale,
			boolean dust, boolean ring, @Nullable double[] angles, boolean secondPhase) {
		Level level = golem.level();
		double[] useAngles = angles != null ? angles : new double[]{40, 30, 20};
		double[] colors = secondPhase
				? new double[]{0.52D, 0.94D, 1.0D, 1.0D}
				: new double[]{1.0D, 0.88D, 0.48D, 1.0D};
		RandomSource random = golem.getRandom();
		ModParticleUtils.multiLayerBowlParticles(level, pos, 3, new int[]{6, 8, 5},
				new double[]{0.6D * scale, 1.0D * scale, 1.2D * scale},
				new double[]{1.8D, 2.2D, 2.5D}, useAngles, colors, new double[]{1.0D, 1.1D, 1.2D}, 0.55F);
		if (dust) {
			ParticleDust.DustData dustData = new ParticleDust.DustData(ParticleInit.DUST.get(), 30F, 20, ParticleDust.EnumDustBehavior.GROW, 0.8F);
			ModParticleUtils.annularParticleOutburst(level, 18, dustData, pos.x, pos.y, pos.z, 1.6F, 0.6F);
		}
		if (ring) {
			level.addParticle(new ParticleRing.RingData(ParticleInit.BIG_RING.get(), 0F, (float) (Math.PI / 2D), 10,
					(float) colors[0], (float) colors[1], (float) colors[2], 1F, 50F * scale, false,
					ParticleRing.EnumRingBehavior.GROW), pos.x, pos.y + 0.1F, pos.z, 0, 0, 0);
		}
		ModParticleUtils.blockParticlesAround(level, pos.x, pos.y, pos.z, (int) (12 * scale), 0.75D * scale, 2.25D * scale,
				0.1D, 0.2D, 0.25D * yBlockPtcScale, 0.5D * yBlockPtcScale, -0.2D, 0.1D,
				(blockPos, state) -> AdvancedBlockParticle.createBlockParticleData(state, null, pos.x, pos.y, pos.z,
						(random.nextFloat() * 0.5F + 0.5F) * 2 / scale, 0.6D, 0.6D, 0.6D, 1D, 0.98D, 40, false, true,
						new ParticleComponent[]{new ParticleComponent.FreeFallSimulator(1.5F)}));
	}

	public static void jumpSmashDerived(LivingEntity golem, Vec3 pos) {
		Level level = golem.level();
		RandomSource random = golem.getRandom();
		ModParticleUtils.blockParticlesAround(level, pos.x, pos.y, pos.z, 15, 0.5D, 1D, 0.35D, 0.5D, 0.4D, 0.75D,
				-0.2D, 0.1D, (blockPos, state) -> AdvancedBlockParticle.createBlockParticleData(state, null,
						pos.x, pos.y, pos.z, (random.nextFloat() * 0.5F + 0.5F) * 1.5F, 0.6D, 0.6D, 0.6D, 1D, 0.98D, 17,
						true, true, new ParticleComponent[]{
								new ParticleComponent.FreeFallSimulator(1F),
								new ParticleComponent.Attractor(new Vec3[]{pos.add(0, 0.5D, 0)}, 4.5F, 1.5F,
										ParticleComponent.Attractor.EnumAttractorBehavior.SIMULATED, 7)
						}));
		level.addParticle(new ParticleRing.RingData(ParticleInit.BIG_RING.get(), 0F, (float) (Math.PI / 2D), 7,
				0.52F, 0.94F, 1F, 0.8F, 50F, false, ParticleRing.EnumRingBehavior.SHRINK), pos.x, pos.y + 0.1F, pos.z, 0, 0, 0);
	}

	public static void leapImpact(LivingEntity golem, boolean slam) {
		Level level = golem.level();
		double[] angles = new double[]{slam ? 55 : 45, 25, 20};
		if (slam) {
			ModParticleUtils.multiLayerBowlParticles(level, golem.position(), 4, new int[]{6, 8, 5},
					new double[]{0.6D, 1.0D, 1.2D}, new double[]{1.8D, 2.2D, 2.5D}, angles,
					new double[]{1.0D, 0.88D, 0.48D, 1.0D}, new double[]{1.0D, 1.1D, 1.2D}, 0.55F);
		}
		ModParticleUtils.blockParticlesAround(level, golem.getX(), golem.getY(), golem.getZ(), slam ? 30 : 12,
				slam ? 1.2D : 0.75D, slam ? 2.5D : 2.25D, 0.1D, 0.2D, 0.25D, 0.5D, -0.2D, 0.1D,
				(blockPos, state) -> AdvancedBlockParticle.createBlockParticleData(state, null,
						golem.getX(), golem.getY(), golem.getZ(),
						slam ? 1.2D * (golem.getRandom().nextFloat() * 0.5F + 0.5F)
								: (golem.getRandom().nextFloat() * 0.5F + 0.5F) * 2,
						0.6D, 0.6D, 0.6D, 1D, 0.98D, slam ? 17 : 40, false, true,
						new ParticleComponent[]{new ParticleComponent.FreeFallSimulator(slam ? 1F : 1.5F)}));
	}

	public static void leapLanding(LivingEntity golem, boolean secondPhase, boolean slam) {
		Level level = golem.level();
		RandomSource random = golem.getRandom();
		ModParticleUtils.blockParticlesAround(level, golem.getX(), golem.getY(), golem.getZ(), slam ? 15 : 6,
				slam ? 0.5D : 1.5D, slam ? 1D : 3D, 0.01D, 0.1D, 0.15D, 0.25D, -0.2D, 0.1D,
				(blockPos, state) -> AdvancedBlockParticle.createBlockParticleData(state, null,
						golem.getX(), golem.getY(), golem.getZ(), random.nextFloat() * 0.2F + 0.6F,
						0.6D, 0.6D, 0.6D, 1D, 0.98D, slam ? 20 : 40, false, true,
						new ParticleComponent[]{new ParticleComponent.FreeFallSimulator(1F)}));
		if (slam && secondPhase) {
			level.addParticle(new ParticleRing.RingData(ParticleInit.BIG_RING.get(), 0F, (float) (Math.PI / 2D), 10,
					0.52F, 0.94F, 1F, 1F, 50F, false, ParticleRing.EnumRingBehavior.GROW),
					golem.getX(), golem.getY() + 0.1F, golem.getZ(), 0, 0, 0);
		}
	}

	/**
	 * {@code doLandingExplosionEffect} of {@code EntityRealmWarden}: the follow-up the second phase gets
	 * once the telegraph resolves. Sixteen bolts fan out of both feet across a 160 degree arc, every
	 * third one thicker and held longer so the burst has a readable pulse instead of a flat spray.
	 */
	public static void leapLandingExplosion(LivingEntity golem) {
		Level level = golem.level();
		RandomSource random = golem.getRandom();
		double yaw = Math.toRadians(golem.yBodyRot);
		double height = golem.getBbHeight() / 2.0;
		double width = golem.getBbWidth();
		for (int i = 0; i < 8; i++) {
			// each side sweeps its own half of the circle, the feet are a body width apart
			double leftAngle = yaw + Math.toRadians((random.nextFloat() - 0.5) * 160);
			double rightAngle = yaw + Math.PI + Math.toRadians((random.nextFloat() - 0.5) * 160);
			Vec3 leftStart = new Vec3(golem.getX() + Math.cos(yaw) * width, golem.getY() + 0.5F,
					golem.getZ() + Math.sin(yaw) * width);
			Vec3 rightStart = new Vec3(golem.getX() - Math.cos(yaw) * width, golem.getY() + 0.5F,
					golem.getZ() - Math.sin(yaw) * width);
			Vec3 leftEnd = leftStart.add(Math.cos(leftAngle) * random.nextFloat() * 15,
					random.nextFloat() * height, Math.sin(leftAngle) * random.nextFloat() * 15);
			Vec3 rightEnd = rightStart.add(Math.cos(rightAngle) * random.nextFloat() * 15,
					random.nextFloat() * height, Math.sin(rightAngle) * random.nextFloat() * 15);
			boolean heavy = i % 3 == 0;
			float size = (heavy ? 0.25F : 0.125F) + (random.nextFloat() - 0.5F) * 0.05F;
			bolt(golem, random, leftStart, leftEnd, heavy, size);
			bolt(golem, random, rightStart, rightEnd, heavy, size);
		}
		level.addParticle(new ParticleRing.RingData(ParticleInit.BIG_RING.get(), 0F, (float) (Math.PI / 2D), 10,
				0.52F, 0.94F, 1F, 1F, 50F, false, ParticleRing.EnumRingBehavior.GROW),
				golem.getX(), golem.getY() + 0.1F, golem.getZ(), 0, 0, 0);
	}

	private static void bolt(LivingEntity golem, RandomSource random, Vec3 start, Vec3 end, boolean heavy, float size) {
		LightningBolt bolt = new LightningBolt.LightningBoltBuilder().count(1)
				.color(BOLT_COLORS[random.nextInt(BOLT_COLORS.length)])
				.lifespan(heavy ? 7 : 6)
				.fadeFunction(heavy ? LightningBolt.FadeFunction.NONE : LightningBolt.FadeFunction.fade(0.6F))
				.size(size).parallelNoise(0.12F).spreadFactor(0.08F).build(start, end, random);
		ClientProxy.LIGHTNING_RENDER.update(golem, bolt);
	}

	public static void leapShock(LivingEntity golem) {		Level level = golem.level();
		RandomSource random = golem.getRandom();
		for (int i = 0; i < 6; i++) {
			Vec3 start = golem.position().offsetRandom(random, 1.0F);
			Vec3 end = start.add((random.nextFloat() - 0.5F) * 6.0, 1.0 + random.nextFloat(),
					(random.nextFloat() - 0.5F) * 6.0);
			Vector4f color = random.nextBoolean() ? BOLT_COLORS[0] : BOLT_COLORS[1];
			LightningBolt bolt = new LightningBolt.LightningBoltBuilder().count(1).color(color).lifespan(6)
					.fadeFunction(LightningBolt.FadeFunction.fade(0.75F))
					.size(0.08F).parallelNoise(0.08F).spreadFactor(0.1F).build(start, end, random);
			ClientProxy.LIGHTNING_RENDER.update(golem, bolt);
		}
	}

}