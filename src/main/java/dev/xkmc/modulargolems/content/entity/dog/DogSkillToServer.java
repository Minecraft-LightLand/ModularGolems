package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class DogSkillToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public ResourceLocation modifier;

	public static DogSkillToServer of(EarthquakeHelper.Instance ins) {
		var ans = new DogSkillToServer();
		ans.modifier = ((GolemModifier) ins.modifier()).getRegistryName();
		return ans;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var sp = context.getSender();
		if (sp == null) return;
		if (!GolemTypes.MODIFIERS.get().containsKey(modifier)) return;
		var mod = GolemTypes.MODIFIERS.get().getValue(modifier);
		if (!(mod instanceof EarthquakeHelper.Modifier jump)) return;
		var mount = sp.getVehicle();
		if (mount instanceof DogGolemEntity dog) {
			var lv = dog.getModifiers().getOrDefault(mod, 0);
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
