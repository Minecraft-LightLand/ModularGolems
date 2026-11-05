package dev.xkmc.modulargolems.compat.materials.alexscaves.client;

import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

import java.util.function.Predicate;

/**
 * 磁力极化 / 阻尼盾的循环音效。
 * <p>
 * 直接复用 AC 阻尼盾的两条循环音（scarlet = 排斥、azure = 吸引），但<b>不能</b>继承 AC 的
 * {@code ItemTickableSound}：它的 {@code tick()} 从 {@code getItemInHand} 取物品栈并要求
 * {@code isUsingItem()}，磁力傀儡手里没有盾牌，会立刻 {@code stop()}。所以这里独立实现。
 * <p>
 * 用 {@code keepAlive} 判定这条音效该不该继续——磁力极化看 modifier，阻尼盾看是否还握着盾。
 */
public class GolemPolarizeSound extends AbstractTickableSoundInstance {

	private final AbstractGolemEntity<?, ?> golem;
	private final boolean azure;
	private final Predicate<AbstractGolemEntity<?, ?>> keepAlive;

	public GolemPolarizeSound(AbstractGolemEntity<?, ?> golem, boolean azure,
			Predicate<AbstractGolemEntity<?, ?>> keepAlive) {
		super(azure ? ACSoundRegistry.RESITOR_SHIELD_AZURE_LOOP.get()
				: ACSoundRegistry.RESITOR_SHIELD_SCARLET_LOOP.get(),
				SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
		this.golem = golem;
		this.azure = azure;
		this.keepAlive = keepAlive;
		this.looping = true;
		this.delay = 0;
		this.attenuation = Attenuation.LINEAR;
		this.volume = 0.7F;
		this.pitch = 1.0F;
		this.x = golem.getX();
		this.y = golem.getY();
		this.z = golem.getZ();
	}

	public boolean isAzure() {
		return azure;
	}

	/** {@code AbstractTickableSoundInstance#stop()} 是 protected，外部调用需要这个包装。 */
	public void stopSound() {
		this.stop();
	}

	@Override
	public void tick() {
		if (!golem.isAlive() || golem.isRemoved() || !keepAlive.test(golem)) {
			this.stop();
			return;
		}
		this.x = golem.getX();
		this.y = golem.getY();
		this.z = golem.getZ();
	}

	@Override
	public boolean canStartSilent() {
		return true;
	}

}
