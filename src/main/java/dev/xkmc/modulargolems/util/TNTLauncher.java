package dev.xkmc.modulargolems.util;

import dev.xkmc.mob_weapon_api.util.Estimator;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TNTLauncher {

	@Nullable
	public static PrimedTnt getTNTEntity(LivingEntity owner, Vec3 pos, LivingEntity target) {
		PrimedTnt e = new PrimedTnt(owner.level(), pos.x(), pos.y(),
				pos.z(), owner);
		Estimator.EstiResult er = setAim(owner, target, 3, 128, e, 0.04, 0.02, 80);
		if (er.getType() == Estimator.EstiType.ZERO) {
			e.setDeltaMovement(er.getVec());
			e.setFuse((int) Math.round(er.getT()));
		} else return null;
		return e;
	}

	public static Estimator.EstiResult setAim(LivingEntity pl, LivingEntity target, double velo, double reach, Entity e, double g, double k, int maxt) {
		if (target.position().distanceTo(pl.position()) < velo)
			return Estimator.EstiType.CLOSE;
		Vec3 mot = target.getDeltaMovement();
		Vec3 tar = target.position().add(0, target.getBbHeight() / 2, 0);
		Vec3 pos = e.position();
		Estimator.EstiResult er = new Estimator(g, k, pos, velo, maxt, tar, mot).getAnswer();
		if (er.getType() == Estimator.EstiType.ZERO)
			return er;
		return Estimator.EstiType.FAIL;
	}

	public static void setDire(float xRot, float yRot, float velo, net.minecraft.world.entity.Entity ent) {
		float f = -Mth.sin(yRot * ((float) Math.PI / 180F)) * Mth.cos(xRot * ((float) Math.PI / 180F));
		float f1 = -Mth.sin(xRot * ((float) Math.PI / 180F));
		float f2 = Mth.cos(yRot * ((float) Math.PI / 180F)) * Mth.cos(xRot * ((float) Math.PI / 180F));
		Vec3 v = new Vec3(f, f1, f2).normalize().scale(velo);
		ent.setDeltaMovement(v);
		float f3 = Mth.sqrt((float) v.horizontalDistanceSqr());
		ent.setYRot((float) (Mth.atan2(v.x, v.z) * (180F / (float) Math.PI)));
		ent.setXRot((float) (Mth.atan2(v.y, f3) * (180F / (float) Math.PI)));
		ent.xRotO = ent.getXRot();
		ent.yRotO = ent.getYRot();
	}

}
