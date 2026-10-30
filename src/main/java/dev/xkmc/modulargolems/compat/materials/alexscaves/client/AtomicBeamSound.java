package dev.xkmc.modulargolems.compat.materials.alexscaves.client;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

import java.util.HashMap;
import java.util.Map;

/**
 * Looping atomic breath beam sound, mirroring Alex's Caves' {@code TremorzillaBeamSound}. Golems have
 * no charge up wind up, so the looping sound replaces the start and end sounds entirely.
 * <p>
 * Only ever touched from the client tick of the modifier.
 */
public final class AtomicBeamSound extends AbstractTickableSoundInstance {

	private static final Map<Integer, AtomicBeamSound> ACTIVE = new HashMap<>();

	private final AbstractGolemEntity<?, ?> golem;

	private AtomicBeamSound(AbstractGolemEntity<?, ?> golem) {
		super(ACSoundRegistry.TREMORZILLA_BEAM_LOOP.get(), SoundSource.HOSTILE, SoundInstance.createUnseededRandom());
		this.golem = golem;
		this.attenuation = Attenuation.LINEAR;
		this.x = golem.getX();
		this.y = golem.getEyeY();
		this.z = golem.getZ();
		this.looping = true;
		this.delay = 0;
		this.volume = 0.0F;
	}

	/**
	 * Starts or stops the looping sound depending on whether the golem is currently firing.
	 */
	public static void update(AbstractGolemEntity<?, ?> golem) {
		if (golem.isBreathAttack()) {
			if (!ACTIVE.containsKey(golem.getId())) {
				AtomicBeamSound sound = new AtomicBeamSound(golem);
				ACTIVE.put(golem.getId(), sound);
				Minecraft.getInstance().getSoundManager().play(sound);
			}
		} else {
			stop(golem);
		}
	}

	/**
	 * Stops the looping sound of a golem that stopped firing.
	 */
	public static void stop(AbstractGolemEntity<?, ?> golem) {
		AtomicBeamSound sound = ACTIVE.remove(golem.getId());
		if (sound != null) sound.stop();
	}

	@Override
	public boolean canPlaySound() {
		return golem.isAlive() && !golem.isSilent() && golem.isBreathAttack();
	}

	@Override
	public void tick() {
		if (canPlaySound()) {
			this.x = golem.getX();
			this.y = golem.getEyeY();
			this.z = golem.getZ();
			this.volume = golem.getBeamProgress(1.0F);
			return;
		}
		this.volume = 0.0F;
		this.stop();
		ACTIVE.remove(this.golem.getId(), this);
	}

	@Override
	public boolean canStartSilent() {
		return true;
	}

}
