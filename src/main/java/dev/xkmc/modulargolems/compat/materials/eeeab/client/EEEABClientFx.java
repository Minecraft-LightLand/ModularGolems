package dev.xkmc.modulargolems.compat.materials.eeeab.client;

import com.eeeab.eeeabsmobs.client.ClientProxy;
import com.eeeab.eeeabsmobs.client.particle.ParticleDust;
import com.eeeab.eeeabsmobs.client.particle.ParticleRing;
import com.eeeab.eeeabsmobs.client.particle.lib.AdvancedBlockParticle;
import com.eeeab.eeeabsmobs.client.particle.lib.AdvancedParticleBase;
import com.eeeab.eeeabsmobs.client.particle.lib.AnimData;
import com.eeeab.eeeabsmobs.client.particle.lib.ParticleRotation;
import com.eeeab.eeeabsmobs.client.particle.lib.component.ParticleComponent.PropertyControl;
import com.eeeab.eeeabsmobs.client.particle.lib.component.ParticleComponent.PropertyControl.EnumParticleProperty;
import com.eeeab.eeeabsmobs.client.particle.lib.component.RibbonComponent;
import com.eeeab.eeeabsmobs.client.particle.lib.component.RibbonComponent.PropertyOverLength;
import com.eeeab.eeeabsmobs.client.particle.lib.component.RibbonComponent.PropertyOverLength.EnumRibbonProperty;
import com.eeeab.eeeabsmobs.client.particle.lib.data.AdvancedBlockParticleData;
import com.eeeab.eeeabsmobs.client.particle.lib.data.AdvancedParticleData;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import com.eeeab.eeeabsmobs.client.particle.lib.component.ParticleComponent;
import com.eeeab.eeeabsmobs.client.particle.util.ModParticleUtils;
import com.eeeab.eeeabsmobs.client.render.LightningBolt;
import com.eeeab.eeeabsmobs.sever.init.ParticleInit;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
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

	private static final Vector4f[] COLORS = {
			new Vector4f(1F, 0.88F, 0.48F, 1F), new Vector4f(0.52F, 0.94F, 1F, 1F)};

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

	/**
	 * Exact port of {@code EntityRealmWarden.doLeapLandingEffect}: BIG_RING, a GLOW flash, a dust annular
	 * outburst, block particles and the two-layer bowl burst. {@code scale/dustSpeed/effectScale/angles}
	 * are the boss's own parameter sets: (60, 1.0, 1.4, {45,25}) on the initial landing and
	 * (120, 1.4, 1.2, {55,25}) on the slam.
	 */
	public static void leapLandingFull(LivingEntity golem, float scale, float dustSpeed, float effectScale,
			double[] angles, boolean secondPhase) {
		Level level = golem.level();
		RandomSource random = golem.getRandom();
		// 落地特效锚到地面：客户端实体插值，落地帧可能还在半空，直接用 getY() 会画在空中
		Vec3 ground = EarthquakeHelper.findFloor(level, golem.position(), 8);
		Vec3 pos = new Vec3(golem.getX(), (ground == null ? golem.getY() : ground.y) + 0.1, golem.getZ());
		Vector4f color = secondPhase ? COLORS[1] : COLORS[0];
		level.addParticle(new ParticleRing.RingData(ParticleInit.BIG_RING.get(), 0F, (float) (Math.PI / 2D), 11,
						color.x, color.y, color.z, 1F, 70F, false, ParticleRing.EnumRingBehavior.GROW),
				pos.x, pos.y, pos.z, 0, 0, 0);
		flashing(golem, pos, scale, color.x, color.y, color.z, secondPhase);
		ParticleDust.DustData dustData = new ParticleDust.DustData(ParticleInit.DUST.get(), 32F, 25,
				ParticleDust.EnumDustBehavior.GROW, 0.79F);
		ModParticleUtils.annularParticleOutburst(level, 20 * dustSpeed, dustData, pos.x, pos.y, pos.z,
				1.6F * dustSpeed, 0.6F);
		ModParticleUtils.blockParticlesAround(level, pos.x, pos.y, pos.z, 15,
				0.75D * effectScale, 1.5D * effectScale, 0.1D * effectScale, 0.15D * effectScale,
				0.45D * effectScale, 0.75D * effectScale, -0.2D, 0.1D,
				(bp, state) -> blockParticleData(random, state, pos, 1.5D));
		double[] colors = {COLORS[0].x, COLORS[0].y, COLORS[0].z, COLORS[0].w};
		ModParticleUtils.multiLayerBowlParticles(level, pos, 3, new int[]{8, 10},
				new double[]{2, 3}, new double[]{1.8, 2.2}, angles, colors, null, 0.55F);
	}

	private static void flashing(LivingEntity golem, Vec3 pos, float scale, float finalR, float finalG,
			float finalB, boolean secondPhase) {
		if (secondPhase) scale *= 1.2F;
		AdvancedParticleBase.spawnParticle(golem.level(), ParticleInit.GLOW.get(), pos.x, pos.y, pos.z, 0, 0, 0,
				true, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 5, true, false, false, new ParticleComponent[]{
						new PropertyControl(EnumParticleProperty.SCALE, AnimData.startAndEnd(15F, scale), false),
						new PropertyControl(EnumParticleProperty.ALPHA, AnimData.startAndEnd(1F, 0.1F), false),
						new PropertyControl(EnumParticleProperty.RED, new AnimData.KeyTrack(new float[]{1F, finalR}, new float[]{0F, 0.5F}), false),
						new PropertyControl(EnumParticleProperty.GREEN, new AnimData.KeyTrack(new float[]{1F, finalG}, new float[]{0F, 0.5F}), false),
						new PropertyControl(EnumParticleProperty.BLUE, new AnimData.KeyTrack(new float[]{1F, finalB}, new float[]{0F, 0.5F}), false),
				});
	}

	private static AdvancedBlockParticleData blockParticleData(RandomSource random, BlockState state, Vec3 pos,
			double scale) {
		return AdvancedBlockParticle.createBlockParticleData(state, null, pos.x, pos.y, pos.z,
				(random.nextFloat() * 0.5F + 0.5F) * 2 / scale, 0.6D, 0.6D, 0.6D, 1D, 0.98D, 40, false, true,
				new ParticleComponent[]{new ParticleComponent.FreeFallSimulator(1.5F)});
	}

	private static AdvancedParticleData ribbonData(double airDiffusionSpeed, float scale) {
		return AdvancedParticleBase.createParticleData(ParticleInit.ADV_ORB.get(),
				new ParticleRotation.EulerAngles(0, 0, 0), 0, 0, 0, 0, 0, airDiffusionSpeed, 3, true, false,
				new ParticleComponent[]{
						new RibbonComponent(ParticleInit.FLAT_RIBBON.get(), 4, 0, 0, 0, scale,
								COLORS[0].x, COLORS[0].y, COLORS[0].z, 0.8F, true, true,
								new ParticleComponent[]{
										new PropertyOverLength(EnumRibbonProperty.ALPHA, AnimData.KeyTrack.startAndEnd(1F, 0F)),
										new PropertyOverLength(EnumRibbonProperty.SCALE, AnimData.KeyTrack.startAndEnd(0.1F, 1F)),
								}, false)
				}, false);
	}

	private static Vec3 randomOffset(Vec3 center, RandomSource random, double size) {
		return center.add((random.nextDouble() - 0.5) * 2 * size, 0, (random.nextDouble() - 0.5) * 2 * size);
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
	public static void leapLandingExplosion(LivingEntity golem, boolean secondPhase) {
		Level level = golem.level();
		RandomSource random = golem.getRandom();
		if (!secondPhase) {
			// phase one: fifteen ribbon particles flung outward within size 30, the boss's else branch
			AdvancedParticleData data = ribbonData(0.52F, 0.3F);
			int size = 30;
			Vec3 ground = EarthquakeHelper.findFloor(level, golem.position(), 8);
			Vec3 center = new Vec3(golem.getX(), ground == null ? golem.getY() : ground.y, golem.getZ());
			for (int i = 0; i < 15; i++) {
				Vec3 rp = randomOffset(center, random, size);
				double dx = rp.x - center.x, dz = rp.z - center.z;
				double len = Math.sqrt(dx * dx + dz * dz);
				if (len > 0) {
					dx /= len;
					dz /= len;
				}
				double angleRad = Math.toRadians(Mth.randomBetweenInclusive(random, 75, 90));
				double speed = Mth.randomBetween(random, 1.5F, 2.5F) * (0.8 + random.nextDouble() * 0.4);
				double hs = speed * Math.cos(angleRad);
				double vs = speed * Math.sin(angleRad);
				level.addParticle(data, rp.x, center.y, rp.z, dx * hs, vs, dz * hs);
			}
			return;
		}
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