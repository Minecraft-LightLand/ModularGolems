package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public record DogSkillToServer(Identifier modifier) implements SerialPacketBase<DogSkillToServer> {


	public static DogSkillToServer of(EarthquakeHelper.Instance ins) {
		return new DogSkillToServer(((GolemModifier) ins.modifier()).getRegistryName());
	}

	@Override
	public void handle(Player sp) {
		if (!GolemTypes.MODIFIERS.get().containsKey(modifier)) return;
		var mod = GolemTypes.MODIFIERS.get().getValue(modifier);
		if (!(mod instanceof EarthquakeHelper.Modifier jump)) return;
		var mount = sp.getVehicle();
		if (mount instanceof DogGolemEntity dog) {
			int lv = dog.getModifiers().getOrDefault(mod, 0);
			if (lv > 0) {
				var ins = new EarthquakeHelper.Instance(dog, jump, lv);
				if (ins.isValid()) {
					dog.jumpAttack = ins;
					ins.addCD();
					dog.jumpAttackDelay = 10;
				}
			}
		}
	}

}
