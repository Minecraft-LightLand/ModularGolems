package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.modifier.base.GolemModifier;
import dev.xkmc.modulargolems.content.modifier.special.ChargeHelper;
import dev.xkmc.modulargolems.content.modifier.special.EarthquakeHelper;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class DogSkillToServer extends SerialPacketBase {

	@SerialClass.SerialField
	public ResourceLocation modifier;

	public static DogSkillToServer of(ChargeHelper.Instance ins) {
		var ans = new DogSkillToServer();
		ans.modifier = ((GolemModifier) ins.modifier()).getRegistryName();
		return ans;
	}

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
		var mount = sp.getVehicle();
		if (!(mount instanceof DogGolemEntity dog)) return;
		var lv = dog.getModifiers().getOrDefault(mod, 0);
		if (lv <= 0) return;
		if (mod instanceof ChargeHelper.Modifier charge) {
			if (dog.isInRangedMode()) return;
			var ins = new ChargeHelper.Instance(dog, charge, lv);
			if (ins.isValid() && charge.canCharge(dog, lv)) {
				dog.startMountCharge(ins);
				ins.addCD();
			}
			return;
		}
		if (mod instanceof EarthquakeHelper.Modifier jump) {
			var ins = new EarthquakeHelper.Instance(dog, jump, lv);
			if (ins.isValid()) {
				dog.jumpAttack = ins;
				ins.addCD();
				dog.jumpAttackDelay = 10;
				// 默认由客户端本地预测负责起跳；技能显式声明要服务端执行时走这里
				if (jump.useServerJump()) {
					ins.modifier().performJump(dog, ins.lv());
				}
			}
		}
	}

}
