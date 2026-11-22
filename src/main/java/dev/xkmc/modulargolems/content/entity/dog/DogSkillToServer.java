package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

@SerialClass
public record DogSkillToServer(ResourceLocation modifier) implements SerialPacketBase<DogSkillToServer> {

	public static DogSkillToServer of(ChargeHelper.Instance ins) {
		return new DogSkillToServer(((GolemModifier) ins.modifier()).getRegistryName());
	}

	public static DogSkillToServer of(EarthquakeHelper.Instance ins) {
		return new DogSkillToServer(((GolemModifier) ins.modifier()).getRegistryName());
	}

	@Override
	public void handle(Player player) {
		GolemModifier mod = GolemTypes.MODIFIERS.get().get(modifier);
		if (mod == null) return;
		var mount = player.getVehicle();
		if (!(mount instanceof DogGolemEntity dog)) return;
		var lv = dog.getModifiers().getOrDefault(mod, 0);
		if (lv <= 0) return;
		if (mod instanceof ChargeHelper.Modifier charge) {
			if (dog.isInRangedMode()) return;
			var ins = new ChargeHelper.Instance(dog, charge, lv);
			if (ins.isValid() && charge.canCharge(dog, lv)) {
				dog.chargeManager().startMountCharge(ins);
				ins.addCD();
			}
			return;
		}
		if (mod instanceof EarthquakeHelper.Modifier jump) {
			var ins = new EarthquakeHelper.Instance(dog, jump, lv);
			if (ins.isValid()) {
				dog.jumpAttack = ins;
				dog.jumpAttackDelay = 10;
				ins.addCD();
				ins.addCD();
				if (jump.useServerJump()) {
					ins.modifier().performJump(dog, ins.lv());
				}
			}
		}
	}

}
