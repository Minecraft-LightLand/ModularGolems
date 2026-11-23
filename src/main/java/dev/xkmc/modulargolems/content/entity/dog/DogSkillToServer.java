package dev.xkmc.modulargolems.content.entity.dog;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
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
	// 技能所属的傀儡（坐骑或背上的乘客）的实体 id
	@SerialClass.SerialField
	public int owner = -1;
	// 由服务端自行从"坐骑 + 乘客"里挑一个可用冲锋（客户端拿不到真实冷却，不能自己判定）
	@SerialClass.SerialField
	public boolean auto;

	public static DogSkillToServer autoCharge() {
		var ans = new DogSkillToServer();
		ans.auto = true;
		return ans;
	}

	public static DogSkillToServer of(ChargeHelper.Instance ins) {
		var ans = new DogSkillToServer();
		ans.modifier = ((GolemModifier) ins.modifier()).getRegistryName();
		ans.owner = ins.owner().getId();
		return ans;
	}

	public static DogSkillToServer of(EarthquakeHelper.Instance ins) {
		var ans = new DogSkillToServer();
		ans.modifier = ((GolemModifier) ins.modifier()).getRegistryName();
		ans.owner = ins.owner().getId();
		return ans;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		var sp = context.getSender();
		if (sp == null) return;
		var mount = sp.getVehicle();
		if (!(mount instanceof AbstractGolemEntity<?, ?> golem)) return;
		if (!(golem instanceof MountChargeManager.View view)) return;
		if (auto) {
			// 服务端按真实冷却挑一个冲锋：坐骑优先，再按乘客顺序
			var picked = dev.xkmc.modulargolems.content.modifier.special.RideSkillPick.charge(golem);
			if (picked == null) return;
			if (golem.isInRangedMode()) return;
			view.chargeManager().startMountCharge(picked);
			picked.addCD();
			return;
		}
		if (!GolemTypes.MODIFIERS.get().containsKey(modifier)) return;
		var mod = GolemTypes.MODIFIERS.get().getValue(modifier);
		// 技能可能属于坐骑本身，也可能属于骑在它身上的傀儡乘客：优先用客户端指定的所有者
		AbstractGolemEntity<?, ?> owner = null;
		int lv = 0;
		var byId = sp.level().getEntity(this.owner);
		if (byId instanceof AbstractGolemEntity<?, ?> cand && (cand == golem || golem.getPassengers().contains(cand))) {
			int l = cand.getModifiers().getOrDefault(mod, 0);
			if (l > 0) {
				owner = cand;
				lv = l;
			}
		}
		if (owner == null) {
			int l = golem.getModifiers().getOrDefault(mod, 0);
			if (l > 0) {
				owner = golem;
				lv = l;
			}
		}
		if (owner == null) {
			owner = null;
			for (var e : golem.getPassengers()) {
				if (e instanceof AbstractGolemEntity<?, ?> p) {
					int l = p.getModifiers().getOrDefault(mod, 0);
					if (l > 0) {
						owner = p;
						lv = l;
						break;
					}
				}
			}
			if (owner == null) return;
		}
		if (mod instanceof ChargeHelper.Modifier charge) {
			if (golem.isInRangedMode()) return;
			var ins = new ChargeHelper.Instance(owner, charge, lv);
			if (ins.isValid() && charge.canCharge(owner, lv)) {
				view.chargeManager().startMountCharge(ins);
				ins.addCD();
			}
			return;
		}
		if (mod instanceof EarthquakeHelper.Modifier jump) {
			var ins = new EarthquakeHelper.Instance(owner, jump, lv);
			if (ins.isValid()) {
				view.chargeManager().startMountJump(ins);
			}
		}
	}

}